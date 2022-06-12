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
package space.vectrix.inertia.entity;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.flare.fastutil.Int2ObjectSyncMap;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.util.CustomIterator;

/* package */ final class EntityStashImpl implements EntityStash {
  private final IntSet stash = Int2ObjectSyncMap.hashset();
  private final Universe universe;

  /* package */ EntityStashImpl(final @NonNull Universe universe) {
    this.universe = universe;
  }

  @Override
  public boolean contains(final @NonNegative int index) {
    return this.stash.contains(index);
  }

  @Override
  public @Nullable Entity get(final @NonNegative int index) {
    if(!this.stash.contains(index)) return null;
    return this.universe.getEntity(index);
  }

  @Override
  public <T extends Entity> @Nullable T get(final @NonNegative int index, final @NonNull Class<T> target) {
    if(!this.stash.contains(index)) return null;
    return this.universe.getEntity(index, target);
  }

  @Override
  public boolean add(final @NonNegative int index) {
    return this.stash.add(index);
  }

  @Override
  public boolean remove(final @NonNegative int index) {
    return this.stash.remove(index);
  }

  @Override
  public void clear() {
    this.stash.clear();
  }

  @Override
  public @NonNull <T extends Entity> CustomIterator<T> iterator(final @NonNull Class<? super T> type) {
    return CustomIterator.of(
      this.stash.iterator(),
      index -> this.universe.getEntity(index, type),
      entity -> this.remove(entity.index())
    );
  }

  @Override
  public @NonNull CustomIterator<Entity> iterator() {
    return CustomIterator.of(
      this.stash.iterator(),
      this.universe::getEntity,
      entity -> this.remove(entity.index())
    );
  }
}
