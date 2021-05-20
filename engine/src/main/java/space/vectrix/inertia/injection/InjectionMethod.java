package space.vectrix.inertia.injection;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Functional interface that can inject members on a defined field on a target
 * object when needed.
 *
 * @since 0.2.0
 */
@FunctionalInterface
public interface InjectionMethod {
  /**
   * Injects the member at the appropriate field on the given target.
   *
   * @param target the target to inject into
   * @param member the member to inject
   * @throws Throwable if a problem occurred attempting to inject
   * @since 0.2.0
   */
  void member(final @NonNull Object target, final @NonNull Object member) throws Throwable;

  /**
   * The factory for creating an {@link InjectionMethod}.
   *
   * @since 0.2.0
   */
  @FunctionalInterface
  interface Factory {
    /**
     * Creates a new {@link InjectionMethod} for the specified input.
     *
     * @param input the target input
     * @return the injection method
     * @throws Throwable if a problem occurred attempting to inject
     * @since 0.2.0
     */
    @NonNull InjectionMethod create(final @NonNull Object input) throws Throwable;
  }
}
