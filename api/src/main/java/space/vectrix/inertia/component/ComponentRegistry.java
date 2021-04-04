package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The component registry.
 *
 * @since 0.1.0
 */
public interface ComponentRegistry {
  /**
   * Returns the {@link ComponentType} with the specified {@code int}
   * index, if it exists.
   *
   * @param index The component index
   * @return The component type, if present
   * @since 0.1.0
   */
  @Nullable ComponentType get(final int index);

  /**
   * Returns the {@link ComponentType} with the specified {@link Class}
   * type, if it exists.
   *
   * @param type The component class
   * @return The component type, if present
   * @since 0.1.0
   */
  @Nullable ComponentType get(final @NonNull Class<?> type);

  /**
   * Returns the {@link ComponentType} with the specified {@link String}
   * identifier, if it exists.
   *
   * @param identifier The component identifier
   * @return The component type, if present
   * @since 0.1.0
   */
  @Nullable ComponentType get(final @NonNull String identifier);
}
