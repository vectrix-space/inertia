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

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.entity.Entity;
import space.vectrix.inertia.entity.EntityFunction;
import space.vectrix.inertia.injection.InjectionStructure;
import space.vectrix.inertia.system.System;
import space.vectrix.inertia.util.CustomIterator;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents the universe of entities, components and processors.
 *
 * @since 0.3.0
 */
public interface Universe {
  /**
   * Ensures the specified {@link Universe} is active, otherwise
   * throws an {@link InactiveUniverseException}.
   *
   * @param universe the universe to check
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  static void checkActive(final @Nullable Universe universe) {
    if(universe == null || !universe.active()) throw new InactiveUniverseException(universe);
  }

  /**
   * Returns a new universe.
   *
   * @return the new universe
   * @since 0.3.0
   */
  static @NonNull Universe create() {
    return Universes.create();
  }

  /**
   * Returns the universe with the specified {@code index}
   * if it exists.
   *
   * @param index the universe index
   * @return the universe, if present
   * @since 0.3.0
   */
  static @Nullable Universe get(final @NonNegative int index) {
    return Universes.get(index);
  }

  /**
   * Returns an {@link Iterator} of all the universes.
   *
   * @return an iterator of universes
   * @since 0.3.0
   */
  static @NonNull Iterator<Universe> universes() {
    return Universes.universes();
  }

  /**
   * Returns {@code true} if the universe has not been destroyed.
   *
   * @return true if the universe is active, otherwise false
   * @since 0.3.0
   */
  boolean active();

  /**
   * Returns the unique {@code int} index for this universe.
   *
   * @return the universe index
   * @since 0.3.0
   */
  @NonNegative int index();

  /**
   * Ticks the {@link System}s in this universe.
   *
   * @return the tick result
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  @NonNull Tick tick();

  /**
   * Sets the {@link InjectionStructure} for system dependency injection.
   *
   * @param factory the structure factory
   * @since 0.3.0
   */
  void injector(final InjectionStructure.@Nullable Factory factory);

  /**
   * Returns {@code true} if the universe contains the specified {@link Entity},
   * otherwise {@code false}.
   *
   * @param entity the entity
   * @return true if the entity exists, otherwise false
   * @since 0.3.0
   */
  boolean hasEntity(final @NonNull Entity entity);

  /**
   * Returns {@code true} if the specified {@code int} entity index
   * contains the {@link ComponentType} if it exists, otherwise
   * {@code false}.
   *
   * @param entity the entity index
   * @param type the component type
   * @return true if the component exists, otherwise false
   * @since 0.3.0
   */
  boolean hasComponent(final @NonNegative int entity, final @NonNull ComponentType type);

  /**
   * Returns {@code true} if the specified {@link Entity} contains the
   * {@link ComponentType} if it exists, otherwise {@code false}.
   *
   * @param entity the entity
   * @param type the component type
   * @return true if the component exists, otherwise false
   * @since 0.3.0
   */
  boolean hasComponent(final @NonNull Entity entity, final @NonNull ComponentType type);

  /**
   * Returns {@code true} if the specified {@code int} entity index contains
   * a component inheriting the specified {@link Class} type, otherwise
   * {@code false}.
   *
   * <p>You should prefer {@link Universe#hasComponent(int, ComponentType)}
   * for speed over this method as much as possible.</p>
   *
   * @param entity the entity index
   * @param type the class type
   * @return true if the component exists, otherwise false
   * @since 0.3.0
   */
  boolean hasComponent(final @NonNegative int entity, final @NonNull Class<?> type);

  /**
   * Returns {@code true} if the specified {@link Entity} contains a component
   * inheriting the specified {@link Class} type, otherwise {@code false}.
   *
   * <p>You should prefer {@link Universe#hasComponent(Entity, ComponentType)}
   * for speed over this method as much as possible.</p>
   *
   * @param entity the entity
   * @param type the class type
   * @return true if the component exists, otherwise false
   * @since 0.3.0
   */
  boolean hasComponent(final @NonNull Entity entity, final @NonNull Class<?> type);

  /**
   * Returns the {@code T} system for the specified {@link Class} if
   * it exists, otherwise {@code null}.
   *
   * @param target the target class
   * @param <T> the target instance type
   * @return the system, if present
   * @since 0.3.0
   */
  <T extends System> @Nullable T getSystem(final @NonNull Class<T> target);

