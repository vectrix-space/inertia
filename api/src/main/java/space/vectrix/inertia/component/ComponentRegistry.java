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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The component registry.
 *
 * @since 0.1.0
 */
public interface ComponentRegistry {
  /**
   * Returns the {@link ComponentType} with the specified {@code int}
   * index, if it exists.
   *
   * @param index The component index
   * @return The component type, if present
   * @since 0.1.0
   */
  @Nullable ComponentType get(final int index);

  /**
   * Returns the {@link ComponentType} with the specified {@link Class}
   * type, if it exists.
   *
   * @param type The component class
   * @return The component type, if present
   * @since 0.1.0
   */
  @Nullable ComponentType get(final @NonNull Class<?> type);

  /**
   * Returns the {@link ComponentType} with the specified {@link String}
   * identifier, if it exists.
   *
   * @param identifier The component identifier
   * @return The component type, if present
   * @since 0.1.0
   */
  @Nullable ComponentType get(final @NonNull String identifier);
}
