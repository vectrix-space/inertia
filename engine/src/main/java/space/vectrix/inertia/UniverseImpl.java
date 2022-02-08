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
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.flare.SyncMap;
import space.vectrix.flare.fastutil.Int2ObjectSyncMap;
import space.vectrix.inertia.component.Component;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.entity.Entity;
import space.vectrix.inertia.entity.EntityFunction;
import space.vectrix.inertia.entity.EntityStash;
import space.vectrix.inertia.injection.InjectionStructure;
import space.vectrix.inertia.system.Dependency;
import space.vectrix.inertia.system.System;
import space.vectrix.inertia.util.CustomIterator;
import space.vectrix.inertia.util.IndexCounter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

import static java.util.Objects.requireNonNull;

public final class UniverseImpl implements Universe {
  /**
   * Stores the processors by class type.
   */
  private final Map<Class<? extends System>, SystemEntry> systems = SyncMap.of(IdentityHashMap::new, 20);

  /**
   * Store the entity stashes.
   */
  private final Set<EntityStash> stashes = SyncMap.setOf(WeakHashMap::new, 20);

  /**
   * Stored by unique {@code int} component type index and {@link Class} component
   * type, with a counter.
   */
  private final Int2ObjectMap<ComponentType> types = Int2ObjectSyncMap.hashmap(50);
  private final Map<Class<?>, ComponentType> typeClasses = SyncMap.of(IdentityHashMap::new, 50);
  private final IndexCounter typeCounter = IndexCounter.counter("types", this.types);

  /**
   * Stored by unique {@code int} component index.
   */
  private final Int2ObjectMap<ComponentEntry> components = Int2ObjectSyncMap.hashmap(100);
  private final IndexCounter componentCounter = IndexCounter.counter("components", this.components);

  /**
   * Stored by unique {@code int} entity index.
   */
  private final Int2ObjectMap<EntityEntry> entities = Int2ObjectSyncMap.hashmap(100);
  private final IndexCounter entityCounter = IndexCounter.counter("entities", this.entities);

  /**
   * Remove queues, for destroying components and entities on sanitization.
   */
  private final Deque<Integer> entityRemovals = new ConcurrentLinkedDeque<>();
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
  public boolean hasEntity(final @NonNull Entity entity) {
    requireNonNull(entity, "entity");
    final EntityEntry entry = this.entities.get(entity.index());
    return entry != null && Objects.equals(entry.entity(), entity);
  }

  @Override
  public boolean hasComponent(final @NonNegative int entity, final @NonNull ComponentType type) {
    requireNonNull(type, "type");
    final EntityEntry entry = this.entities.get(entity);
    return entry != null && entry.get(type) != null;
  }

  @Override
  public boolean hasComponent(final @NonNull Entity entity, final @NonNull ComponentType type) {
    requireNonNull(entity, "entity");
    return this.hasComponent(entity.index(), type);
  }

  @Override
  public boolean hasComponent(@NonNegative int entity, @NonNull Class<?> type) {
    requireNonNull(type, "type");
    final EntityEntry entry = this.entities.get(entity);
    return entry != null && entry.get(type) != null;
  }

  @Override
  public boolean hasComponent(final @NonNull Entity entity, final @NonNull Class<?> type) {
    requireNonNull(entity, "entity");
    return this.hasComponent(entity.index(), type);
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
    final EntityEntry entry = this.entities.get(entity);
    if(entry != null) return entry.entity();
    return null;
  }

  @Override
  public <T extends Entity> @Nullable T getEntity(final @NonNegative int entity, final @NonNull Class<? super T> target) {
    final EntityEntry entry = this.entities.get(entity);
    if(entry != null) return entry.entity(target);
    return null;
  }

  @Override
  public <T> @Nullable T getComponent(final @NonNegative int entity, final @NonNull ComponentType type) {
    requireNonNull(type, "type");
    final EntityEntry entry = this.entities.get(entity);
    return entry != null ? entry.component(type) : null;
  }

  @Override
  public @Nullable <T> T getComponent(final @NonNull Entity entity, final @NonNull ComponentType type) {
    requireNonNull(entity, "entity");
    return this.getComponent(entity.index(), type);
  }

  @Override
  public <T> @Nullable T getComponent(final @NonNegative int entity, final @NonNull Class<? super T> type) {
    requireNonNull(type, "type");
    final EntityEntry entry = this.entities.get(entity);
    return entry != null ? entry.component(type) : null;
  }

