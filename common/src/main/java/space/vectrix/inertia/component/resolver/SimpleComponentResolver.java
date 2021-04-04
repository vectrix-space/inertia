package space.vectrix.inertia.component.resolver;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.Component;
import space.vectrix.inertia.ComponentDependency;
import space.vectrix.inertia.HolderDependency;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.component.ComponentResolver;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.injector.MemberInjector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public final class SimpleComponentResolver<C, H> implements ComponentResolver<C, H> {
  public static SimpleComponentResolver.Factory FACTORY = new SimpleComponentResolver.Factory();

  private final AtomicInteger index = new AtomicInteger();
  private final Map<Class<?>, Structure> structures = new HashMap<>();
  private final MutableGraph<ComponentType> componentDependencies = GraphBuilder.undirected()
    .allowsSelfLoops(false)
    .expectedNodeCount(1000)
    .build();

  private final Universe<C, H> universe;

  /* package */ SimpleComponentResolver(final Universe<C, H> universe) {
    this.universe = universe;
  }

  @Override
  public @NonNull ComponentType resolve(final @Nullable ComponentType parent, final @NonNull Class<?> type) {
    final Structure structure = this.structures.computeIfAbsent(type, Structure::generate);
    final ComponentType componentType = this.universe.components().computeIfAbsent(type, key -> {
      final Component component = type.getAnnotation(Component.class);
      if(component == null) throw new IllegalArgumentException("Target type must have a component annotation!");
      return new ComponentType(this.index.getAndIncrement(), type, component.id(), component.name());
    });
    if(parent != null) {
      this.componentDependencies.putEdge(parent, componentType);
    }
    for(final Class<?> dependency : structure.getRequiredDependencies().keySet()) {
      componentType.requiredDependencies().add(this.resolve(componentType, dependency));
    }
    for(final Class<?> dependency : structure.getOptionalDependencies().keySet()) {
      componentType.optionalDependencies().add(this.resolve(componentType, dependency));
    }
    return componentType;
  }

  @Override
  public <T extends C> @NonNull T create(final @NonNull H holder, final MemberInjector.@NonNull Factory<?, C> injector, final @NonNull ComponentType componentType) {
    return this.create(holder, injector, null, componentType);
  }

  @SuppressWarnings("unchecked")
  private <T extends C> T create(final H holder, final MemberInjector.Factory<?, C> injector, final @Nullable ComponentType parent, final ComponentType type) {
    final Class<?> componentClass = type.type();
    final Structure structure = this.structures.get(componentClass);
    final T componentInstance = (T) this.createInstance(componentClass);
    for(final Map.Entry<Class<?>, Field> holderEntry : structure.getHolders().entrySet()) {
      this.injectMember(injector, holderEntry.getValue(), componentInstance, holder);
    }
    for(final ComponentType dependency : this.componentDependencies.adjacentNodes(type)) {
      if(parent != null && dependency.index() == type.index()) continue;
      // TODO: Grab the instance of the depended on component from the holder, or create a new one.
      final Object dependencyInstance = new Object();
      final Field requiredField = structure.getRequiredDependencies().get(dependency.type());
      if (requiredField != null) {
        this.injectMember(injector, requiredField, componentInstance, dependencyInstance);
      }
      final Field optionalField = structure.getOptionalDependencies().get(dependency.type());
      if (optionalField != null) {
        this.injectMember(injector, optionalField, componentInstance, dependencyInstance);
      }
    }
    return componentInstance;
  }

  private <T> void injectMember(final MemberInjector.@NonNull Factory<T, C> injector, final @NonNull Field field, final @NonNull C target, final @NonNull Object member) {
    try {
      final MemberInjector<T, C> injectorInstance = injector.create(target, field);
      injectorInstance.member(target, (T) member);
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

  public static final class Structure {
    public static @NonNull Structure generate(final @NonNull Class<?> component) {
      final Map<Class<?>, Field> requiredComponents = new IdentityHashMap<>();
      final Map<Class<?>, Field> optionalComponents = new IdentityHashMap<>();
      final Map<Class<?>, Field> holders = new IdentityHashMap<>();

      Structure.find(component, Class::getSuperclass, fields -> {
        for(final Field field : fields) {
          final ComponentDependency componentDependency = field.getAnnotation(ComponentDependency.class);
          final HolderDependency holderDependency = field.getAnnotation(HolderDependency.class);
          if(componentDependency != null) {
            if(componentDependency.required()) {
              requiredComponents.putIfAbsent(field.getType(), field);
            } else {
              optionalComponents.putIfAbsent(field.getType(), field);
            }
          } else if(holderDependency != null) {
            holders.putIfAbsent(field.getType(), field);
          }
        }
      });

      return new Structure(requiredComponents, optionalComponents, holders);
    }

    public static void find(final @NonNull Class<?> component,
                            final @NonNull Function<Class<?>, Class<?>> superType,
                            final @NonNull Consumer<Field[]> fieldSearch) {
      Class<?> searchClass = component;
      while(searchClass != null) {
        final Field[] fields = searchClass.getDeclaredFields();
        fieldSearch.accept(fields);
        searchClass = superType.apply(searchClass);
      }
    }

    private final Map<Class<?>, Field> requiredDependencies;
    private final Map<Class<?>, Field> optionalDependencies;
    private final Map<Class<?>, Field> holders;

    /* package */ Structure(final Map<Class<?>, Field> requiredDependencies, final Map<Class<?>, Field> optionalDependencies, final Map<Class<?>, Field> holders) {
      this.requiredDependencies = requiredDependencies;
      this.optionalDependencies = optionalDependencies;
      this.holders = holders;
    }

    public @NonNull Map<Class<?>, Field> getRequiredDependencies() {
      return this.requiredDependencies;
    }

    public @NonNull Map<Class<?>, Field> getOptionalDependencies() {
      return this.optionalDependencies;
    }

    public @NonNull Map<Class<?>, Field> getHolders() {
      return this.holders;
    }
  }

  public static final class Factory implements ComponentResolver.Factory {
    /* package */ Factory() {}

    @Override
    public <C, H> @NonNull ComponentResolver<C, H> create(final @NonNull Universe<C, H> universe) {
      return new SimpleComponentResolver<>(universe);
    }
  }
}
