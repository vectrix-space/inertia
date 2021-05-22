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
package space.vectrix.inertia.util.counter;

import static java.util.Objects.requireNonNull;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

/**
 * A way to acquire new indexes from a collection of used indexes.
 *
 * @since 0.2.0
 */
public interface IndexCounter {
  /**
   * Returns a new {@link IndexCounter} for the specified {@link IntSet}.
   *
   * @param identifier the counter identifier
   * @param set the set
   * @return the counter
   * @since 0.2.0
   */
  static @NonNull IndexCounter counter(final @NonNull String identifier, final IntSet set) {
    requireNonNull(identifier, "identifier");
    requireNonNull(set, "set");
    return new IndexCounterImpl(identifier, set);
  }

  /**
   * Returns a new {@link IndexCounter} for the specified {@link Int2ObjectMap}
   * where the key is the index to count.
   *
   * @param identifier the counter identifier
   * @param map the map
   * @return the counter
   * @since 0.2.0
   */
  static @NonNull IndexCounter counter(final @NonNull String identifier, final Int2ObjectMap<?> map) {
    requireNonNull(identifier, "identifier");
    requireNonNull(map, "map");
    return new IndexCounterImpl(identifier, map.keySet());
  }

  /**
   * Returns the internal index for this counter as an {@link AtomicInteger}.
   *
   * @return the counter
   * @since 0.2.0
   */
  @NonNull AtomicInteger counter();

  /**
   * Returns the next index for this counter if it is available, otherwise
   * throws a {@link UnavailableIndexException}.
   *
   * @return the next available index
   * @throws UnavailableIndexException when there is no available index
   */
  int next() throws UnavailableIndexException;

  /**
   * Retrieves the next index for this counter if it is available and passes
   * it to the {@link IntFunction} and returns the {@code T}, otherwise throws
   * a {@link UnavailableIndexException}.
   *
   * <p>The consumer will be run while inside a lock, which should then be
   * used to update the backing unavailable index set.</p>
   *
   * @param consumer the index consumer
   * @param <T> the return type
   * @return an object
   * @throws UnavailableIndexException when there is no available index
   */
  <T> T next(final @NonNull IntFunction<T> consumer) throws UnavailableIndexException;
}
