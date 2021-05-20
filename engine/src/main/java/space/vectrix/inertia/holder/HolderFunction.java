package space.vectrix.inertia.holder;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;

/**
 * Represents a function used to create a new {@link Holder}.
 *
 * @param <T> the holder type
 * @since 0.2.0
 */
@FunctionalInterface
public interface HolderFunction<T extends Holder> {
  /**
   * Creates a new {@link Holder} for the specified {@link Universe} with the
   * specified {@code index}.
   *
   * @param universe the universe
   * @param index the index
   * @return the holder
   * @since 0.2.0
   */
  T apply(final @NonNull Universe universe, final int index);
}
