package space.vectrix.inertia;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a component dependency to be injected when instantiated.
 *
 * @since 0.1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentDependency {
  /**
   * Determines whether the annotated component field, requires an injection
   * on instantiation. (Defaults: true)
   *
   * @return Whether the component is required
   * @since 0.1.0
   */
  boolean required() default true;
}
