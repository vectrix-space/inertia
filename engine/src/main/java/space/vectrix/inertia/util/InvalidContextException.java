package space.vectrix.inertia.util;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Thrown when an object is being used in the wrong context.
 *
 * @since 0.2.0
 */
public class InvalidContextException extends IllegalStateException {
  public InvalidContextException(final @NonNull String context, final @NonNull String details) {
    super(context + ": " + details);
  }
}
