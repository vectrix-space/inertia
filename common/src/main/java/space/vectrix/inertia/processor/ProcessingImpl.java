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

import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.holder.Holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public final class ProcessingImpl<H extends Holder<C>, C> implements Processing<H, C> {
  private final AtomicInteger index = new AtomicInteger();
  private final Universe<H, C> universe;

  /* package */ ProcessingImpl(final Universe<H, C> universe) {
    this.universe = universe;
  }

  @Override
  public <T extends Processor<H, C>> @NonNull T create(final @NonNull Class<T> type, final @NonNull Function<Universe<H, C>, T> processorFunction) {
    requireNonNull(type, "type");
    requireNonNull(processorFunction, "processorFunction");
    final AbstractProcessors<H, C> processors = (AbstractProcessors<H, C>) this.universe.processors();
    final T processorInstance = processorFunction.apply(this.universe);
    processors.put(type, processorInstance);
    return processorInstance;
  }

  @Override
  public int process() {
    final AbstractProcessors<H, C> processors = (AbstractProcessors<H, C>) this.universe.processors();
    final int index = this.index.getAndIncrement();
    final List<Processor<H, C>> collection = new ArrayList<>(processors.all());
    Collections.sort(collection);
    for(final Processor<H, C> processor : collection) {
      boolean run = true;
      try {
        if(!processor.initialized()) processor.initialize();
      } catch(final Throwable throwable) {
        run = false;
        this.processException(throwable);
      }
      try {
        if(run) processor.prepare();
      } catch(final Throwable throwable) {
        run = false;
        this.processException(throwable);
      }
      try {
        if(run) processor.execute();
      } catch(final Throwable throwable) {
        this.processException(throwable);
      }
    }
    return index;
  }

  @Override
  public void processException(final @NonNull Throwable throwable) {
    final RuntimeException printer = new RuntimeException("Encountered a processing error: ", throwable);
    printer.printStackTrace();
  }

  public static final class Factory implements Processing.Factory {
    @Override
    public @NonNull <H extends Holder<C>, C> Processing<H, C> create(final @NonNull Universe<H, C> universe) {
      requireNonNull(universe, "universe");
      return new ProcessingImpl<>(universe);
    }
  }
}
