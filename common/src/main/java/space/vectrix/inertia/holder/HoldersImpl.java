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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.flare.fastutil.Int2ObjectSyncMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public final class HoldersImpl<H extends Holder<C>, C> extends AbstractHolders<H, C> {
  private final IntSet holders = IntSets.synchronize(new IntOpenHashSet(100));
  private final Int2ObjectSyncMap<H> instances = Int2ObjectSyncMap.hashmap(100);
  private final Multimap<Class<?>, H> typed = Multimaps.synchronizedMultimap(HashMultimap.create(10, 50));

  public HoldersImpl() {}

  @Override
  @SuppressWarnings("unchecked")
  public <T extends H> @NonNull Optional<T> get(final int index) {
    return Optional.ofNullable((T) this.instances.get(index));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends H> @NonNull Collection<T> all(final @NonNull Class<T> type) {
    requireNonNull(type, "type");
    return (Collection<T>) this.typed.get(type);
  }

  @Override
  public @NonNull Collection<? extends H> all() {
    return this.instances.values();
  }

  @Override
  public @NonNull Iterator<H> iterator() {
    return this.instances.values().iterator();
  }

  @Override
  public boolean put(final int index) {
    return this.holders.add(index);
  }

  @Override
  public <T extends H> void put(final int index, final @NonNull T holder) {
    if (this.put(index)) {
      this.instances.put(index, holder);
      this.typed.put(holder.getClass(), holder);
    }
  }

  @Override
  public boolean contains(final int index) {
    return this.holders.contains(index);
  }

  @Override
  public boolean remove(final int index) {
    if (this.holders.remove(index)) {
      this.removeInstance(index);
      return true;
    }
    return false;
  }

  @Override
  public void clear() {
    this.holders.clear();
    this.instances.clear();
    this.typed.clear();
  }

  private void removeInstance(final int index) {
    final H holder = this.instances.remove(index);
    if(holder != null) {
      this.typed.remove(holder.getClass(), holder);
    }
  }
}
