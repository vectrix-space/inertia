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
package space.vectrix.inertia.holder;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.annotation.Component;
import space.vectrix.inertia.component.ComponentType;

import java.util.Optional;

/**
 * Represents a way to explore a holders associated components.
 *
 * @since 0.2.0
 */
public interface HolderExplorer {
  /**
   * Returns whether the holder is a valid participant in a
   * {@link Universe}.
   *
   * @return whether the holder is valid
   * @since 0.2.0
   */
  boolean valid();

  /**
   * Returns {@code true} if the holder has a {@link Component} instance for
   * the specified {@link ComponentType}.
   *
   * @param componentType the component type
   * @return whether the component exists in this holder
   * @since 0.2.0
   */
  boolean contains(final @NonNull ComponentType componentType);

  /**
   * Returns the {@code T} component with the specified {@link ComponentType},
   * if it exists.
   *
   * @param componentType the component type
   * @param <T> the specific component type
   * @return the component, if present
   * @since 0.2.0
   */
  <T> @Nullable T get(final @NonNull ComponentType componentType);

  /**
   * Returns the {@code T} component with the specified {@link ComponentType},
   * if it exists.
   *
   * @param componentType the component type
   * @param <T> the specific component type
   * @return the component, if present
   * @since 0.2.0
   */
  <T> @NonNull Optional<T> getPresent(final @NonNull ComponentType componentType);

  /**
   * Returns the existing {@code T} or creates a new {@code T} for the
   * specified {@link ComponentType}.
   *
   * @param componentType the component type
   * @param <T> the specific component type
   * @return the present component, or a new component
   * @since 0.2.0
   */
  <T> @NonNull T add(final @NonNull ComponentType componentType);
}
