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

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.function.Function;

/**
 * Useful map functions.
 *
 * @since 0.2.0
 */
public interface MapFunctions {
  /**
   * Returns the existing {@code V} value for the specified {@code K} key
   * otherwise returns (but does not store) the result from the specified
   * {@link Function}.
   *
   * @param map the map
   * @param key the key
   * @param function the function
   * @param <K> the key type
   * @param <V> the value type
   * @return the value
   * @since 0.2.0
   */
  static <K, V> V getOr(final @NonNull Map<K, V> map, final @NonNull K key, final @NonNull Function<K, V> function) {
    V value = map.get(key);
    if(value == null) value = function.apply(key);
    return value;
  }

  /**
   * Returns the existing {@code V} value for the specified {@code int} key
   * otherwise returns (but does not store) the result from the specified
   * {@link Int2ObjectFunction}.
   *
   * @param map the map
   * @param key the key
   * @param function the function
   * @param <V> the value type
   * @return the value
   * @since 0.2.0
   */
  static <V> V getOr(final @NonNull Int2ObjectMap<V> map, final int key, final @NonNull Int2ObjectFunction<V> function) {
    V value = map.get(key);
    if(value == null) value = function.apply(key);
    return value;
  }
}
