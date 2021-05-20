package space.vectrix.inertia.injection;

import net.kyori.coffee.reflection.Types;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.annotation.Inject;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class LmbdaInjectionStructureFactory implements InjectionStructure.Factory {
  private final MethodHandles.Lookup lookup;

  public LmbdaInjectionStructureFactory() {
    this(MethodHandles.lookup());
  }

  public LmbdaInjectionStructureFactory(final MethodHandles.@NonNull Lookup lookup) {
    this.lookup = lookup;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull InjectionStructure create(final @NonNull Class<?> target, final InjectionMethod.@NonNull Factory injectionFactory) {
    final Map<Class<?>, InjectionStructure.Entry> dependencies = new IdentityHashMap<>();
    final List<Class<?>> ancestors = (List<Class<?>>) Types.ancestors(target);
    for(final Class<?> ancestor : ancestors) {
      if(ancestor.isInterface()) continue;
      for(final Field field : ancestor.getDeclaredFields()) {
        final Inject injectAnnotation = field.getAnnotation(Inject.class);
        try {
          if(injectAnnotation != null) {
            final Class<?> type = field.getType();
            field.setAccessible(true);

            final MethodHandle handle = this.lookup.unreflectSetter(field);
            dependencies.put(type, new LmbdaInjectionStructureEntry(
              injectAnnotation,
              injectionFactory.create(handle)
            ));
          }
        } catch(final Throwable throwable) {
          throw new IllegalStateException("Unable to create method handle for '" + field.getName() + "'!", throwable);
        }
      }
    }
    return new LmbdaInjectionStructure(dependencies);
  }

  /* package */ static final class LmbdaInjectionStructure implements InjectionStructure {
    private final Map<Class<?>, Entry> injectors;

    /* package */ LmbdaInjectionStructure(final Map<Class<?>, Entry> injectors) {
      this.injectors = injectors;
    }

    @Override
    public @NonNull Map<Class<?>, Entry> injectors() {
      return this.injectors;
    }
  }

  /* package */ static final class LmbdaInjectionStructureEntry implements InjectionStructure.Entry {
    private final Inject annotation;
    private final InjectionMethod method;

    /* package */ LmbdaInjectionStructureEntry(final Inject annotation, final InjectionMethod method) {
      this.annotation = annotation;
      this.method = method;
    }

    @Override
    public @NonNull Inject annotation() {
      return this.annotation;
    }

    @Override
    public @NonNull InjectionMethod method() {
      return this.method;
    }
  }
}
