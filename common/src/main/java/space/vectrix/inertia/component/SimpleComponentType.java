package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

public final class SimpleComponentType implements ComponentType {
  private final Set<ComponentType> requiredDependencies = new HashSet<>();
  private final Set<ComponentType> optionalDependencies = new HashSet<>();
  private final int index;
  private final String id;
  private final String name;
  private final Class<?> component;

  public SimpleComponentType(final int index,
                             final String id,
                             final String name,
                             final Class<?> component) {
    this.index = index;
    this.id = id;
    this.name = name;
    this.component = component;
  }

  @Override
  public int index() {
    return this.index;
  }

  @Override
  public @NonNull String id() {
    return this.id;
  }

  @Override
  public @NonNull String name() {
    return this.name;
  }

  @Override
  public @NonNull Class<?> type() {
    return this.component;
  }

  @Override
  public @NonNull Set<ComponentType> requiredDependencies() {
    return this.requiredDependencies;
  }

  @Override
  public @NonNull Set<ComponentType> optionalDependencies() {
    return this.optionalDependencies;
  }
}
