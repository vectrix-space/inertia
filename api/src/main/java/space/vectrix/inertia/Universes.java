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
package space.vectrix.inertia;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.flare.SyncMap;
import space.vectrix.inertia.holder.Holder;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unchecked")
/* package */ final class Universes {
  private static final SyncMap<String, Universe<?, ?>> UNIVERSES = SyncMap.hashmap(5);

  /* package */ static <H extends Holder<C>, C> @NonNull Universe<H, C> compute(final @NonNull Universe<H, C> universe) {
    return (Universe<H, C>) Universes.UNIVERSES.computeIfAbsent(universe.id(), key -> universe);
  }

  /* package */ static <H extends Holder<C>, C> @Nullable Universe<H, C> get(final @NonNull String identifier) {
    return (Universe<H, C>) Universes.UNIVERSES.get(identifier);
  }

  /* package */ static <H extends Holder<C>, C> @Nullable Universe<H, C> remove(final @NonNull String identifier) {
    return (Universe<H, C>) Universes.UNIVERSES.remove(identifier);
  }

  /* package */ static @NonNull Collection<Universe<?, ?>> getAll() {
    return Collections.unmodifiableCollection(Universes.UNIVERSES.values());
  }
}
