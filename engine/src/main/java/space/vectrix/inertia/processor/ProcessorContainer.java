package space.vectrix.inertia.processor;

import net.kyori.coffee.math.range.i.IntRange;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;

/**
 * The processor registry.
 *
 * @since 0.2.0
 */
public interface ProcessorContainer {
  /**
   * Returns the {@code T} processor with the specified {@link Class}
   * type, if it exists.
   *
   * @param type The processor class
   * @param <T> The processor type
   * @return The processor instance, if present
   * @since 0.2.0
   */
  <T extends Processor> @Nullable T getProcessor(final @NonNull Class<T> type);

  /**
   * Adds the {@code T} processor to this container.
   *
   * @param processor the processor
   * @param <T> the specific processor type
   * @since 0.2.0
   */
  <T extends Processor> void addProcessor(final @NonNull T processor);

  /**
   * Returns a {@link Collection} of {@link Processor}s with the
   * specified {@code int} priority.
   *
   * @param priority The priority
   * @return A collection of processor instances
   * @since 0.2.0
   */
  @NonNull Collection<? extends Processor> processors(final int priority);

  /**
   * Returns a {@link Collection} of {@link Processor}s with
   * the specified {@link IntRange} priority.
   *
   * @param priorities The range of priorities
   * @return A collection of processor instances
   * @since 0.2.0
   */
  @NonNull Collection<? extends Processor> processors(final @NonNull IntRange priorities);

  /**
   * Returns a {@link Collection} of {@link Processor}s in this
   * registry.
   *
   * @return A collection of processor instances
   * @since 0.2.0
   */
  @NonNull Collection<? extends Processor> processors();
}
