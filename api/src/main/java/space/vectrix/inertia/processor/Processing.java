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

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.holder.Holder;

import java.util.function.Function;

/**
 * The processing system.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public interface Processing<H extends Holder<C>, C> {
  /**
   * Creates the {@code T} processor using the specified {@link Class} type
   * and {@link Function}.
   *
   * @param type The processor class
   * @param processorFunction The processor function
   * @param <T> The specific processor type
   * @return The processor instance
   * @since 0.1.0
   */
  <T extends Processor<H, C>> @NonNull T create(final @NonNull Class<T> type,
                                                final @NonNull Function<Universe<H, C>, T> processorFunction);

  /**
   * Ticks all the processors contained within this {@link Universe}.
   *
   * @return The tick index
   * @since 0.1.0
   */
  int process();

  /**
   * Handles any {@link Throwable}s caused in the processing phases.
   *
   * @param throwable The processing exception
   * @since 0.1.0
   */
  void processException(final @NonNull Throwable throwable);

  /**
   * The factory for creating a {@link Processing} system.
   *
   * @since 0.1.0
   */
  @FunctionalInterface
  interface Factory {
    /**
     * Creates a new {@link Processing} system for the specified {@link Universe}.
     *
     * @param universe The universe
     * @param <H> The holder type
     * @param <C> The component type
     * @return The processing system
     * @since 0.1.0
     */
    <H extends Holder<C>, C> @NonNull Processing<H, C> create(final @NonNull Universe<H, C> universe);
  }
}
