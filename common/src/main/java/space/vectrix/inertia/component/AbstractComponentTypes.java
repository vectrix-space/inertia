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
import space.vectrix.inertia.holder.Holder;

import java.util.function.Function;

/**
 * {@inheritDoc}
 *
 * Provides methods to use internally for storing and removing {@code C}
 * component types.
 *
 * @param <H> The holder type
 * @param <C> The component type
 */
public abstract class AbstractComponentTypes<H extends Holder<C>, C> implements ComponentTypes {
  /**
   * Puts the specified {@link Class} type and {@link ComponentTypeImpl} into
   * this registry if it doesn't already exist, otherwise returns the existing
   * {@link ComponentTypeImpl}.
   *
   * @param type The component class
   * @param computation The function to create and store a new component type
   * @return The component type
   * @since 0.1.0
   */
  public abstract @NonNull ComponentTypeImpl<H, C> put(final @NonNull Class<?> type, final @NonNull Function<Class<?>, ComponentTypeImpl<H, C>> computation);

  /**
   * Returns {@code true} if the specified {@code int} component type index
   * exists in this registry, otherwise returns {@code false}.
   *
   * @param index The component type index
   * @return True if it exists, otherwise false
   * @since 0.1.0
   */
  public abstract boolean contains(final int index);
}
