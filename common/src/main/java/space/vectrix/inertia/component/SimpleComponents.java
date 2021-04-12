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
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.flare.SyncMap;
import space.vectrix.inertia.component.type.ComponentType;
import space.vectrix.inertia.holder.Holder;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class SimpleComponents<H extends Holder<C>, C> extends AbstractComponents<H, C> {
  private final Int2ObjectMap<C> componentInstances = new Int2ObjectOpenHashMap<>(100);
  private final Int2ObjectMap<Collection<C>> componentInstancesGrouped = new Int2ObjectOpenHashMap<>(10);

  @Override
  @SuppressWarnings("unchecked")
  public <T extends C> @NonNull Optional<T> get(final int holder, final @NonNull ComponentType componentType) {
    requireNonNull(componentType, "componentType");
    return Optional.ofNullable((T) this.componentInstances.get(this.getCombinedIndex(holder, componentType.index())));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends C> @NonNull Optional<T> get(final @NonNull H holder, final @NonNull ComponentType componentType) {
    requireNonNull(componentType, "componentType");
    return Optional.ofNullable((T) this.componentInstances.get(this.getCombinedIndex(holder.getIndex(), componentType.index())));
  }

  @Override
  public @NonNull Collection<? extends C> all(final int holder) {
    final Collection<C> components = this.componentInstancesGrouped.get(holder);
    if(components == null) return Collections.emptySet();
    return components;
  }

  @Override
  public @NonNull Collection<? extends C> all(final @NonNull H holder) {
    requireNonNull(holder, "holder");
    return this.all(holder.getIndex());
  }

  @Override
  public @NonNull Collection<? extends C> all() {
    return this.componentInstances.values();
  }

  @Override
  public <T extends C> boolean put(final int holder, final @NonNull ComponentType componentType, final @NonNull T component) {
    final int index = this.getCombinedIndex(holder, componentType.index());
    if(this.componentInstances.putIfAbsent(index, component) == null) {
      this.componentInstancesGrouped.computeIfAbsent(holder, key -> SyncMap.hashset()).add(component);
      return true;
    }
    return false;
  }

  @Override
  public boolean remove(final int holder, final @NonNull ComponentType componentType) {
    final int index = this.getCombinedIndex(holder, componentType.index());
    return this.removeInstance(holder, index);
  }

  private boolean removeInstance(final int holder, final int combinedIndex) {
    final C component = this.componentInstances.remove(combinedIndex);
    if(component != null) {
      final Collection<C> components = this.componentInstancesGrouped.get(holder);
      if (components != null) components.remove(component);
      return true;
    }
    return false;
  }

  private int getCombinedIndex(final int holder, final int component) {
    return (component << 8) + holder;
  }
}