  /**
   * Returns the {@link ComponentType} with the specified {@code int}
   * index if it exists, otherwise {@code null}.
   *
   * @param type the type index
   * @return the component type, if present
   * @since 0.3.0
   */
  @Nullable ComponentType getType(final @NonNegative int type);

  /**
   * Returns the {@link ComponentType} for the specified {@link String}
   * identifier if it exists, otherwise {@code null}.
   *
   * @param id the type identifier
   * @return the component type, if present
   * @since 0.3.0
   */
  @Nullable ComponentType getType(final @NonNull String id);

  /**
   * Returns the {@link ComponentType} for the specified {@link Class}
   * if it exists, otherwise {@code null}.
   *
   * @param target the target class
   * @return the component type, if present
   * @since 0.3.0
   */
  @Nullable ComponentType getType(final @NonNull Class<?> target);

  /**
   * Returns the {@link Entity} with the specified {@code int}
   * entity index if it exists, otherwise {@code null}.
   *
   * @param entity the entity index
   * @return the entity, if present
   * @since 0.3.0
   */
  @Nullable Entity getEntity(final @NonNegative int entity);

  /**
   * Returns the {@code T} entity with the specified {@code int}
   * entity index and {@link Class} target if it exists, otherwise
   * {@code null}.
   *
   * @param entity the entity index
   * @param target the entity class type
   * @param <T> the entity type
   * @return the entity, if present
   * @since 0.3.0
   */
  <T extends Entity> @Nullable T getEntity(final @NonNegative int entity, final @NonNull Class<? super T> target);

  /**
   * Returns the {@code T} component instance for the specified {@code int} entity
   * index and {@link ComponentType} if it exists, otherwise {@code null}.
   *
   * @param entity the entity index
   * @param type the component type
   * @param <T> the component instance type
   * @return the component, if present
   * @since 0.3.0
   */
  <T> @Nullable T getComponent(final @NonNegative int entity, final @NonNull ComponentType type);

  /**
   * Returns the {@code T} component instance for the specified {@link Entity}
   * index and {@link ComponentType} if it exists, otherwise {@code null}.
   *
   * @param entity the entity index
   * @param type the component type
   * @param <T> the component instance type
   * @return the component, if present
   * @since 0.3.0
   */
  <T> @Nullable T getComponent(final @NonNull Entity entity, final @NonNull ComponentType type);

  /**
   * Returns the {@code T} component instance for the specified {@code int}
   * entity index and specified {@link Class} type if it exists, otherwise
   * {@code null}.
   *
   * <p>You should prefer {@link Universe#getComponent(int, ComponentType)}
   * for speed over this method as much as possible.</p>
   *
   * @param entity the entity index
   * @param type the component class type
   * @param <T> the component type
   * @return the component, if present
   * @since 0.3.0
   */
  <T> @Nullable T getComponent(final @NonNegative int entity, final @NonNull Class<? super T> type);

  /**
   * Returns the {@code T} component instance for the specified {@link Entity}
   * and specified {@link Class} type if it exists, otherwise {@code null}.
   *
   * <p>You should prefer {@link Universe#getComponent(int, ComponentType)}
   * for speed over this method as much as possible.</p>
   *
   * @param entity the entity
   * @param type the component class type
   * @param <T> the component type
   * @return the component, if present
   * @since 0.3.0
   */
  <T> @Nullable T getComponent(final @NonNull Entity entity, final @NonNull Class<? super T> type);

  /**
   * Adds the {@code T} system to this universe.
   *
   * @param system the system
   * @param <T> the system type
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  <T extends System> void addSystem(final @NonNull T system);

  /**
   * Returns a new {@link Entity}.
   *
   * @return the new entity
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  @NonNull Entity createEntity();

  /**
   * Returns a new {@code T} entity instance for the specified
   * {@link EntityFunction}.
   *
   * @param function the entity function
   * @param <T> the entity type
   * @return the new entity
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  <T extends Entity> @NonNull T createEntity(final @NonNull EntityFunction<T> function);

  /**
   * Returns a new {@code T} component instance for the specified {@link Entity}
   * index and {@link ComponentType} if it exists, otherwise {@code null}.
   *
   * @param entity the entity
   * @param type the component type
   * @param <T> the component instance type
   * @return the new component
   * @throws InactiveUniverseException if the universe is not active
   * @throws IllegalArgumentException if the entity does not exist
   * @since 0.3.0
   */
  <T> @NonNull T addComponent(final @NonNull Entity entity, final @NonNull ComponentType type);

