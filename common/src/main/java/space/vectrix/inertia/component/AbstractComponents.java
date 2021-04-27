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

/**
 * {@inheritDoc}
 *
 * Provides methods to use internally for storing and removing
 * {@code H} holder {@code C} components.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public abstract class AbstractComponents<H extends Holder<C>, C> implements Components<H, C> {
  /**
   * Returns {@code true} if the specified {@code T} component for the
   * {@link ComponentType} and {@code int} holder is stored successfully,
   * otherwise returns {@code false}.
   *
   * @param holder The holder index
   * @param componentType The component type
   * @param component The component instance
   * @param <T> The specific component type
   * @return Whether the component was stored
   * @since 0.1.0
   */
  public abstract <T extends C> boolean put(final int holder, final @NonNull ComponentType componentType, final @NonNull T component);

  /**
   * Returns {@code true} if the specified {@link ComponentType} for the
   * {@code int} holder is removed successfully, otherwise returns {@code false}.
   *
   * @param holder The holder index
   * @param componentType The component type
   * @return Whether the component was removed
   * @since 0.1.0
   */
  public abstract boolean remove(final int holder, final @NonNull ComponentType componentType);

  /**
   * Removes the {@code int} holder from the registry.
   *
   * @param holder The holder index
   * @since 0.1.0
   */
  public abstract void remove(final int holder);
}
