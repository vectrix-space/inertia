package space.vectrix.inertia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a dependency to be injected when instantiated.
 *
 * @since 0.2.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
  /**
   * Returns {@code true} if this dependency should only be injected if it
   * already exists, otherwise {@code false}.
   *
   * @return whether the injection is optional
   * @since 0.2.0
   */
  boolean optional() default false;
}
