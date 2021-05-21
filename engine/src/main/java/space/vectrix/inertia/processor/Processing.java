package space.vectrix.inertia.processor;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;

import java.util.Collection;
import java.util.function.Function;

/**
 * The processing system.
 *
 * @since 0.2.0
 */
public interface Processing {
  /**
   * Ticks all the processors contained within this {@link Universe}.
   *
   * @return The tick index
   * @since 0.2.0
   */
  int process(final @NonNull Collection<ProcessingSystem> systems);

  /**
   * Handles any {@link Throwable}s caused in the processing phases.
   *
   * @param throwable The processing exception
   * @since 0.2.0
   */
  void processException(final @NonNull Throwable throwable);

  /**
   * The factory for creating a {@link Processing} system.
   *
   * @since 0.2.0
   */
  @FunctionalInterface
  interface Factory {
    /**
     * Creates a new {@link Processing} system for the specified {@link Universe}.
     *
     * @param universe The universe
     * @return The processing system
     * @since 0.2.0
     */
    @NonNull Processing create(final @NonNull Universe universe);
  }
}
