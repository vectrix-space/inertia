package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

public final class ComponentType {
  private final Set<ComponentType> requiredDependencies = new HashSet<>();
  private final Set<ComponentType> optionalDependencies = new HashSet<>();
  private final int index;
  private final Class<?> component;
  private final String id;
  private final String name;

  public ComponentType(final int index,
                       final Class<?> component,
                       final String id,
                       final String name) {
    this.index = index;
    this.component = component;
    this.id = id;
    this.name = name;
  }

  public int index() {
    return this.index;
  }

  public @NonNull Class<?> type() {
    return this.component;
  }

  public @NonNull Set<ComponentType> requiredDependencies() {
    return this.requiredDependencies;
  }

  public @NonNull Set<ComponentType> optionalDependencies() {
    return this.optionalDependencies;
  }

  public @NonNull String id() {
    return this.id;
  }

  public @NonNull String name() {
    return this.name;
  }
}
