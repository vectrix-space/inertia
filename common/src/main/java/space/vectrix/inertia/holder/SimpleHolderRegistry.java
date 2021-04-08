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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Optional;

public final class SimpleHolderRegistry<H extends Holder<C>, C> implements HolderRegistry<H, C> {
  private final Int2ObjectMap<H> holders = new Int2ObjectOpenHashMap<>(100);
  private final Multimap<Class<?>, H> holdersTyped = HashMultimap.create(10, 50);

  public SimpleHolderRegistry() {}

  @Override
  public @NonNull Optional<H> get(final int index) {
    return Optional.ofNullable(this.holders.get(index));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends H> @NonNull Collection<T> get(final @NonNull Class<T> type) {
    return (Collection<T>) this.holdersTyped.get(type);
  }

  @Override
  public @NonNull Collection<? extends H> all() {
    return this.holders.values();
  }

  public <T extends H> void put(final int index, final @NonNull Class<?> type, final @NonNull T holder) {
    if (this.holdersTyped.put(type, holder)) {
      this.holders.put(index, holder);
    }
  }
}
