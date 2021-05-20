package space.vectrix.inertia.internal;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.flare.fastutil.Int2ObjectSyncMap;
import space.vectrix.flare.fastutil.Long2ObjectSyncMap;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.component.ComponentContainer;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.injection.InjectionMethod;
import space.vectrix.inertia.injection.InjectionStructure;
import space.vectrix.inertia.util.version.Version;

import java.util.Map;
import java.util.Optional;

public final class InternalComponentContainer implements ComponentContainer {
  private final IntSet componentRemovals = Int2ObjectSyncMap.hashset(50);
  private final Long2ObjectMap<Object> components = Long2ObjectSyncMap.hashmap(1000);
  private final Int2ObjectSyncMap<Int2ObjectSyncMap<Object>> componentHolders = Int2ObjectSyncMap.hashmap(500);
  private final Int2ObjectSyncMap<InjectionStructure> componentStructures = Int2ObjectSyncMap.hashmap(500);
  private final InjectionStructure.Factory injectionStructure;
  private final InjectionMethod.Factory injectionMethod;
  private final Universe universe;

  public InternalComponentContainer(final @NonNull Universe universe,
                                    final InjectionStructure.@NonNull Factory injectionStructure,
                                    final InjectionMethod.@NonNull Factory injectionMethod) {
    this.injectionStructure = injectionStructure;
    this.injectionMethod = injectionMethod;
    this.universe = universe;
  }

  @Override
  public boolean containsComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    final Version holderVersion = holder.version();
    final Version componentVersion = componentType.version();
    if(!holderVersion.belongs(this.universe, componentVersion)) return false;
    return this.components.containsKey(this.getCombinedIndex(holderVersion.index(), componentVersion.index()));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @Nullable T getComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    final Version holderVersion = holder.version();
    final Version componentVersion = componentType.version();
    if(!holderVersion.belongs(this.universe, componentVersion)) return null;
    return (T) this.components.get(this.getCombinedIndex(holderVersion.index(), componentVersion.index()));
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull <T> Optional<T> getPresentComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    final Version holderVersion = holder.version();
    final Version componentVersion = componentType.version();
    if(!holderVersion.belongs(this.universe, componentVersion)) return Optional.empty();
    return Optional.ofNullable((T) this.components.get(this.getCombinedIndex(holderVersion.index(), componentVersion.index())));
  }

  @Override
  public <T> @NonNull T addComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @Nullable T removeComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    final Version holderVersion = holder.version();
    final Version componentVersion = componentType.version();
    if(!holderVersion.belongs(this.universe, componentVersion) && !this.componentRemovals.add(componentVersion.index())) return null;
    return (T) this.components.get(this.getCombinedIndex(holderVersion.index(), componentVersion.index()));
  }

  @SuppressWarnings("unchecked")
  private <T> T create(final @NonNull Holder holder,
                       final @NonNull ComponentType componentType,
                       final @Nullable ComponentType parentType) {
    final Version holderVersion = holder.version();
    final Version componentVersion = componentType.version();
    final Class<?> componentClass = componentType.getClass();
    if(!holderVersion.belongs(this.universe, componentVersion)) return null;

    final Int2ObjectSyncMap<Object> components = this.componentHolders.computeIfAbsent(holderVersion.index(), ignored -> Int2ObjectSyncMap.hashmap(500));
    T componentInstance = (T) components.get(componentVersion.index());
    if(componentInstance == null) {
      componentInstance = (T) components.computeIfAbsent(componentVersion.index(), key -> this.createInstance(componentClass));
      this.components.put(this.getCombinedIndex(holderVersion.index(), componentVersion.index()), componentInstance);

      final InjectionStructure structure = this.componentStructures.get(componentVersion.index());
      if(structure != null) {
        for(final Map.Entry<Class<?>, InjectionStructure.Entry> entry : structure.injectors().entrySet()) {
          final Class<?> target = entry.getKey();
          final InjectionStructure.Entry injectionEntry = entry.getValue();

          // Holder Injection
          if(target.isInstance(holder)) {
            this.injectMember(injectionEntry.method(), componentInstance, holder);
            continue;
          }

          // Component Injection
          // TODO
        }
      }
    }
    return componentInstance;
  }

  private void injectMember(final @NonNull InjectionMethod injector, final @NonNull Object target, final @NonNull Object member) {
    try {
      injector.member(target, member);
    } catch(final Throwable throwable) {
      throw new IllegalStateException("Unable to inject member.", throwable);
    }
  }

  private Object createInstance(final @NonNull Class<?> componentClass) {
    try {
      return componentClass.getDeclaredConstructor().newInstance();
    } catch(final Throwable exception) {
      throw new IllegalStateException("Unable to instantiate component.", exception);
    }
  }

  private long getCombinedIndex(final int holder, final int component) {
    return ((long) component << 32) + holder;
  }
}
