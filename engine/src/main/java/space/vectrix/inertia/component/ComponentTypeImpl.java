package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.util.version.Version;

import java.util.Objects;

/* package */ final class ComponentTypeImpl implements ComponentType {
  private final Version version;
  private final String id;
  private final String name;
  private final Class<?> type;

  /* package */ ComponentTypeImpl(final @NonNull Version version,
                                  final @NonNull String id,
                                  final @NonNull String name,
                                  final @NonNull Class<?> type) {
    this.version = version;
    this.id = id;
    this.name = name;
    this.type = type;
  }

  @Override
  public @NonNull Version version() {
    return this.version;
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
    return this.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.version(), this.id(), this.name(), this.type());
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if(other == this) return true;
    if(!(other instanceof ComponentType)) return false;
    final ComponentType that = (ComponentType) other;
    return Objects.equals(this.version(), that.version())
      && Objects.equals(this.id(), that.id())
      && Objects.equals(this.name(), that.name())
      && Objects.equals(this.type(), that.type());
  }

  @Override
  public String toString() {
    return "ComponentType{version=" + this.version +
      ", id=" + this.id + ", name=" + this.name +
      ", type=" + this.type + "}";
  }
}
