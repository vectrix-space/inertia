package space.vectrix.inertia.processor;

/**
 * Represents a system that can be processed.
 *
 * @since 0.2.0
 */
public interface ProcessingSystem {
  /**
   * Runs the systems processing functionality.
   *
   * @throws Throwable when there is an issue processing the system
   * @since 0.2.0
   */
  default void process() throws Throwable {}
}
