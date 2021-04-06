package space.vectrix.inertia.holder;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;

/**
 * The holder resolver.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public interface HolderResolver<H extends Holder<C>, C> {
  /**
   * Creates the {@code T} holder using the specified {@link HolderFunction}.
   *
   * @param holderFunction The holder function
   * @param <T> The specific holder type
   * @return The holder
   * @since 0.1.0
   */
  <T extends H> @NonNull T create(final @NonNull HolderFunction<H, C, T> holderFunction);

  /**
   * The function for creating a new holder from the supplied
   * {@link Universe} and {@code index}.
   *
   * @param <H> The holder type
   * @param <C> The component type
   * @param <T> The specific holder type
   * @since 0.1.0
   */
  @FunctionalInterface
  interface HolderFunction<H extends Holder<C>, C, T extends H> {
    /**
     * Creates a new {@link Holder} for the specified {@link Universe}
     * with the specified {@code index}.
     *
     * @param universe The universe
     * @param index The index
     * @return The new holder
     */
    T apply(final @NonNull Universe<H, C> universe, final int index);
  }

  /**
   * The factory for created an {@link HolderResolver}.
   *
   * @since 0.1.0
   */
  @FunctionalInterface
  interface Factory {
    /**
     * Creates a new {@link HolderResolver} for the specified {@link Universe}.
     *
     * @param universe The universe
     * @param <H> The holder type
     * @param <C> The component type
     * @return The holder resolver
     * @since 0.1.0
     */
    <H extends Holder<C>, C> @NonNull HolderResolver<H, C> create(final @NonNull Universe<H, C> universe);
  }
}
