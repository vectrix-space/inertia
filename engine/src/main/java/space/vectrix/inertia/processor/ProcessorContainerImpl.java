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
