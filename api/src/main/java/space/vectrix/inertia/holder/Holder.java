package space.vectrix.inertia.holder;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.component.ComponentType;

import java.util.Optional;

/**
 * Represents a holder of components.
 *
 * @param <C> The component type
 * @since 0.1.0
 */
public interface Holder<C> {
  /**
   * Returns the holder index.
   *
   * @return The holder index
   * @since 0.1.0
   */
  int getIndex();

  /**
   * Adds the specified {@code T} component to this holder and returns
   * {@code true} if the component was added, otherwise {@code false}.
   *
   * @param type The component type
   * @param component The component
   * @param <T> The component type
   * @return True if the component was added, otherwise false
   * @since 0.1.0
   */
  <T extends C> boolean addComponent(final @NonNull ComponentType type, final @NonNull T component);

  /**
   * Returns the {@code T} component with the specified {@code index}, if
   * it exists.
   *
   * @param index The component index
   * @param <T> The component type
   * @return The component, if present
   * @since 0.1.0
   */
  <T extends C> @NonNull Optional<T> getComponent(final int index);

  /**
   * Returns the {@code T} component with the specified {@link Class}, if
   * it exists.
   *
   * @param type The component class
   * @param <T> The component type
   * @return The component, if present
   * @since 0.1.0
   */
  <T extends C> @NonNull Optional<T> getComponent(final @NonNull Class<T> type);

  /**
   * Returns the {@code T} component with the specified {@link String}, if
   * it exists.
   *
   * @param identifier The component identifier
   * @param <T> The component type
   * @return The component, if present
   * @since 0.1.0
   */
  <T extends C> @NonNull Optional<T> getComponent(final @NonNull String identifier);

  /**
   * Returns {@code true} if it removed the component with the specified
   * {@link ComponentType}, otherwise returns false.
   *
   * @param type The component type
   * @return True if the component was removed, otherwise false
   * @since 0.1.0
   */
  boolean removeComponent(final @NonNull ComponentType type);

  /**
   * Clears the components in this holder.
   *
   * @since 0.1.0
   */
  void clearComponent();
}
