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

import java.util.Collection;
import java.util.Optional;

/**
 * The holder registry.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public interface Holders<H extends Holder<C>, C> {
  /**
   * Returns the {@code T} holder with the specified {@code int}
   * index, if it exists.
   *
   * @param index The holder index
   * @return The holder instance, if present
   * @since 0.1.0
   */
  <T extends H> @NonNull Optional<T> get(final int index);

  /**
   * Returns a {@link Collection} of {@code T} holders in this
   * registry.
   *
   * @param type The holder class
   * @param <T> The specific holder type
   * @return A collection of holder instances of that type
   * @since 0.1.0
   */
  <T extends H> @NonNull Collection<T> get(final @NonNull Class<T> type);

  /**
   * Returns a {@link Collection} of {@code H} holders in this
   * registry.
   *
   * @return A collection of holder instances
   * @since 0.1.0
   */
  @NonNull Collection<? extends H> all();
}
