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
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public final class HoldersImpl<H extends Holder<C>, C> extends AbstractHolders<H, C> {
  private final IntSet holders = new IntOpenHashSet(100);
  private final Int2ObjectMap<H> holderInstances = new Int2ObjectOpenHashMap<>(100);
  private final Multimap<Class<?>, H> holderInstancesTyped = HashMultimap.create(10, 50);

  public HoldersImpl() {}

  @Override
  @SuppressWarnings("unchecked")
  public <T extends H> @NonNull Optional<T> get(final int index) {
    synchronized(this.lock) {
      return Optional.ofNullable((T) this.holderInstances.get(index));
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends H> @NonNull Collection<T> all(final @NonNull Class<T> type) {
    requireNonNull(type, "type");
    synchronized(this.lock) {
      return (Collection<T>) this.holderInstancesTyped.get(type);
    }
  }

  @Override
  public @NonNull Collection<? extends H> all() {
    return this.holderInstances.values();
  }

  @Override
  public @NonNull Iterator<H> iterator() {
    return this.holderInstances.values().iterator();
  }

  @Override
  public boolean put(final int index) {
    synchronized(this.lock) {
      return this.holders.add(index);
    }
  }

  @Override
  public <T extends H> void put(final int index, final @NonNull T holder) {
    synchronized(this.lock) {
      if (this.put(index)) {
        this.holderInstances.put(index, holder);
        this.holderInstancesTyped.put(holder.getClass(), holder);
      }
    }
  }

  @Override
  public boolean contains(final int index) {
    synchronized(this.lock) {
      return this.holders.contains(index);
    }
  }

  @Override
  public boolean remove(final int index) {
    synchronized(this.lock) {
      if (this.holders.remove(index)) {
        this.removeInstance(index);
        return true;
      }
      return false;
    }
  }

  private void removeInstance(final int index) {
    final H holder = this.holderInstances.remove(index);
    if(holder != null) {
      this.holderInstancesTyped.remove(holder.getClass(), holder);
    }
  }
}
