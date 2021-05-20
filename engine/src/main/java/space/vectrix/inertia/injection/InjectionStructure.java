package space.vectrix.inertia.injection;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.annotation.Inject;

import java.util.Map;

/**
 * Represents the dependency structure of the specified target.
 *
 * @since 0.2.0
 */
public interface InjectionStructure {
  /**
   * Returns a {@link Map} of injectors.
   *
   * @return a map of injectors
   * @since 0.2.0
   */
  @NonNull Map<Class<?>, Entry> injectors();

  /**
   * A dependency structure entry.
   *
   * @since 0.2.0
   */
  interface Entry {
    /**
     * Returns the {@link Inject} dependency annotation.
     *
     * @return the dependency annotation
     * @since 0.2.0
     */
    @NonNull Inject annotation();

    /**
     * Returns the {@link InjectionMethod} for the dependency.
     *
     * @return the injection method
     * @since 0.2.0
     */
    @NonNull InjectionMethod method();
  }

  /**
   * The factory for creating {@link InjectionStructure}.
   *
   * @since 0.2.0
   */
  interface Factory {
    /**
     * Creates a new {@link InjectionStructure} for the specified {@link Class}
     * target and specified {@link InjectionMethod.Factory}s.
     *
     * @param target the target
     * @param injectionFactory the injection factory
     * @return the injection structure
     * @since 0.2.0
     */
    @NonNull InjectionStructure create(final @NonNull Class<?> target,
                                       final InjectionMethod.@NonNull Factory injectionFactory);
  }
}
