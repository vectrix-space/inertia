package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.annotation.Component;
import space.vectrix.inertia.holder.Holder;

import java.util.Optional;

/**
 * The component container.
 *
 * @since 0.2.0
 */
public interface ComponentContainer {
  /**
   * Returns {@code true} if the specified {@link Holder} has a
   * {@link Component} instance for the specified {@link ComponentType}.
   *
   * @param holder the holder
   * @param componentType the component type
   * @return whether the component exists in this holder
   * @since 0.2.0
   */
  boolean containsComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType);

  /**
   * Returns the {@code T} component for the specified {@link Holder} with the
   * specified {@link ComponentType}, if it exists.
   *
   * @param holder the holder
   * @param componentType the component type
   * @param <T> the specific component type
   * @return the component, if present
   * @since 0.2.0
   */
  <T> @Nullable T getComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType);

  /**
   * Returns the {@code T} component for the specified {@link Holder} with the
   * specified {@link ComponentType}, if it exists.
   *
   * @param holder the holder
   * @param componentType the component type
   * @param <T> the specific component type
   * @return the component, if present
   * @since 0.2.0
   */
  <T> @NonNull Optional<T> getPresentComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType);

  /**
   * Returns the existing {@code T} or creates a new {@code T} component
   * for the specified {@link Holder} with the specified {@link ComponentType}.
   *
   * @param holder the holder
   * @param componentType the component type
   * @param <T> the specific component type
   * @return the present component, or a new component
   * @since 0.2.0
   */
  <T> @NonNull T addComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType);

  /**
   * Returns the {@code T} component if it exists and removes the specified
   * {@link ComponentType} from the specified {@link Holder}.
   *
   * @param holder the holder
   * @param componentType the component type
   * @param <T> the specific component type
   * @return the removed component, if present
   * @since 0.2.0
   */
  <T> @Nullable T removeComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType);
}
