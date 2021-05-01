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
package space.vectrix.inertia.component;

import static java.util.Objects.requireNonNull;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.flare.SyncMap;
import space.vectrix.inertia.holder.Holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ComponentsImpl<H extends Holder<C>, C> extends AbstractComponents<H, C> {
  private final SyncMap<Long, C> components = SyncMap.hashmap(100);
  private final Int2ObjectMap<Set<Integer>> holders = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>(10));

  public ComponentsImpl() {}

  @Override
  @SuppressWarnings("unchecked")
  public <T extends C> @NonNull Optional<T> get(final int holder, final @NonNull ComponentType componentType) {
    requireNonNull(componentType, "componentType");
    return Optional.ofNullable((T) this.components.get(this.getCombinedIndex(holder, componentType.index())));
  }

  @Override
  public <T extends C> @NonNull Optional<T> get(final @NonNull H holder, final @NonNull ComponentType componentType) {
    requireNonNull(componentType, "componentType");
    return this.get(holder.index(), componentType);
  }

  @Override
  public @NonNull Collection<? extends C> all(final int holder) {
    final Set<Integer> components = this.holders.get(holder);
    if(components == null) return Collections.emptySet();
    final List<C> instances = new ArrayList<>(components.size());
    for(final int component : components) {
      instances.add(this.components.get(this.getCombinedIndex(holder, component)));
    }
    return instances;
  }

  @Override
  public @NonNull Collection<? extends C> all(final @NonNull H holder) {
    requireNonNull(holder, "holder");
    return this.all(holder.index());
  }

  @Override
  public @NonNull Collection<? extends C> all() {
    return this.components.values();
  }

  @Override
  public @NonNull Iterator<C> iterator() {
    return this.components.values().iterator();
  }

  @Override
  public <T extends C> boolean put(final int holder, final @NonNull ComponentType componentType, final @NonNull T component) {
    final long index = this.getCombinedIndex(holder, componentType.index());
    if (this.components.putIfAbsent(index, component) == null) {
      this.holders.computeIfAbsent(holder, key -> SyncMap.hashset()).add(componentType.index());
      return true;
    }
    return false;
  }

  @Override
  public boolean remove(final int holder, final @NonNull ComponentType componentType) {
    if (this.components.remove(this.getCombinedIndex(holder, componentType.index())) != null) {
      final Set<Integer> components = this.holders.get(holder);
      if (components != null) {
        components.remove(componentType.index());
        return true;
      }
    }
    return false;
  }

  @Override
  public void remove(final int holder) {
    final Set<Integer> components = this.holders.remove(holder);
    if (components != null) {
      for (final int component : components) {
        this.components.remove(this.getCombinedIndex(holder, component));
      }
    }
  }

  private long getCombinedIndex(final int holder, final int component) {
    return ((long) component << 32) + holder;
  }
}
