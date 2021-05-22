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

import net.kyori.coffee.math.range.i.IntRange;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;

/**
 * The processor registry.
 *
 * @since 0.2.0
 */
public interface ProcessorContainer {
  /**
   * Returns the {@code T} processor with the specified {@link Class}
   * type, if it exists.
   *
   * @param type The processor class
   * @param <T> The processor type
   * @return The processor instance, if present
   * @since 0.2.0
   */
  <T extends Processor> @Nullable T getProcessor(final @NonNull Class<T> type);

  /**
   * Adds the {@code T} processor to this container.
   *
   * @param processor the processor
   * @param <T> the specific processor type
   * @since 0.2.0
   */
  <T extends Processor> void addProcessor(final @NonNull T processor);

  /**
   * Returns a {@link Collection} of {@link Processor}s with the
   * specified {@code int} priority.
   *
   * @param priority The priority
   * @return A collection of processor instances
   * @since 0.2.0
   */
  @NonNull Collection<? extends Processor> processors(final int priority);

  /**
   * Returns a {@link Collection} of {@link Processor}s with
   * the specified {@link IntRange} priority.
   *
   * @param priorities The range of priorities
   * @return A collection of processor instances
   * @since 0.2.0
   */
  @NonNull Collection<? extends Processor> processors(final @NonNull IntRange priorities);

  /**
   * Returns a {@link Collection} of {@link Processor}s in this
   * registry.
   *
   * @return A collection of processor instances
   * @since 0.2.0
   */
  @NonNull Collection<? extends Processor> processors();
}
