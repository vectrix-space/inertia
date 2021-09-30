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

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;

import static java.util.Objects.requireNonNull;

/**
 * Represents a component type.
 *
 * @since 0.3.0
 */
public interface ComponentType {
  /**
   * Returns the existing {@link ComponentType} for the specified {@link Class}
   * target and {@link Universe}, otherwise creates a new one.
   *
   * @param universe the universe
   * @param target the target component
   * @return the component type
   * @since 0.3.0
   */
  static @NonNull ComponentType create(final @NonNull Universe universe, final @NonNull Class<?> target) {
    requireNonNull(universe, "universe");
    requireNonNull(target, "target");
    return ComponentTypes.resolve(universe, target);
  }

  /**
   * Returns the unique {@code int} index for this component
   * type.
   *
   * @return the component type index
   * @since 0.3.0
   */
  @NonNegative int index();

  /**
   * Returns the {@link String} identifier for this component
   * type.
   *
   * @return the component type identifier
   * @since 0.3.0
   */
  @NonNull String id();

  /**
   * Returns the {@link String} name for this component type.
   *
   * @return the component type name
   * @since 0.3.0
   */
  @NonNull String name();

  /**
   * Returns the {@link Class} type for this component type.
   *
   * @return the component type class
   * @since 0.3.0
   */
  @NonNull Class<?> type();
}
