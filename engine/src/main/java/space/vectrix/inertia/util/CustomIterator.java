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
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.util.functional.ThrowableConsumer;
import space.vectrix.inertia.util.functional.ThrowableFunction;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * A custom iterator that can be used to filter elements while iterating.
 *
 * @param <T> the element type
 * @since 0.3.0
 */
public interface CustomIterator<T> extends Iterator<T> {
  /**
   * Returns a new empty custom iterator.
   *
   * @param <T> the element type
   * @return a new empty custom iterator
   * @since 0.3.0
   */
  static <T> @NonNull CustomIterator<T> empty() {
    return new CustomIteratorImpl<>(Collections.<T>emptyIterator(), x -> x, ThrowableConsumer.throwing(new UnsupportedOperationException()));
  }

  /**
   * Returns a new custom iterator with the specified backing iterator.
   *
   * @param iterator the backing iterator
   * @param <T> the element type
   * @return a new custom iterator
   * @since 0.3.0
   */
  static <T> @NonNull CustomIterator<T> of(final @NonNull Iterator<@NonNull T> iterator) {
    requireNonNull(iterator, "iterator");
    return new CustomIteratorImpl<>(iterator, x -> x, ThrowableConsumer.throwing(new UnsupportedOperationException()));
  }

  /**
   * Returns a new custom iterator with the specified backing iterator, and
   * {@link ThrowableFunction} to remap elements.
   *
   * @param iterator the backing iterator
   * @param mapper the remapping function
   * @param <A> the input type
   * @param <T> the output type
   * @param <E> the exception type
   * @return a new custom iterator
   * @since 0.3.0
   */
  static <A, T, E extends Throwable> @NonNull CustomIterator<T> of(final @NonNull Iterator<@NonNull A> iterator,
                                                                   final @NonNull ThrowableFunction<@NonNull A, @Nullable T, E> mapper) {
    requireNonNull(iterator, "iterator");
    requireNonNull(mapper, "mapper");
    return new CustomIteratorImpl<>(iterator, mapper, ThrowableConsumer.throwing(new UnsupportedOperationException()));
  }

  /**
   * Returns a new custom iterator, with the specified backing iterator,
   * {@link ThrowableFunction} to remap elements, and {@link ThrowableConsumer}
   * to remove elements.
   *
   * @param iterator the backing iterator
   * @param mapper the remapping function
   * @param remove the removal function
   * @param <A> the input type
   * @param <T> the output type
   * @param <E> the exception type
   * @return a new custom iterator
   * @since 0.3.0
   */
  static <A, T, E extends Throwable> @NonNull CustomIterator<T> of(final @NonNull Iterator<@NonNull A> iterator,
                                                                   final @NonNull ThrowableFunction<@NonNull A, @Nullable T, E> mapper,
                                                                   final @NonNull ThrowableConsumer<@NonNull T, E> remove) {
    requireNonNull(iterator, "iterator");
    requireNonNull(mapper, "mapper");
    requireNonNull(remove, "remove");
    return new CustomIteratorImpl<>(iterator, mapper, remove);
  }

  /**
   * Returns this iterator with the next elements filtered with the specified
   * {@link Predicate} returning {@code true}.
   *
   * @param predicate the predicate
   * @return this iterator
   * @since 0.3.0
   */
  @NonNull CustomIterator<T> with(final @NonNull Predicate<? super T> predicate);

  /**
   * Returns this iterator with the next elements filtered without the specified
   * {@link Predicate} returning {@code true}.
   *
   * @param predicate the predicate
   * @return this iterator
   * @since 0.3.0
   */
  @NonNull CustomIterator<T> without(final @NonNull Predicate<? super T> predicate);
}
