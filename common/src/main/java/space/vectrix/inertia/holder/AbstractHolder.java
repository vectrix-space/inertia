package space.vectrix.inertia.holder;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.component.ComponentType;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class AbstractHolder<C> implements Holder<C> {
  private final Int2ObjectMap<C> components = new Int2ObjectOpenHashMap<>(100);
  private final Map<Class<?>, C> componentsTyped = new IdentityHashMap<>(50);
  private final Map<String, C> componentsNamed = new HashMap<>(50);
  private final int index;

  protected AbstractHolder(final int index) {
    this.index = index;
  }

  @Override
  public int getIndex() {
    return this.index;
  }

  @Override
  public <T extends C> boolean addComponent(final @NonNull ComponentType type, final @NonNull T component) {
    if (this.components.putIfAbsent(type.index(), component) == null) {
      this.componentsTyped.put(type.type(), component);
      this.componentsNamed.put(type.id(), component);
      return true;
    }
    return false;
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final int index) {
    return Optional.ofNullable((T) this.components.get(index));
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final @NonNull Class<T> type) {
    return Optional.ofNullable((T) this.componentsTyped.get(type));
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final @NonNull String identifier) {
    return Optional.ofNullable((T) this.componentsNamed.get(identifier));
  }

  @Override
  public boolean removeComponent(final @NonNull ComponentType type) {
    if (this.components.remove(type.index()) != null) {
      this.componentsTyped.remove(type.type());
      this.componentsNamed.remove(type.name());
      return true;
    }
    return false;
  }

  @Override
  public void clearComponent() {
    this.components.clear();
    this.componentsTyped.clear();
    this.componentsNamed.clear();
  }
}
