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
package space.vectrix.inertia.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A custom iterator that can be used to filter elements while iterating.
 *
 * @param <E> the element type
 * @since 0.3.0
 */
public interface CustomIterator<E> extends Iterator<E> {
  /**
   * Returns this iterator with the next elements filtered with the specified
   * {@link Predicate} returning {@code true}.
   *
   * @param predicate the predicate
   * @return this iterator
   * @since 0.3.0
   */
  @NonNull CustomIterator<E> with(final @NonNull Predicate<? super E> predicate);

  /**
   * Returns this iterator with the next elements filtered without the specified
   * {@link Predicate} returning {@code true}.
   *
   * @param predicate the predicate
   * @return this iterator
   * @since 0.3.0
   */
  @NonNull CustomIterator<E> without(final @NonNull Predicate<? super E> predicate);
}
