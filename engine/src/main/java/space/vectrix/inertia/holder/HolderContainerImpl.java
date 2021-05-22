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

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.flare.SyncMap;
import space.vectrix.flare.fastutil.Int2ObjectSyncMap;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.processor.ProcessingSystem;
import space.vectrix.inertia.util.counter.IndexCounter;
import space.vectrix.inertia.util.version.Version;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;

public final class HolderContainerImpl implements HolderContainer, ProcessingSystem {
  private final IntSet holderRemovals = Int2ObjectSyncMap.hashset(50);
  private final Int2ObjectSyncMap<Holder> holders = Int2ObjectSyncMap.hashmap(500);
  private final IndexCounter holderCounter = IndexCounter.counter("holders", this.holders);
  private final Multimap<Class<?>, Holder> holderGroup = Multimaps.newMultimap(
    SyncMap.of(IdentityHashMap<Class<?>, SyncMap.ExpungingValue<Collection<Holder>>>::new, 500),
    () -> SyncMap.hashset(100)
  );
  private final Universe universe;

  public HolderContainerImpl(final @NonNull Universe universe) {
    this.universe = universe;
  }

  @Override
  public boolean valid(final @NonNull Holder holder) {
    final Version holderVersion = holder.version();
    if(!holderVersion.belongs(this.universe)) return false;
    if(!this.holders.containsKey(holderVersion.index())) return false;

    return !this.holderRemovals.contains(holderVersion.index());
  }

  @Override
  public @NonNull Holder createHolder() {
    return this.createHolder(HolderImpl::new);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Holder> @NonNull T createHolder(final @NonNull HolderFunction<T> function) {
    requireNonNull(function, "function");
    final T holder = this.holderCounter.next(index -> (T) this.holders.computeIfAbsent(index, key -> function.apply(this.universe, key)));
    this.holderGroup.put(holder.getClass(), holder);
    return holder;
  }

  @Override
  public boolean removeHolder(final @NonNull Holder holder) {
    requireNonNull(holder, "holder");
    final Version holderVersion = holder.version();
    if(!holderVersion.belongs(this.universe)) return false;

    return this.holderRemovals.add(holderVersion.index());
  }

  @Override
  public @NonNull Collection<Holder> holders() {
    return Collections.unmodifiableCollection(this.holders.values());
  }

  @Override
  public void process() {
    final IntIterator iterator = this.holderRemovals.intIterator();
    while(iterator.hasNext()) {
      final int index = iterator.nextInt();
      final Holder holder = this.holders.remove(index);
      this.holderGroup.remove(holder.getClass(), holder);
      iterator.remove();
    }
  }
}
