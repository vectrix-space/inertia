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
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.flare.SyncMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ProcessorContainerImpl implements ProcessorContainer, ProcessingSystem {
  private final Map<Class<? extends Processor>, Processor> processors = SyncMap.of(IdentityHashMap::new, 50);

  public ProcessorContainerImpl() {}

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Processor> @Nullable T getProcessor(final @NonNull Class<T> type) {
    return (T) this.processors.get(type);
  }

  @Override
  public <T extends Processor> void addProcessor(final @NonNull T processor) {
    this.processors.put(processor.getClass(), processor);
  }

  @Override
  public @NonNull Collection<? extends Processor> processors(final int priority) {
    final List<Processor> processors = new ArrayList<>(this.processors.size());
    for(final Processor processor : this.processors.values()) {
      if(processor.priority() == priority) processors.add(processor);
    }
    return processors;
  }

  @Override
  public @NonNull Collection<? extends Processor> processors(final @NonNull IntRange priorities) {
    final List<Processor> processors = new ArrayList<>(this.processors.size());
    for(final Processor processor : this.processors.values()) {
      if(priorities.test(processor.priority())) processors.add(processor);
    }
    return processors;
  }

  @Override
  public @NonNull Collection<? extends Processor> processors() {
    return Collections.unmodifiableCollection(this.processors.values());
  }
}
