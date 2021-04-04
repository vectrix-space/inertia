package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.injector.MemberInjector;

/**
 * The component resolver.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public interface ComponentResolver<H, C> {
  /**
   * Resolves the {@link ComponentType} from the specified {@link Class}
   * type.
   *
   * @param type The component class
   * @return The component type
   * @since 0.1.0
   */
  @NonNull ComponentType resolve(final @NonNull Class<?> type);

  /**
   * Creates the {@code T} component for the specified {@code H} holder
   * using the {@link ComponentType} and {@link MemberInjector}s.
   *
   * @param holder The holder
   * @param componentType The component type
   * @param componentInjector The component injector
   * @param holderInjector The holder injector
   * @param <T> The component type
   * @return The component
   * @since 0.1.0
   */
  <T extends C> @NonNull T create(final @NonNull H holder,
                                  final @NonNull ComponentType componentType,
                                  final MemberInjector.@NonNull Factory<?, C> componentInjector,
                                  final MemberInjector.@NonNull Factory<?, H> holderInjector);

  /**
   * The factory for creating an {@link ComponentResolver}.
   *
   * @since 0.1.0
   */
  @FunctionalInterface
  interface Factory {
    /**
     * Creates a new {@link ComponentResolver} for the specified {@link Universe}.
     *
     * @param universe The universe
     * @param <H> The holder type
     * @param <C> The component type
     * @return The component resolver
     * @since 0.1.0
     */
    <H, C> @NonNull ComponentResolver<H, C> create(final @NonNull Universe<H, C> universe);
  }
}
