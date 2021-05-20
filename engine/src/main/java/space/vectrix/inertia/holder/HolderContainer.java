package space.vectrix.inertia.holder;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface HolderContainer {
  /**
   * Returns a new {@link Holder}.
   *
   * @return the new holder
   * @since 0.2.0
   */
  @NonNull Holder createHolder();

  /**
   * Returns a new {@link Holder} of type {@code T} using the specified
   * {@link HolderFunction}.
   *
   * @param function the holder function
   * @param <T> the specific holder type
   * @return the new holder
   * @since 0.2.0
   */
  <T extends Holder> @NonNull T createHolder(final @NonNull HolderFunction<T> function);

  /**
   * Returns whether the specified {@link Holder} is a valid participant.
   *
   * @param holder the holder
   * @return whether the holder is valid
   * @since 0.2.0
   */
  boolean valid(final @NonNull Holder holder);
}
