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
package space.vectrix.inertia.entity;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.UniverseImpl;
import space.vectrix.inertia.util.CustomIterator;

import static java.util.Objects.requireNonNull;

/**
 * A stash of entities.
 *
 * @since 0.3.0
 */
public interface EntityStash extends Iterable<Entity> {
  /**
   * Returns a new entity stash for the specified universe.
   *
   * @param universe the universe
   * @return a new entity stash
   * @since 0.3.0
   */
  static @NonNull EntityStash create(final @NonNull Universe universe) {
    requireNonNull(universe, "universe");
    final UniverseImpl internal = (UniverseImpl) universe;
    return internal.addStash(new EntityStashImpl(universe));
  }

  /**
   * Returns {@code true} if the specified entity index is present in this
   * stash, otherwise {@code false}.
   *
   * @param index the entity index
   * @return whether the entity is present in the stash
   * @since 0.3.0
   */
  boolean contains(final @NonNegative int index);

  /**
   * Returns {@code true} if the specified entity is present in this stash,
   * otherwise {@code false}.
   *
   * @param entity the entity
   * @return whether the entity is present in the stash
   * @since 0.3.0
   */
  default boolean contains(final @NonNull Entity entity) {
    requireNonNull(entity, "entity");
    return this.contains(entity.index());
  }

  /**
   * Returns the {@link Entity} at the specified index, if it exists.
   *
   * @param index the entity index
   * @return the entity, if present
   * @since 0.3.0
   */
  @Nullable Entity get(final @NonNegative int index);

  /**
   * Returns the {@code T} entity at the specified index with the specified
   * {@link Class} type, if it exists.
   *
   * @param index the entity index
   * @param target the entity class type
   * @param <T> the entity type
   * @return the entity, if present
   * @since 0.3.0
   */
  <T extends Entity> @Nullable T get(final @NonNegative int index, final @NonNull Class<T> target);

  /**
   * Returns {@code true} if the entity was added to this stash, otherwise
   * {@code false}.
   *
   * @param entity the entity
   * @return whether the entity was added to the stash
   * @since 0.3.0
   */
  default boolean add(final @NonNull Entity entity) {
    requireNonNull(entity, "entity");
    return this.add(entity.index());
  }

  /**
   * Returns {@code true} if the entity index was added to this stash,
   * otherwise {@code false}.
   *
   * @param index the entity index
   * @return whether the entity index was added to the stash
   * @since 0.3.0
   */
  boolean add(final @NonNegative int index);

  /**
   * Returns {@code true} if the entity was removed from this stash, otherwise
   * {@code false}.
   *
   * @param entity the entity
   * @return whether the entity was removed from the stash
   * @since 0.3.0
   */
  default boolean remove(final @NonNull Entity entity) {
    requireNonNull(entity, "entity");
    return this.remove(entity.index());
  }

  /**
   * Returns {@code true} if the entity index was removed from this stash,
   * otherwise {@code false}.
   *
   * @param index the entity index
   * @return whether the entity index was removed from the stash
   * @since 0.3.0
   */
  boolean remove(final @NonNegative int index);

  /**
   * Clears all the entities from this stash.
   *
   * @since 0.3.0
   */
  void clear();

  /**
   * Returns a {@link CustomIterator} of the entities in this stash.
   *
   * <p>Calling {@link CustomIterator#remove()} will remove the entity
   * from the stash and NOT the universe.</p>
   *
   * @return an iterator of entities
   * @since 0.3.0
   */
  @Override
  @NonNull CustomIterator<Entity> iterator();
}
