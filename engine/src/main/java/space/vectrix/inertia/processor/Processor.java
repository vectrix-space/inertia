package space.vectrix.inertia.processor;

import static java.util.Objects.requireNonNull;

import net.kyori.coffee.Ordered;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a processor of holders.
 *
 * @since 0.2.0
 */
@FunctionalInterface
public interface Processor extends Ordered<Processor> {
  /**
   * Returns an {@code int} for this processors priority.
   *
   * @return The processor priority
   * @since 0.2.0
   */
  default int priority() {
    return 0;
  }

  /**
   * Returns {@code true} if this processor has been initialized, otherwise
   * returns {@code false}.
   *
   * <p>In the case you need a custom implementation of {@link Processor#initialize()},
   * this should return {@code false} by default, then be set {@code true} after
   * {@link Processor#initialize()} has been called.</p>
   *
   * @return Whether this processor has been initialized
   * @since 0.2.0
   */
  default boolean initialized() {
    return true;
  }

  /**
   * Called when the processor is initialized.
   *
   * <p>Requires {@link Processor#initialized()} to return {@code false}
   * to be run in the processing loop.</p>
   *
   * @throws Throwable when there is an issue processing
   * @since 0.2.0
   */
  default void initialize() throws Throwable {}

  /**
   * Called when the processor is in the preparation stage at the
   * beginning of each game tick.
   *
   * @throws Throwable when there is an issue processing
   * @since 0.2.0
   */
  default void prepare() throws Throwable {}

  /**
   * Called when the processor is in the execution stage in the
   * middle of each game tick.
   *
   * @throws Throwable when there is an issue processing
   * @since 0.2.0
   */
  void execute() throws Throwable;

  @Override
  default int compareTo(final @NonNull Processor other) {
    requireNonNull(other, "other");
    return Integer.compare(this.priority(), other.priority());
  }
}
