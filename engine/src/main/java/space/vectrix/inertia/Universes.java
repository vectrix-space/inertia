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
import space.vectrix.flare.fastutil.Int2ObjectSyncMap;
import space.vectrix.inertia.util.counter.IndexCounter;

import java.util.function.Consumer;

/* package */ final class Universes {
  private static final Int2ObjectSyncMap<Universe> UNIVERSES = Int2ObjectSyncMap.hashmap(100);
  private static final IndexCounter UNIVERSES_INDEX = IndexCounter.counter("universe", Universes.UNIVERSES);

  /* package */ static @NonNull Universe create(final @NonNull Consumer<Universe.Builder> builderConsumer) {
    return Universes.UNIVERSES_INDEX.next(index -> Universes.UNIVERSES.computeIfAbsent(index, key -> {
      final UniverseImpl.BuilderImpl builder = new UniverseImpl.BuilderImpl();
      builderConsumer.accept(builder);
      return builder.build(key);
    }));
  }

  /* package */ static @Nullable Universe get(final int index) {
    return Universes.UNIVERSES.get(index);
  }

  /* package */ static @Nullable Universe remove(final int index) {
    return Universes.UNIVERSES.remove(index);
  }
}
