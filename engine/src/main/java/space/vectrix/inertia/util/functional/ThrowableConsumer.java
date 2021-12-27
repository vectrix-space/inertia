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
package space.vectrix.inertia.util.functional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.function.Consumer;

/**
 * A {@link Consumer} that can throw an exception.
 *
 * @param <T> the element type
 * @param <E> the exception type
 * @since 0.3.0
 */
@FunctionalInterface
public interface ThrowableConsumer<T, E extends Throwable> extends Consumer<T> {
  /**
   * Returns a throwable consumer that throws the specified exception.
   *
   * @param exception the exception to throw
   * @param <T> the element type
   * @param <E> the exception type
   * @return a throwable consumer that throws an exception
   * @since 0.3.0
   */
  static <T, E extends Throwable> ThrowableConsumer<T, E> throwing(final @NonNull E exception) {
    return ignored -> {
      throw exception;
    };
  }

  @Override
  default void accept(final @PolyNull T element) {
    try {
      this.acceptThrows(element);
    } catch(final Throwable exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Accepts the specified element.
   *
   * @param element the element
   * @throws E the exception thrown by the consumer
   * @since 0.3.0
   */
  void acceptThrows(final @PolyNull T element) throws E;
}