  /**
   * Removes the specified {@link System} from this universe.
   *
   * @param system the system
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  void removeSystem(final @NonNull Class<? extends System> system);

  /**
   * Marks the specified {@code int} entity for removal.
   *
   * @param entity the entity index
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  void removeEntity(final @NonNegative int entity);

  /**
   * Marks the specified {@link Entity} for removal.
   *
   * @param entity the entity
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  void removeEntity(final @NonNull Entity entity);

  /**
   * Marks the specified {@link ComponentType} for the {@link Entity}
   * to be removed.
   *
   * @param entity the entity
   * @param type the component type
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  void removeComponent(final @NonNull Entity entity, final @NonNull ComponentType type);

  /**
   * Marks all the components on the specified {@link Entity} to be
   * removed.
   *
   * @param entity the entity
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  void clearComponents(final @NonNull Entity entity);

  /**
   * Returns a {@link CustomIterator} of {@link System}s in this universe.
   *
   * @return an iterator of systems
   * @since 0.3.0
   */
  @NonNull CustomIterator<System> systems();

  /**
   * Returns a {@link CustomIterator} of {@link ComponentType}s in this universe.
   *
   * @return an iterator of component types
   * @since 0.3.0
   */
  @NonNull CustomIterator<ComponentType> types();

  /**
   * Returns a {@link CustomIterator} of {@link Entity}s of the specified
   * {@link Class} type in this universe.
   *
   * @param type the entity class type
   * @param <T> the entity type
   * @return an iterator of entities
   * @since 0.3.0
   */
  <T extends Entity> @NonNull CustomIterator<T> entities(final @NonNull Class<? super T> type);

  /**
   * Returns a {@link CustomIterator} of {@link Entity}s in this universe.
   *
   * @return an iterator of entities
   * @since 0.3.0
   */
  @NonNull CustomIterator<Entity> entities();

  /**
   * Returns a {@link CustomIterator} of {@link Entity}s in this universe
   * that are queued to be removed of the specified {@link Class} type.
   *
   * <p>Calling {@link CustomIterator#remove()} will remove the entity
   * from the removal queue and allow it to continue existing in the
   * universe.</p>
   *
   * @param type the entity class type
   * @param <T> the entity type
   * @return an iterator of entities that are going to be removed
   * @since 0.3.0
   */
  <T extends Entity> @NonNull CustomIterator<T> removingEntities(final @NonNull Class<? super T> type);

  /**
   * Returns a {@link CustomIterator} of {@link Entity}s in this universe
   * that are queued to be removed.
   *
   * <p>Calling {@link CustomIterator#remove()} will remove the entity
   * from the removal queue and allow it to continue existing in the
   * universe.</p>
   *
   * @return an iterator of entities that are going to be removed
   * @since 0.3.0
   */
  @NonNull CustomIterator<Entity> removingEntities();

  /**
   * Returns a {@link CustomIterator} of {@code T} component instances for
   * the specified {@link ComponentType}.
   *
   * @param type the component type
   * @param <T> the component instance type
   * @return an iterator of components
   * @since 0.3.0
   */
  <T> @NonNull CustomIterator<T> components(final @NonNull ComponentType type);

  /**
   * Returns a {@link CustomIterator} of component instances for the specified
   * {@link Entity}.
   *
   * @param entity the entity
   * @return an iterator of components
   * @since 0.3.0
   */
  @NonNull CustomIterator<Object> components(final @NonNull Entity entity);

  /**
   * Returns a {@link CustomIterator} of component instances in this universe.
   *
   * @return an iterator of components
   * @since 0.3.0
   */
  @NonNull CustomIterator<Object> components();

  /**
   * Destroys the universe.
   *
   * @throws InactiveUniverseException if the universe is not active
   * @since 0.3.0
   */
  void destroy();

  /**
   * The result of a tick.
   *
   * @since 0.3.0
   */
  interface Tick {
    /**
     * Returns the {@code int} time this tick ran on.
     *
     * @return the time
     * @since 0.3.0
     */
    @NonNegative int time();

    /**
     * Returns a {@link Collection} of {@link Throwable}s encountered on
     * this tick.
     *
     * @return a collection of throwables
     * @since 0.3.0
     */
    @NonNull Collection<Throwable> errors();
  }
}
