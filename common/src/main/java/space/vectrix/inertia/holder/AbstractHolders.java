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

/**
 * {@inheritDoc}
 *
 * Provides methods to use internally for storing and removing {@code H}
 * holders.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public abstract class AbstractHolders<H extends Holder<C>, C> implements Holders<H, C> {
  /**
   * Returns {@code true} if the specified {@code int} holder is stored
   * successfully, otherwise returns {@code false}.
   *
   * @param index The holder index
   * @return Whether the holder was stored
   * @since 0.1.0
   */
  public abstract boolean put(final int index);

  /**
   * Puts the specified {@code int} holder with the {@code T} holder instance.
   *
   * @param index The holder index
   * @param holder The holder instance
   * @param <T> The specific holder type
   * @since 0.1.0
   */
  public abstract <T extends H> void put(final int index, final @NonNull T holder);

  /**
   * Returns {@code true} if the specified {@code int} index exists in this
   * registry, otherwise returns {@code false}.
   *
   * @param index The holder index
   * @return True if it exists, otherwise false
   * @since 0.1.0
   */
  public abstract boolean contains(final int index);

  /**
   * Removes the {@code int} holder from the registry.
   *
   * @param index The holder index
   * @return Whether the holder was removed
   * @since 0.1.0
   */
  public abstract boolean remove(final int index);

  /**
   * Clears the holders from the registry.
   *
   * @since 0.1.0
   */
  public abstract void clear();
}
