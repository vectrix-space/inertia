/*
 * This file is part of inertia, licensed under the MIT License (MIT).
 *
 * Copyright (c) vectrix.space <https://vectrix.space/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package space.vectrix.inertia;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.PriorityQueues;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueues;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.flare.SyncMap;
import space.vectrix.flare.fastutil.Int2ObjectSyncMap;
import space.vectrix.inertia.component.Component;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.entity.Entity;
import space.vectrix.inertia.entity.EntityFunction;
import space.vectrix.inertia.injection.InjectionStructure;
import space.vectrix.inertia.system.Dependency;
import space.vectrix.inertia.system.System;
import space.vectrix.inertia.util.IndexCounter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

import static java.util.Objects.requireNonNull;

public final class UniverseImpl implements Universe {
  /**
   * Stores the processors by class type.
   */
  private final Map<Class<? extends System>, SystemEntry> systems = SyncMap.of(IdentityHashMap::new, 20);

  /**
   * Stored by unique {@code int} component type index and {@link Class} component
   * type, with a counter.
   */
  private final Int2ObjectMap<ComponentType> types = Int2ObjectSyncMap.hashmap(50);
  private final Map<Class<?>, ComponentType> typeClasses = SyncMap.of(IdentityHashMap::new, 50);
  private final IndexCounter typeCounter = IndexCounter.counter("types", this.types);

  /**
   * Stored by the unique {@code int} entity identifier and unique {@code int}
   * component type identifier.
   */
  private final Int2ObjectMap<Int2ObjectMap<ComponentEntry>> entityComponents = Int2ObjectSyncMap.hashmap(100);

  /**
   * Stored by unique {@code int} component index.
   */
  private final Int2ObjectMap<ComponentEntry> components = Int2ObjectSyncMap.hashmap(100);
  private final IndexCounter componentCounter = IndexCounter.counter("components", this.components);

  /**
   * Stored by unique {@code int} entity index.
   */
  private final Int2ObjectMap<Entity> entities = Int2ObjectSyncMap.hashmap(100);
  private final IndexCounter entityCounter = IndexCounter.counter("entities", this.entities);

  /**
   * Remove queues, for destroying components and entities on sanitization.
   */
  private final IntPriorityQueue entityRemovals = IntPriorityQueues.synchronize(new IntArrayFIFOQueue());
  private final PriorityQueue<IntIntPair> entityComponentRemovals = PriorityQueues.synchronize(new ObjectArrayFIFOQueue<>());

  private final AtomicInteger time = new AtomicInteger();
  private final Object lock = new Object();
  private final int index;

  private InjectionStructure.@Nullable Factory factory;
  private boolean active = true;

  /* package */ UniverseImpl(final @NonNegative int index) {
    this.index = index;
  }

  @Override
  public boolean active() {
    return this.active;
  }

  @Override
  public @NonNegative int index() {
    return this.index;
  }

  @Override
  public @NonNull Tick tick() {
    Universe.checkActive(this);
    return this.update();
  }

  @Override
  public void injector(final InjectionStructure.@Nullable Factory factory) {
    this.factory = factory;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends System> @Nullable T getSystem(final @NonNull Class<T> target) {
    requireNonNull(target, "target");
    final Pair<System, InjectionStructure> systemEntry = this.systems.get(target);
    return systemEntry != null ? (T) systemEntry.left() : null;
  }

  @Override
  public @Nullable ComponentType getType(final @NonNegative int type) {
    return this.types.get(type);
  }

  @Override
  public @Nullable ComponentType getType(final @NonNull Class<?> target) {
    requireNonNull(target, "target");
    return this.typeClasses.get(target);
  }

  @Override
  public @Nullable Entity getEntity(final @NonNegative int entity) {
    return this.entities.get(entity);
  }

  @Override
  public @Nullable Entity getEntityFromComponent(final @NonNegative int component) {
    final ComponentEntry entry = this.components.get(component);
    if(entry != null) return entry.entity();
    return null;
  }

  @Override
  public boolean hasComponent(final @NonNull Entity entity, final @NonNull ComponentType type) {
    requireNonNull(entity, "entity");
    requireNonNull(type, "type");
    final Int2ObjectMap<ComponentEntry> components = this.entityComponents.get(entity.index());
    if(components != null) {
      final ComponentEntry entry = components.get(type.index());
      return entry != null;
    }
    return false;
  }

  @Override
  public @Nullable Object getComponent(final @NonNegative int component) {
    final ComponentEntry entry = this.components.get(component);
    if(entry != null) return entry.component();
    return null;
  }

  @Override
  public @Nullable <T> T getComponent(final @NonNull Entity entity, final @NonNull ComponentType type) {
    requireNonNull(entity, "entity");
    requireNonNull(type, "type");
    final Int2ObjectMap<ComponentEntry> components = this.entityComponents.get(entity.index());
    if(components != null) {
      final ComponentEntry entry = components.get(type.index());
      return entry != null ? entry.component() : null;
    }
    return null;
  }

  @Override
  public <T extends System> void addSystem(final @NonNull T system) {
    Universe.checkActive(this);
    requireNonNull(system, "system");
    this.systems.computeIfAbsent(system.getClass(), key -> {
      final SystemEntry systemEntry = new SystemEntry(system, null);
      if(this.factory != null) {
        final InjectionStructure structure = this.factory.create(key);
        this.injectSystem(system, structure);
        systemEntry.right(structure);
      }
      return systemEntry;
    });
  }

  @Override
  public @NonNull Entity createEntity() {
    Universe.checkActive(this);
    return this.createEntity(Entity.simple());
  }

  @Override
  public <T extends Entity> @NonNull T createEntity(final @NonNull EntityFunction<T> function) {
    Universe.checkActive(this);
    requireNonNull(function, "function");
    return this.entityCounter.next(index -> {
      final T entity = function.apply(this, index);
      this.entities.put(index, entity);
      return entity;
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @NonNull T addComponent(final @NonNull Entity entity, final @NonNull ComponentType type) {
    Universe.checkActive(this);
    requireNonNull(entity, "entity");
    requireNonNull(type, "type");
    return this.componentCounter.next(index -> {
      Int2ObjectMap<ComponentEntry> components = this.entityComponents.get(entity.index());
      ComponentEntry entry;
      if(components != null) {
        if((entry = components.get(type.index())) != null) {
          return entry.component();
        }
      } else {
        components = Int2ObjectSyncMap.hashmap(100);
      }
      final T componentReference = (T) this.createInstance(type.type());
      entry = new ComponentEntry(type, index, componentReference, entity);
      components.put(type.index(), entry);
      this.components.put(index, entry);
      return componentReference;
    });
  }

  @Override
  public void removeSystem(final @NonNull Class<? extends System> system) {
    Universe.checkActive(this);
    this.systems.remove(system);
  }

  @Override
  public void removeEntity(final @NonNegative int entity) {
    Universe.checkActive(this);
    this.entityRemovals.enqueue(entity);
  }

  @Override
  public void removeEntity(final @NonNull Entity entity) {
    Universe.checkActive(this);
    requireNonNull(entity, "entity");
    this.entityRemovals.enqueue(entity.index());
  }

  @Override
  public void removeComponent(final @NonNull Entity entity, final @NonNull ComponentType type) {
    Universe.checkActive(this);
    requireNonNull(entity, "entity");
    requireNonNull(type, "type");
    this.entityComponentRemovals.enqueue(IntIntPair.of(entity.index(), type.index()));
  }

  @Override
  public void clearComponents(final @NonNull Entity entity) {
    Universe.checkActive(this);
    requireNonNull(entity, "entity");
    final int index = entity.index();
    final Int2ObjectMap<ComponentEntry> components = this.entityComponents.get(entity.index());
    if(components != null) {
      components.values().forEach(entry -> this.entityComponentRemovals.enqueue(IntIntPair.of(index, entry.type().index())));
    }
  }

  @Override
  public @NonNull Iterator<System> systems() {
    return new SystemIterator(this.systems.values().iterator());
  }

  @Override
  public @NonNull Collection<Entity> entities() {
    return Collections.unmodifiableCollection(this.entities.values());
  }

  @Override
  public <T> @NonNull Collection<T> components(final @NonNull ComponentType type) {
    requireNonNull(type, "type");
    final List<T> collection = new ArrayList<>();
    this.components.values().forEach(entry -> {
      if(entry.type().index() == type.index()) {
        collection.add(entry.component());
      }
    });
    return collection;
  }

  @Override
  public @NonNull Collection<Object> components(final @NonNull Entity entity) {
    requireNonNull(entity, "entity");
    final List<Object> collection = new ArrayList<>();
    final Int2ObjectMap<ComponentEntry> components = this.entityComponents.get(entity.index());
    if(components != null) {
      components.values().forEach(entry -> collection.add(entry.component()));
    }
    return collection;
  }

  @Override
  public @NonNull Collection<Object> components() {
    final List<Object> collection = new ArrayList<>();
    this.components.values().forEach(entry -> collection.add(entry.component()));
    return collection;
  }

  @Override
  public void destroy() {
    Universe.checkActive(this);
    synchronized(this.lock) {
      this.active = false;
      this.clear();
      Universes.remove(this.index());
    }
  }

  /* package */ void deactivate() {
    Universe.checkActive(this);
    synchronized(this.lock) {
      this.active = false;
      this.clear();
    }
  }

  // Internal

  public @NonNull ComponentType resolveComponent(final @NonNull Class<?> target, final @NonNull IntFunction<ComponentType> function) {
    Universe.checkActive(this);
    requireNonNull(target, "target");
    requireNonNull(function, "function");
    return this.typeClasses.computeIfAbsent(target, ignored -> this.typeCounter.next(index -> {
      final ComponentType componentType = function.apply(index);
      this.types.put(index, componentType);
      if(this.factory != null) this.injectSystems(componentType);
      return componentType;
    }));
  }

  private void injectSystems(final @NonNull ComponentType type) {
    requireNonNull(type, "type");
    for(final Map.Entry<Class<? extends System>, SystemEntry> entry : this.systems.entrySet()) {
      final SystemEntry systemEntry = entry.getValue();
      final System system = systemEntry.left();
      final InjectionStructure structure = systemEntry.right();
      if(structure != null) {
        final InjectionStructure.Entry injectionEntry = structure.injectors().get(type.type());
        if (system == null || injectionEntry == null) continue;
        final Dependency dependency = injectionEntry.annotation();
        if (!dependency.optional()) continue;
        try {
          injectionEntry.target().inject(system, type);
        } catch (final Throwable throwable) {
          throw new IllegalStateException(
            "Unable to inject component type '"
              + type.type().getSimpleName() + "' into system '"
              + system.getClass().getSimpleName() + "'."
          );
        }
      }
    }
  }

  private <T extends System> void injectSystem(final @NonNull T system, final @NonNull InjectionStructure structure) {
    requireNonNull(system, "system");
    requireNonNull(structure, "structure");
    for(final Map.Entry<Class<?>, InjectionStructure.Entry> entry : structure.injectors().entrySet()) {
      final Class<?> target = entry.getKey();
      final InjectionStructure.Entry injectionEntry = entry.getValue();
      if(!target.isAnnotationPresent(Component.class)) continue;
      final Dependency dependency = injectionEntry.annotation();
      ComponentType componentType = this.getType(target);
      if(componentType == null && !dependency.optional()) componentType = ComponentType.create(this, target);
      if(componentType != null) {
        try {
          injectionEntry.target().inject(system, componentType);
        } catch(final Throwable throwable) {
          throw new IllegalStateException(
            "Unable to inject component type '"
            + target.getSimpleName() + "' into system '"
            + system.getClass().getSimpleName() + "'."
          );
        }
      }
    }
  }

  private boolean destroyEntity(final @NonNegative int entity) {
    final Int2ObjectMap<ComponentEntry> components = this.entityComponents.remove(entity);
    if(components != null) {
      components.values().forEach(entry -> this.components.remove(entry.index()));
      return this.entities.remove(entity) != null;
    }
    return false;
  }

  private boolean destroyComponent(final @NonNegative int entity, final @NonNegative int type) {
    final Int2ObjectMap<ComponentEntry> components = this.entityComponents.get(entity);
    if(components != null) {
      final ComponentEntry entry = components.remove(type);
      if(components.isEmpty()) this.entityComponents.remove(entity);
      return entry != null && this.components.remove(entry.index()) != null;
    }
    return false;
  }

  private void clear() {
    this.entityComponents.clear();
    this.components.clear();
    this.entities.clear();
    this.systems.clear();
  }

  // Utility

  private Tick update() {
    synchronized(this.lock) {
      final List<SystemEntry> systems = new ArrayList<>(this.systems.values());
      final List<Throwable> errors = new ArrayList<>();
      final int time = this.time.getAndIncrement();
      Collections.sort(systems);
      for(final SystemEntry systemEntry : systems) {
        final System system = systemEntry.left();
        boolean run = true;
        try {
          if (!system.initialized()) system.initialize();
        } catch (final Throwable throwable) {
          errors.add(throwable);
          run = false;
        }
        if(system.initialized()) {
          try {
            if(run) system.prepare();
          } catch (final Throwable throwable) {
            errors.add(throwable);
            run = false;
          }
          try {
            if(run) system.execute();
          } catch (final Throwable throwable) {
            errors.add(throwable);
            run = false;
          }
          try {
            if(run) system.sanitize();
          } catch (final Throwable throwable) {
            errors.add(throwable);
          }
        }
      }
      this.sanitize();
      return new TickImpl(time, errors);
    }
  }

  private void sanitize() {
    while(!this.entityRemovals.isEmpty()) {
      this.destroyEntity(this.entityRemovals.dequeueInt());
    }
    while(!this.entityComponentRemovals.isEmpty()) {
      final IntIntPair pair = this.entityComponentRemovals.dequeue();
      this.destroyComponent(pair.firstInt(), pair.secondInt());
    }
  }

  private @NonNull Object createInstance(final @NonNull Class<?> componentClass) {
    try {
      return componentClass.getDeclaredConstructor().newInstance();
    } catch(final Throwable exception) {
      throw new IllegalStateException("Unable to instantiate component.", exception);
    }
  }

  /* package */ static final class TickImpl implements Tick {
    private final int time;
    private final Collection<Throwable> errors;

    /* package */ TickImpl(final @NonNegative int time, final @NonNull Collection<Throwable> errors) {
      this.time = time;
      this.errors = errors;
    }

    @Override
    public @NonNegative int time() {
      return this.time;
    }

    @Override
    public @NonNull Collection<Throwable> errors() {
      return this.errors;
    }
  }

  /* package */ static final class ComponentEntry {
    private final ComponentType componentType;
    private final int componentIndex;
    private final Object componentReference;
    private final Entity entityReference;

    /* package */ ComponentEntry(final @NonNull ComponentType componentType,
                                 final @NonNegative int componentIndex,
                                 final @NonNull Object componentReference,
                                 final @NonNull Entity entityReference) {
      this.componentType = componentType;
      this.componentIndex = componentIndex;
      this.componentReference = componentReference;
      this.entityReference = entityReference;
    }

    public @NonNull ComponentType type() {
      return this.componentType;
    }

    public @NonNegative int index() {
      return this.componentIndex;
    }

    @SuppressWarnings("unchecked")
    public <T> @NonNull T component() {
      final Class<?> clazz = this.componentType.type();
      return (T) clazz.cast(this.componentReference);
    }

    public @NonNull Entity entity() {
      return this.entityReference;
    }
  }

  /* package */ static class SystemEntry extends ObjectObjectMutablePair<System, InjectionStructure> implements Comparable<SystemEntry> {
    public SystemEntry(final @NonNull System left, final @Nullable InjectionStructure right) {
      super(left, right);
    }

    @Override
    public int compareTo(final @NonNull SystemEntry other) {
      return this.left.compareTo(other.left());
    }
  }

  /* package */ static class SystemIterator implements Iterator<System> {
    private final Iterator<SystemEntry> iterator;
    private SystemEntry next;

    /* package */ SystemIterator(final @NonNull Iterator<SystemEntry> iterator) {
      this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
      return this.iterator.hasNext();
    }

    @Override
    public @NonNull System next() {
      return (this.next = this.iterator.next()).left();
    }

    @Override
    public void remove() {
      if(this.next == null) throw new IllegalStateException("remove() called before next()");
      this.iterator.remove();
    }
  }
}
