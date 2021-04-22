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
import space.vectrix.inertia.holder.Holder;

/**
 * {@inheritDoc}
 *
 * Provides methods to use internally for storing and removing
 * {@link Processor}s.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public abstract class AbstractProcessors<H extends Holder<C>, C> implements Processors<H, C> {
  protected final Object lock = new Object();

  /**
   * Returns {@code true} if the specified {@code T} processor is stored
   * successfully, otherwise returns {@code false}.
   *
   * @param type The processor class
   * @param processor The processor instance
   * @param <T> The specific processor type
   * @return Whether the processor was stored
   * @since 0.1.0
   */
  public abstract <T extends Processor<H, C>> boolean put(final Class<T> type, final @NonNull T processor);

  /**
   * Removes the processor with the specified {@link Class} from the
   * registry.
   *
   * @param type The processor class
   * @return Whether the processor was removed
   * @since 0.1.0
   */
  public abstract boolean remove(final @NonNull Class<? extends Processor<H, C>> type);
}
