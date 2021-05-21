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
package space.vectrix.inertia.processor;

import static java.util.Objects.requireNonNull;

import net.kyori.coffee.Ordered;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a processor of holders.
 *
 * @since 0.2.0
 */
@FunctionalInterface
public interface Processor extends Ordered<Processor> {
  /**
   * Returns an {@code int} for this processors priority.
   *
   * @return The processor priority
   * @since 0.2.0
   */
  default int priority() {
    return 0;
  }

  /**
   * Returns {@code true} if this processor has been initialized, otherwise
   * returns {@code false}.
   *
   * <p>In the case you need a custom implementation of {@link Processor#initialize()},
   * this should return {@code false} by default, then be set {@code true} after
   * {@link Processor#initialize()} has been called.</p>
   *
   * @return Whether this processor has been initialized
   * @since 0.2.0
   */
  default boolean initialized() {
    return true;
  }

  /**
   * Called when the processor is initialized.
   *
   * <p>Requires {@link Processor#initialized()} to return {@code false}
   * to be run in the processing loop.</p>
   *
   * @throws Throwable when there is an issue processing
   * @since 0.2.0
   */
  default void initialize() throws Throwable {}

  /**
   * Called when the processor is in the preparation stage at the
   * beginning of each game tick.
   *
   * @throws Throwable when there is an issue processing
   * @since 0.2.0
   */
  default void prepare() throws Throwable {}

  /**
   * Called when the processor is in the execution stage in the
   * middle of each game tick.
   *
   * @throws Throwable when there is an issue processing
   * @since 0.2.0
   */
  void execute() throws Throwable;

  @Override
  default int compareTo(final @NonNull Processor other) {
    requireNonNull(other, "other");
    return Integer.compare(this.priority(), other.priority());
  }
}
