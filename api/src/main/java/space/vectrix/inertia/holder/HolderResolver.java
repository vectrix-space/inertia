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
import space.vectrix.inertia.Universe;

/**
 * The holder resolver.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public interface HolderResolver<H extends Holder<C>, C> {
  /**
   * Creates the {@code T} holder using the specified {@link HolderFunction}.
   *
   * @param holderFunction The holder function
   * @param <T> The specific holder type
   * @return The holder
   * @since 0.1.0
   */
  <T extends H> @NonNull T create(final @NonNull HolderFunction<H, C, T> holderFunction);

  /**
   * The function for creating a new holder from the supplied
   * {@link Universe} and {@code index}.
   *
   * @param <H> The holder type
   * @param <C> The component type
   * @param <T> The specific holder type
   * @since 0.1.0
   */
  @FunctionalInterface
  interface HolderFunction<H extends Holder<C>, C, T extends H> {
    /**
     * Creates a new {@link Holder} for the specified {@link Universe}
     * with the specified {@code index}.
     *
     * @param universe The universe
     * @param index The index
     * @return The new holder
     */
    T apply(final @NonNull Universe<H, C> universe, final int index);
  }

  /**
   * The factory for created an {@link HolderResolver}.
   *
   * @since 0.1.0
   */
  @FunctionalInterface
  interface Factory {
    /**
     * Creates a new {@link HolderResolver} for the specified {@link Universe}.
     *
     * @param universe The universe
     * @param <H> The holder type
     * @param <C> The component type
     * @return The holder resolver
     * @since 0.1.0
     */
    <H extends Holder<C>, C> @NonNull HolderResolver<H, C> create(final @NonNull Universe<H, C> universe);
  }
}
