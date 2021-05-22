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
package space.vectrix.inertia.component;

import static java.util.Objects.requireNonNull;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.flare.SyncMap;
import space.vectrix.flare.fastutil.Int2ObjectSyncMap;
import space.vectrix.flare.fastutil.Long2ObjectSyncMap;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.annotation.Component;
import space.vectrix.inertia.annotation.Inject;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.injection.InjectionMethod;
import space.vectrix.inertia.injection.InjectionStructure;
import space.vectrix.inertia.processor.ProcessingSystem;
import space.vectrix.inertia.util.InvalidContextException;
import space.vectrix.inertia.util.MapFunctions;
import space.vectrix.inertia.util.counter.IndexCounter;
import space.vectrix.inertia.util.version.Version;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public final class ComponentContainerImpl implements ComponentContainer, ProcessingSystem {
  // Component Types
  private final Int2ObjectSyncMap<ComponentType> componentTypes = Int2ObjectSyncMap.hashmap(500);
  private final IndexCounter componentCounter = IndexCounter.counter("componentType", this.componentTypes);
  private final Map<Class<?>, ComponentType> componentTypesGroup = SyncMap.of(IdentityHashMap::new, 500);
  private final Map<String, ComponentType> componentTypesNamed = SyncMap.hashmap(500);
  private final Int2ObjectSyncMap<InjectionStructure> componentStructures = Int2ObjectSyncMap.hashmap(500);

  // Components
  private final LongSet componentRemovals = Long2ObjectSyncMap.hashset(50);
  private final Long2ObjectMap<Object> components = Long2ObjectSyncMap.hashmap(1000);
  private final Int2ObjectSyncMap<Int2ObjectSyncMap<Object>> componentHolders = Int2ObjectSyncMap.hashmap(500);

  private final InjectionStructure.Factory injectionStructure;
  private final InjectionMethod.Factory injectionMethod;
  private final Universe universe;

  public ComponentContainerImpl(final @NonNull Universe universe,
                                final InjectionStructure.@NonNull Factory injectionStructure,
                                final InjectionMethod.@NonNull Factory injectionMethod) {
    this.injectionStructure = injectionStructure;
    this.injectionMethod = injectionMethod;
    this.universe = universe;
  }

  @Override
  public @Nullable ComponentType getType(final int index) {
    return this.componentTypes.get(index);
  }

  @Override
  public @Nullable ComponentType getType(final @NonNull Class<?> type) {
    requireNonNull(type, "type");
    return this.componentTypesGroup.get(type);
  }

  @Override
  public @Nullable ComponentType getType(final @NonNull String identifier) {
    requireNonNull(identifier, "identifier");
    return this.componentTypesNamed.get(identifier);
  }

  @Override
  public @Nullable ComponentType resolveType(final @NonNull Class<?> type) {
    requireNonNull(type, "type");
    return this.resolve(type);
  }

  @Override
  public @NonNull Collection<ComponentType> types() {
    return this.componentTypes.values();
  }

  @Override
  public boolean containsComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    final Version holderVersion = requireNonNull(holder, "holder").version();
    final Version componentVersion = requireNonNull(componentType, "componentType").version();

    if(!holderVersion.belongs(this.universe, componentVersion)) return false;
    return this.components.containsKey(this.getCombinedIndex(holderVersion.index(), componentVersion.index()));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @Nullable T getComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    final Version holderVersion = requireNonNull(holder, "holder").version();
    final Version componentVersion = requireNonNull(componentType, "componentType").version();

    if(!holderVersion.belongs(this.universe, componentVersion)) return null;
    return (T) this.components.get(this.getCombinedIndex(holderVersion.index(), componentVersion.index()));
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull <T> Optional<T> getPresentComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    final Version holderVersion = requireNonNull(holder, "holder").version();
    final Version componentVersion = requireNonNull(componentType, "componentType").version();

    if(!holderVersion.belongs(this.universe, componentVersion)) return Optional.empty();
    return Optional.ofNullable((T) this.components.get(this.getCombinedIndex(holderVersion.index(), componentVersion.index())));
  }

  @Override
  public <T> @NonNull T addComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    requireNonNull(holder, "holder");
    requireNonNull(componentType, "componentType");
    return this.create(holder, componentType, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @Nullable T removeComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    final Version holderVersion = requireNonNull(holder, "holder").version();
    final Version componentVersion = requireNonNull(componentType, "componentType").version();
    final long version = this.getCombinedIndex(holderVersion.index(), componentVersion.index());

    if(!holderVersion.belongs(this.universe, componentVersion) && !this.componentRemovals.add(version)) return null;
    return (T) this.components.get(version);
  }

  @Override
  public void clearComponents(final @NonNull Holder holder) {
    final Version holderVersion = requireNonNull(holder, "holder").version();
    if(!holderVersion.belongs(this.universe)) return;

    final Int2ObjectSyncMap<Object> components = this.componentHolders.get(holderVersion.index());
    if(components == null) return;

    for(final int componentIndex : components.keySet()) {
      final long version = this.getCombinedIndex(holderVersion.index(), componentIndex);
      this.componentRemovals.add(version);
    }
  }

  @Override
  public void process() throws Throwable {
    final LongIterator iterator = this.componentRemovals.longIterator();
    while(iterator.hasNext()) {
      final long index = iterator.nextLong();
      final int component = (int) (index >> 32);
      final int holder = (int) (index & 0xFFFF);
      this.components.remove(index);

      final Int2ObjectSyncMap<Object> components = this.componentHolders.get(holder);
      if(components.size() <= 1) {
        this.componentHolders.remove(holder);
      } else {
        components.remove(component);
      }

      iterator.remove();
    }
  }

  private @Nullable ComponentType resolve(final @NonNull Class<?> type) {
    final Component annotation = type.getAnnotation(Component.class);
    if(annotation == null) throw new IllegalArgumentException("Target type must have a component annotation!");

    return this.componentTypesGroup.computeIfAbsent(type, ignored0 -> this.componentTypesNamed.computeIfAbsent(annotation.id(), ignored1 ->
      this.componentCounter.next(index -> {
        final ComponentType componentType = this.componentTypes.computeIfAbsent(index, keyIndex -> new ComponentTypeImpl(
          Version.version(keyIndex, this.universe.index()),
          annotation.id(),
          annotation.name(),
          type
        ));

        this.componentStructures.putIfAbsent(index, this.injectionStructure.create(type, this.injectionMethod));
        return componentType;
      })
    ));
  }

  @SuppressWarnings("unchecked")
  private <T> @NonNull T create(final @NonNull Holder holder,
                                final @NonNull ComponentType componentType,
                                final @Nullable ComponentType parentType) {
    final Version holderVersion = holder.version();
    final Version componentVersion = componentType.version();
    final Class<?> componentClass = componentType.type();
    if(!holderVersion.belongs(this.universe, componentVersion)) throw new InvalidContextException("universe", "Universe does not contain the specified holder or component type!");

    final Int2ObjectSyncMap<Object> components = this.componentHolders.computeIfAbsent(holderVersion.index(), ignored -> Int2ObjectSyncMap.hashmap(500));
    final T componentInstance = (T) components.computeIfAbsent(componentVersion.index(), key -> this.createInstance(componentClass));
    this.components.put(this.getCombinedIndex(holderVersion.index(), componentVersion.index()), componentInstance);

    final InjectionStructure structure = this.componentStructures.get(componentVersion.index());
    if(structure != null) {
      for(final Map.Entry<Class<?>, InjectionStructure.Entry> entry : structure.injectors().entrySet()) {
        final Class<?> target = entry.getKey();
        final InjectionStructure.Entry injectionEntry = entry.getValue();

        // Holder Injection
        if(target.isInstance(holder)) {
          this.injectMember(injectionEntry.method(), componentInstance, holder);
          continue;
        }

        // Component Injection
        final Inject annotation = injectionEntry.annotation();
        final ComponentType dependency = MapFunctions.getOr(this.componentTypesGroup, target, this::resolve);
        final Version dependencyVersion = dependency.version();

        if(parentType != null && dependencyVersion.equals(componentVersion)) continue;

        final Object dependencyInstance = MapFunctions.getOr(components, dependencyVersion.index(), key -> {
          if (!annotation.optional()) return this.create(holder, dependency, componentType);
          return null;
        });

        if(dependencyInstance != null) {
          this.injectMember(injectionEntry.method(), componentInstance, dependencyInstance);
        }
      }
    }
    return componentInstance;
  }

  private void injectMember(final @NonNull InjectionMethod injector, final @NonNull Object target, final @NonNull Object member) {
    try {
      injector.member(target, member);
    } catch(final Throwable throwable) {
      throw new IllegalStateException("Unable to inject member.", throwable);
    }
  }

  private Object createInstance(final @NonNull Class<?> componentClass) {
    try {
      return componentClass.getDeclaredConstructor().newInstance();
    } catch(final Throwable exception) {
      throw new IllegalStateException("Unable to instantiate component.", exception);
    }
  }

  private long getCombinedIndex(final int holder, final int component) {
    return ((long) component << 32) + holder;
  }
}
