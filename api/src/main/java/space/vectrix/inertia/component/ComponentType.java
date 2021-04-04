package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

/**
 * The component type.
 *
 * @since 0.1.0
 */
public interface ComponentType {
  /**
   * The component index.
   *
   * @return The index
   * @since 0.1.0
   */
  int index();

  /**
   * The component identifier.
   *
   * @return The identifier
   * @since 0.1.0
   */
  @NonNull String id();

  /**
   * The component name.
   *
   * @return The name
   * @since 0.1.0
   */
  @NonNull String name();

  /**
   * The component type.
   *
   * @return The type
   * @since 0.1.0
   */
  @NonNull Class<?> type();

  /**
   * The component required {@link ComponentType} dependencies.
   *
   * @return The required dependencies
   * @since 0.1.0
   */
  @NonNull Set<ComponentType> requiredDependencies();

  /**
   * The component optional {@link ComponentType} dependencies.
   *
   * @return The optional dependencies
   * @since 0.1.0
   */
  @NonNull Set<ComponentType> optionalDependencies();
}
