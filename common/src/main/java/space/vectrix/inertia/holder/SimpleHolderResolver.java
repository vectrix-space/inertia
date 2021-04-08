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

import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;

import java.util.concurrent.atomic.AtomicInteger;

public final class SimpleHolderResolver<H extends Holder<C>, C> implements HolderResolver<H, C> {
  private final AtomicInteger index = new AtomicInteger();
  private final Universe<H, C> universe;

  /* package */ SimpleHolderResolver(final Universe<H, C> universe) {
    this.universe = universe;
  }

  @Override
  public <T extends H> @NonNull T create(final @NonNull HolderFunction<H, C, T> holderFunction) {
    requireNonNull(holderFunction, "holderFunction");
    final int index = this.index.getAndIncrement();
    final T holderInstance = holderFunction.apply(this.universe, index);
    ((SimpleHolderRegistry<H, C>) this.universe.holders()).put(index, holderInstance.getClass(), holderInstance);
    return holderInstance;
  }

  public static final class Factory implements HolderResolver.Factory {
    public Factory() {}

    @Override
    public @NonNull <H extends Holder<C>, C> HolderResolver<H, C> create(final @NonNull Universe<H, C> universe) {
      requireNonNull(universe, "universe");
      return new SimpleHolderResolver<>(universe);
    }
  }
}
