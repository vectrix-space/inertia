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
package space.vectrix.inertia.processor;

import net.kyori.coffee.math.range.i.IntRange;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.flare.SyncMap;
import space.vectrix.inertia.holder.Holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ProcessorsImpl<H extends Holder<C>, C> extends AbstractProcessors<H, C> {
  private final Map<Class<? extends Processor<H, C>>, Processor<H, C>> processors = SyncMap.of(IdentityHashMap::new, 10);

  public ProcessorsImpl() {}

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull <T extends Processor<H, C>> Optional<T> get(final @NonNull Class<T> type) {
    return Optional.ofNullable((T) this.processors.get(type));
  }

  @Override
  public @NonNull Collection<? extends Processor<H, C>> all(final int priority) {
    final List<Processor<H, C>> processors = new ArrayList<>(this.processors.size());
    for(final Processor<H, C> processor : this.processors.values()) {
      if(processor.priority() == priority) processors.add(processor);
    }
    return processors;
  }

  @Override
  public @NonNull Collection<? extends Processor<H, C>> all(final @NonNull IntRange priorities) {
    final List<Processor<H, C>> processors = new ArrayList<>(this.processors.size());
    for(final Processor<H, C> processor : this.processors.values()) {
      if(priorities.test(processor.priority())) processors.add(processor);
    }
    return processors;
  }

  @Override
  public @NonNull Collection<? extends Processor<H, C>> all() {
    return Collections.unmodifiableCollection(this.processors.values());
  }

  @Override
  public @NonNull Iterator<Processor<H, C>> iterator() {
    return this.processors.values().iterator();
  }

  @Override
  public <T extends Processor<H, C>> boolean put(final Class<T> type, final @NonNull T processor) {
    return this.processors.putIfAbsent(type, processor) == null;
  }

  @Override
  public boolean remove(final @NonNull Class<? extends Processor<H, C>> type) {
    return this.processors.remove(type) != null;
  }
}
