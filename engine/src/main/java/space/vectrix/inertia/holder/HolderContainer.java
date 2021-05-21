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

public interface HolderContainer {
  /**
   * Returns a new {@link Holder}.
   *
   * @return the new holder
   * @since 0.2.0
   */
  @NonNull Holder createHolder();

  /**
   * Returns a new {@link Holder} of type {@code T} using the specified
   * {@link HolderFunction}.
   *
   * @param function the holder function
   * @param <T> the specific holder type
   * @return the new holder
   * @since 0.2.0
   */
  <T extends Holder> @NonNull T createHolder(final @NonNull HolderFunction<T> function);

  /**
   * Returns whether the specified {@link Holder} is a valid participant.
   *
   * @param holder the holder
   * @return whether the holder is valid
   * @since 0.2.0
   */
  boolean valid(final @NonNull Holder holder);
}
