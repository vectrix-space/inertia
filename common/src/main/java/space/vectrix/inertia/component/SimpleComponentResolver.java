package space.vectrix.inertia.component;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.Component;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.injector.MemberInjector;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("UnstableApiUsage")
public final class SimpleComponentResolver<H extends Holder<C>, C> implements ComponentResolver<H, C> {
  public static SimpleComponentResolver.Factory FACTORY = new SimpleComponentResolver.Factory();

  private final AtomicInteger index = new AtomicInteger();
  private final MutableGraph<ComponentType> componentDependencies = GraphBuilder.undirected()
    .allowsSelfLoops(false)
    .expectedNodeCount(1000)
    .build();

  private final Universe<H, C> universe;

  /* package */ SimpleComponentResolver(final Universe<H, C> universe) {
    this.universe = universe;
  }

  @Override
  public @NonNull ComponentType resolve(final @NonNull Class<?> type) {
    return this.resolve(null, type);
  }

  @Override
  public <T extends C> @NonNull T create(final @NonNull H holder,
                                         final @NonNull ComponentType componentType,
                                         final MemberInjector.@NonNull Factory<?, C> componentInjector,
                                         final MemberInjector.@NonNull Factory<?, H> holderInjector) {
    return this.create(holder, componentInjector, holderInjector, null, componentType);
  }

  private ComponentType resolve(final @Nullable ComponentType parent, final @NonNull Class<?> type) {
    final ComponentType componentType = ((SimpleComponentRegistry) this.universe.components()).computeIfAbsent(type, key -> {
      final Component component = type.getAnnotation(Component.class);
      if(component == null) throw new IllegalArgumentException("Target type must have a component annotation!");
      return new SimpleComponentType(this.index.getAndIncrement(), component.id(), component.name(), type, ComponentStructure.generate(type));
    });
    if(parent != null) {
      this.componentDependencies.putEdge(parent, componentType);
    }
    if(componentType instanceof SimpleComponentType) {
      final ComponentStructure structure = ((SimpleComponentType) componentType).structure();
      for (final Class<?> dependency : structure.getRequiredDependencies().keySet()) {
        componentType.requiredDependencies().add(this.resolve(componentType, dependency));
      }
      for (final Class<?> dependency : structure.getOptionalDependencies().keySet()) {
        componentType.optionalDependencies().add(this.resolve(componentType, dependency));
      }
    }
    return componentType;
  }

  @SuppressWarnings("unchecked")
  private <T extends C> T create(final @NonNull H holder,
                                 final MemberInjector.@NonNull Factory<?, C> componentInjector,
                                 final MemberInjector.@NonNull Factory<?, H> holderInjector,
                                 final @Nullable ComponentType parentType,
                                 final @NonNull ComponentType componentType) {
    final Class<?> componentClass = componentType.type();
    final T componentInstance = (T) this.createInstance(componentClass);
    if(componentType instanceof SimpleComponentType) {
      final ComponentStructure structure = ((SimpleComponentType) componentType).structure();
      for (final Map.Entry<Class<?>, Field> holderEntry : structure.getHolders().entrySet()) {
        this.injectMember(holderInjector, holderEntry.getValue(), componentInstance, holder);
      }
      for (final ComponentType dependency : this.componentDependencies.adjacentNodes(componentType)) {
        if (parentType != null && dependency.index() == componentType.index()) continue;
        final C dependencyInstance = holder.getComponent(dependency.index())
          .orElseGet(() -> this.create(holder, componentInjector, holderInjector, componentType, dependency));
        final Field requiredField = structure.getRequiredDependencies().get(dependency.type());
        if (requiredField != null) {
          this.injectMember(componentInjector, requiredField, componentInstance, dependencyInstance);
        }
        final Field optionalField = structure.getOptionalDependencies().get(dependency.type());
        if (optionalField != null) {
          this.injectMember(componentInjector, optionalField, componentInstance, dependencyInstance);
        }
      }
    }
    return componentInstance;
  }

  @SuppressWarnings("unchecked")
  private <T, M> void injectMember(final MemberInjector.@NonNull Factory<?, M> injector, final @NonNull Field field, final @NonNull T target, final @NonNull Object member) {
    try {
      final MemberInjector<T, M> injectorInstance = (MemberInjector<T, M>) injector.create(target, field);
      injectorInstance.member(target, (M) member);
    } catch(final Throwable throwable) {
      throw new IllegalStateException("Unable to inject member.", throwable);
    }
  }

  private Object createInstance(final @NonNull Class<?> componentClass) {
    try {
      return componentClass.newInstance();
    } catch(final IllegalAccessException | InstantiationException exception) {
      throw new IllegalStateException("Unable to instantiate component.", exception);
    }
  }

  public static final class Factory implements ComponentResolver.Factory {
    /* package */ Factory() {}

    @Override
    public <H extends Holder<C>, C> @NonNull ComponentResolver<H, C> create(final @NonNull Universe<H, C> universe) {
      return new SimpleComponentResolver<>(universe);
    }
  }
}
