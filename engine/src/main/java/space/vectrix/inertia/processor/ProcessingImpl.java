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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class ProcessingImpl implements Processing {
  private final AtomicInteger index = new AtomicInteger();
  private final Universe universe;

  public ProcessingImpl(final @NonNull Universe universe) {
    this.universe = universe;
  }

  @Override
  public int process(final @NonNull Collection<ProcessingSystem> systems) {
    final int index = this.index.getAndIncrement();
    final List<Processor> collection = new ArrayList<>(this.universe.processors());
    Collections.sort(collection);
    for(final Processor processor : collection) {
      boolean run = true;
      // Initialize the processors.
      try {
        if(!processor.initialized()) processor.initialize();
      } catch(final Throwable throwable) {
        run = false;
        this.processException(throwable);
      }

      if(processor.initialized()) {
        // Prepare the processors.
        try {
          if(run) processor.prepare();
        } catch(final Throwable throwable) {
          run = false;
          this.processException(throwable);
        }

        // Execute the processors.
        try {
          if(run) processor.execute();
        } catch(final Throwable throwable) {
          this.processException(throwable);
        }

        // Process the systems.
        try {
          if(run) {
            for(final ProcessingSystem system : systems) {
              system.process();
            }
          }
        } catch(final Throwable throwable) {
          this.processException(throwable);
        }
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
    public @NonNull Processing create(final @NonNull Universe universe) {
      requireNonNull(universe, "universe");
      return new ProcessingImpl(universe);
    }
  }
}
