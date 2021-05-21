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
