package space.vectrix.inertia;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.holder.Holder;

import java.util.Collection;
import java.util.function.Function;

/**
 * The main accessor to the component system.
 *
 * @since 0.1.0
 */
public final class Inertia {
  /**
   * Creates a new {@link Universe} from the specified {@link Universe.Builder}
   * type passed into the {@link Function}.
   *
   * @param universeBuilder The universe builder type
   * @param buildingFunction The universe building function
   * @param <H> The holder type
   * @param <C> The component type
   * @return The new universe
   * @since 0.1.0
   */
  public static <H extends Holder<C>, C> Universe<H, C> create(final @NonNull Class<? extends Universe.Builder<?, ?>> universeBuilder,
                                                               final @NonNull Function<Universe.Builder<H, C>, Universe<H, C>> buildingFunction) {
    final Universe.Builder<H, C> builder = Inertia.createBuilder(universeBuilder);
    final Universe<H, C> universe = buildingFunction.apply(builder);
    return Universes.compute(universe);
  }

  /**
   * Returns the {@link Universe} with the specified {@link String} identifier,
   * if it exists.
   *
   * @param identifier The universe identifier
   * @param <H> The holder type
   * @param <C> The component type
   * @return The universe, if present
   * @since 0.1.0
   */
  public static <H extends Holder<C>, C> @Nullable Universe<H, C> get(final @NonNull String identifier) {
    return Universes.get(identifier);
  }

  /**
   * Returns a {@link Collection} of the created {@link Universe}s.
   *
   * @return A collection of universes
   * @since 0.1.0
   */
  public static @NonNull Collection<Universe<?, ?>> getAll() {
    return Universes.getAll();
  }

  @SuppressWarnings("unchecked")
  private static <H extends Holder<C>, C> Universe.Builder<H, C> createBuilder(final @NonNull Class<? extends Universe.Builder<?, ?>> universeBuilder) {
    try {
      return (Universe.Builder<H, C>) universeBuilder.newInstance();
    } catch(final Throwable throwable) {
      throw new IllegalStateException("Unable to create universe builder!", throwable);
    }
  }
}
