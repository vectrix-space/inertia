package space.vectrix.inertia;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a class that is to behave like as a component.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
  /**
   * The component identifier.
   *
   * <p>The identifier must match the format [a-Z0-9_-].</p>
   *
   * @return The component identifier
   */
  String id();

  /**
   * The component name.
   *
   * @return The component name
   */
  String name();
}
