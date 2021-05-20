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
package space.vectrix.inertia.util.counter;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

/* package */ final class IndexCounterImpl implements IndexCounter {
  private final AtomicInteger counter = new AtomicInteger();
  private final Object lock = new Object();
  private final String identifier;
  private final IntSet indexes;

  /* package */ IndexCounterImpl(final @NonNull String identifier, final @NonNull IntSet indexes) {
    this.identifier = identifier;
    this.indexes = indexes;
  }

  @Override
  public @NonNull AtomicInteger counter() {
    return this.counter;
  }

  @Override
  public int next() throws UnavailableIndexException {
    synchronized(this.lock) {
      int lap = 0;
      for(; ; ) {
        int index = this.counter().getAndIncrement();
        if(!indexes.contains(index)) return index;
        if(index == Integer.MAX_VALUE) {
          if(lap++ > 0) throw new UnavailableIndexException("Reached maximum index space for counter '" + this.identifier + "'!");
        }
      }
    }
  }

  @Override
  public <T> T next(final @NonNull IntFunction<T> consumer) throws UnavailableIndexException {
    synchronized(this.lock) {
      int lap = 0;
      for(; ; ) {
        int index = this.counter().getAndIncrement();
        if(!indexes.contains(index)) {
          return consumer.apply(index);
        }
        if(index == Integer.MAX_VALUE) {
          if(lap++ > 0) throw new UnavailableIndexException("Reached maximum index space for counter '" + this.identifier + "'!");
        }
      }
    }
  }
}
