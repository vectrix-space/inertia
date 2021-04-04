package space.vectrix.inertia.injector;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;

/**
 * Functional interface that can inject members on a defined field on a target
 * object when needed.
 *
 * @param <T> The target type
 * @param <M> The member type
 */
@FunctionalInterface
public interface MemberInjector<T, M> {
  /**
   * Injects the member at the appropriate field on the given target.
   *
   * @param target The target to inject into
   * @param member The member to inject
   * @throws Throwable If an exception occurred
   */
  void member(final @NonNull T target, final @NonNull M member) throws Throwable;

  /**
   * The factory for creating an {@link MemberInjector}.
   *
   * @param <T> The target type
   * @param <M> The member type
   */
  @FunctionalInterface
  interface Factory<T, M> {
    /**
     * Creates a new {@link MemberInjector} for the specified {@link Object}
     * target and {@link Field} field.
     *
     * @param object The target object
     * @param field The target field
     * @return A new member injector
     * @throws Exception If an exception occurred
     */
    @NonNull MemberInjector<T, M> create(final @NonNull Object object, final @NonNull Field field) throws Exception;
  }
}
