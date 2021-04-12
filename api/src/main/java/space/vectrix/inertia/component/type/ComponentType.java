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
package space.vectrix.inertia.component.type;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

/**
 * The component type.
 *
 * @since 0.1.0
 */
public interface ComponentType {
  /**
   * The component index.
   *
   * @return The index
   * @since 0.1.0
   */
  int index();

  /**
   * The component identifier.
   *
   * @return The identifier
   * @since 0.1.0
   */
  @NonNull String id();

  /**
   * The component name.
   *
   * @return The name
   * @since 0.1.0
   */
  @NonNull String name();

  /**
   * The component type.
   *
   * @return The type
   * @since 0.1.0
   */
  @NonNull Class<?> type();

  /**
   * The component required {@link ComponentType} dependencies.
   *
   * @return The required dependencies
   * @since 0.1.0
   */
  @NonNull Set<ComponentType> requiredDependencies();

  /**
   * The component optional {@link ComponentType} dependencies.
   *
   * @return The optional dependencies
   * @since 0.1.0
   */
  @NonNull Set<ComponentType> optionalDependencies();
}