  @Override
  public <T> @Nullable T getComponent(final @NonNull Entity entity, final @NonNull Class<? super T> type) {
    requireNonNull(entity, "entity");
    return this.getComponent(entity.index(), type);
  }

  @Override
  public <T extends System> void addSystem(final @NonNull T system) {
    Universe.checkActive(this);
    requireNonNull(system, "system");
    this.systems.computeIfAbsent(system.getClass(), key -> {
      try {
        if(this.factory != null) {
          final InjectionStructure structure = this.factory.create(key);
          this.injectSystem(system, structure);
          return new SystemEntry(system, structure);
        }
      } catch(final Throwable throwable) {
        throwable.printStackTrace();
      }

      return new SystemEntry(system, null);
    });
  }

  @Override
  public @NonNull Entity createEntity() {
    return this.createEntity(Entity.simple());
  }

  @Override
  public <T extends Entity> @NonNull T createEntity(final @NonNull EntityFunction<T> function) {
    Universe.checkActive(this);
    requireNonNull(function, "function");
    return this.entityCounter.next(index -> {
      final T entity = function.apply(this, index);
      this.entities.put(index, new EntityEntry(entity));
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
      final EntityEntry entityEntry = this.entities.get(entity.index());
      if(entityEntry == null) throw new IllegalArgumentException("Entity does not exist!");
      ComponentEntry entry = entityEntry.get(type);
      if(entry != null) return entry.component();
      final T componentReference = (T) this.createInstance(type.type());
      entry = new ComponentEntry(type, index, componentReference);
      entityEntry.add(entry);
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
    this.entityRemovals.offerFirst(entity);
  }

  @Override
  public void removeEntity(final @NonNull Entity entity) {
    Universe.checkActive(this);
    requireNonNull(entity, "entity");
    this.entityRemovals.offerFirst(entity.index());
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
    final EntityEntry entityEntry = this.entities.get(index);
    if(entityEntry == null) return;
    entityEntry.entries().forEach(entry -> this.entityComponentRemovals.enqueue(IntIntPair.of(index, entry.type().index())));
  }

  @Override
  public @NonNull CustomIterator<System> systems() {
    return CustomIterator.of(this.systems.values().iterator(), SystemEntry::left, system -> this.removeSystem(system.getClass()));
  }

  @Override
  public @NonNull CustomIterator<ComponentType> types() {
    return CustomIterator.of(this.types.values().iterator());
  }

  @Override
  public @NonNull <T extends Entity> CustomIterator<T> entities(final @NonNull Class<? super T> type) {
    return CustomIterator.<EntityEntry, T, Throwable>of(this.entities.values().iterator(), entry -> entry.entity(type), this::removeEntity);
  }

  @Override
  public @NonNull CustomIterator<Entity> entities() {
    return CustomIterator.of(this.entities.values().iterator(), EntityEntry::entity, this::removeEntity);
  }

  @Override
  public @NonNull CustomIterator<Entity> removingEntities() {
    return CustomIterator.of(this.entityRemovals.iterator(), this::getEntity, entity -> this.entityRemovals.remove(entity.index()));
  }

  @Override
  public <T> @NonNull CustomIterator<T> components(final @NonNull ComponentType type) {
    requireNonNull(type, "type");
    return CustomIterator.of(this.components.values().iterator(), entry -> entry.type().index() == type.index() ? entry.component() : null);
  }

  @Override
  public @NonNull CustomIterator<Object> components(final @NonNull Entity entity) {
    requireNonNull(entity, "entity");
    final EntityEntry entityEntry = this.entities.get(entity.index());
    if(entityEntry == null) return CustomIterator.empty();
    return CustomIterator.of(entityEntry.entries().iterator(), ComponentEntry::component);
  }

  @Override
  public @NonNull CustomIterator<Object> components() {
    return CustomIterator.of(this.components.values().iterator(), ComponentEntry::component);
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

  public @NonNull EntityStash addStash(final @NonNull EntityStash stash) {
    Universe.checkActive(this);
    requireNonNull(stash, "stash");
    this.stashes.add(stash);
    return stash;
  }

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
      if(system != null && structure != null) {
        final InjectionStructure.Entry injectionEntry = structure.injectors().get(type.type());
        if(injectionEntry == null) continue;
        final Dependency dependency = injectionEntry.annotation();
        if(!dependency.optional()) continue;
        try {
          injectionEntry.target().inject(system, type);
        } catch(final Throwable throwable) {
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

  private void destroyEntity(final @NonNegative int entity) {
    final EntityEntry entityEntry = this.entities.remove(entity);
    if(entityEntry != null) {
      entityEntry.entries().forEach(entry -> this.components.remove(entry.index()));
      this.stashes.forEach(stash -> stash.remove(entity));
    }
  }

  private void destroyComponent(final @NonNegative int entity, final @NonNegative int type) {
    final EntityEntry entityEntry = this.entities.get(entity);
    final ComponentEntry componentEntry;
    if(entityEntry != null && (componentEntry = entityEntry.remove(type)) != null) {
      this.components.remove(componentEntry.index());
    }
  }

  private void clear() {
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
      // 1. Initialize
      for(final SystemEntry systemEntry : systems) {
        final System system = systemEntry.left();
        try {
          if(!system.initialized()) system.initialize();
        } catch(final Throwable throwable) {
          errors.add(throwable);
        }
      }
      // 2. Prepare
      for(final SystemEntry systemEntry : systems) {
        final System system = systemEntry.left();
        try {
          if(system.initialized()) system.prepare();
        } catch(final Throwable throwable) {
          errors.add(throwable);
        }
      }
      // 3. Execute
      for(final SystemEntry systemEntry : systems) {
        final System system = systemEntry.left();
        try {
          if(system.initialized()) system.execute();
        } catch(final Throwable throwable) {
          errors.add(throwable);
        }
      }
      // 4. Sanitize
      for(final SystemEntry systemEntry : systems) {
        final System system = systemEntry.left();
        try {
          if(system.initialized()) system.sanitize();
        } catch(final Throwable throwable) {
          errors.add(throwable);
        }
      }
      this.sanitize();
      return new TickImpl(time, errors);
    }
  }

  private void sanitize() {
    while(!this.entityComponentRemovals.isEmpty()) {
      final IntIntPair pair = this.entityComponentRemovals.dequeue();
      this.destroyComponent(pair.firstInt(), pair.secondInt());
    }
    while(!this.entityRemovals.isEmpty()) {
      this.destroyEntity(this.entityRemovals.pollFirst());
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

  /* package */ static class SystemEntry extends ObjectObjectImmutablePair<System, InjectionStructure> implements Comparable<SystemEntry> {
    private static final long serialVersionUID = 0L;

    public SystemEntry(final @NonNull System left, final @Nullable InjectionStructure right) {
      super(left, right);
    }

    @Override
    public int compareTo(final @NonNull SystemEntry other) {
      return this.left.compareTo(other.left());
    }
  }

  /* package */ static final class EntityEntry {
    private final Int2ObjectMap<ComponentEntry> components = new Int2ObjectOpenHashMap<>();
    private final Entity entityReference;

    /* package */ EntityEntry(final @NonNull Entity entityReference) {
      this.entityReference = entityReference;
    }

    public @NonNegative int index() {
      return this.entityReference.index();
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> @Nullable T entity(final @NonNull Class<? super T> type) {
      if(!type.isAssignableFrom(this.entityReference.getClass())) return null;
      return (T) this.entityReference;
    }

    public @NonNull Entity entity() {
      return this.entityReference;
    }

    public <T> @Nullable T component(final @NonNull ComponentType type) {
      final ComponentEntry entry = this.components.get(type.index());
      return entry != null ? entry.component() : null;
    }

    public <T> @Nullable T component(final @NonNull Class<? super T> type) {
      final ComponentEntry entry = this.get(type);
      return entry != null ? entry.component() : null;
    }

    public @Nullable ComponentEntry get(final @NonNull ComponentType type) {
      return this.components.get(type.index());
    }

    public @Nullable ComponentEntry get(final @NonNull Class<?> type) {
      for(final ComponentEntry entry : this.components.values()) {
        if(type.isAssignableFrom(entry.component().getClass())) return entry;
      }
      return null;
    }

    public void add(final @NonNull ComponentEntry entry) {
      this.components.put(entry.type().index(), entry);
    }

    public @Nullable ComponentEntry remove(final @NonNegative int type) {
      return this.components.remove(type);
    }

    public Collection<ComponentEntry> entries() {
      return Collections.unmodifiableCollection(this.components.values());
    }
  }

  /* package */ static final class ComponentEntry {
    private final ComponentType componentType;
    private final int componentIndex;
    private final Object componentReference;

    /* package */ ComponentEntry(final @NonNull ComponentType componentType,
                                 final @NonNegative int componentIndex,
                                 final @NonNull Object componentReference) {
      this.componentType = componentType;
      this.componentIndex = componentIndex;
      this.componentReference = componentReference;
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
  }
}
