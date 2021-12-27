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
import space.vectrix.inertia.component.Component;
import space.vectrix.inertia.component.ComponentType;

import java.util.Iterator;
import java.util.Optional;

/**
 * Represents a container of components.
 *
 * @since 0.3.0
 */
public interface Entity {
  /**
   * Returns an {@link EntityFunction} to create a simple entity
   * implementation that components can be attached to.
   *
   * @return the simple entity function
   * @since 0.3.0
   */
  static @NonNull EntityFunction<Entity> simple() {
    return EntityImpl::new;
  }

  /**
   * Returns the {@link Universe}.
   *
   * @return the universe
   * @since 0.3.0
   */
  @NonNull Universe universe();

  /**
   * Returns the {@code int} index.
   *
   * @return the index
   * @since 0.3.0
   */
  @NonNegative int index();

  /**
   * Returns {@code true} if the entity has a {@link Component} instance for
   * the specified {@link ComponentType}.
   *
   * @param type the component type
   * @return whether the component exists in this entity
   * @since 0.3.0
   */
  boolean contains(final @NonNull ComponentType type);

  /**
   * Returns the {@code T} component with the specified {@link ComponentType},
   * if it exists.
   *
   * @param type the component type
   * @param <T> the specific component type
   * @return the component, if present
   * @since 0.3.0
   */
  <T> @Nullable T get(final @NonNull ComponentType type);

  /**
   * Returns the {@code T} component with the specified {@link ComponentType},
   * if it exists.
   *
   * @param type the component type
   * @param <T> the specific component type
   * @return the component, if present
   * @since 0.3.0
   */
  default <T> @NonNull Optional<T> getPresent(final @NonNull ComponentType type) {
    return Optional.ofNullable(this.get(type));
  }

  /**
   * Returns the existing {@code T} or creates a new {@code T} for the
   * specified {@link ComponentType}.
   *
   * @param type the component type
   * @param <T> the specific component type
   * @return the present component, or a new component
   * @since 0.3.0
   */
  <T> @NonNull T add(final @NonNull ComponentType type);

  /**
   * Removes the specified {@link ComponentType} if it exists.
   *
   * @param type the component type
   * @since 0.3.0
   */
  void remove(final @NonNull ComponentType type);

  /**
   * Removes all the components associated with this entity.
   *
   * @since 0.3.0
   */
  void clear();

  /**
   * Marks this entity for removal from the universe.
   *
   * @since 0.3.0
   */
  void destroy();

  /**
   * Returns an {@link Iterator} of components.
   *
   * @return an iterator of components
   * @since 0.3.0
   */
  @NonNull Iterator<Object> components();
}
