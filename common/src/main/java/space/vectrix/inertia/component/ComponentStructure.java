package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.ComponentDependency;
import space.vectrix.inertia.HolderDependency;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ComponentStructure {
  public static @NonNull ComponentStructure generate(final @NonNull Class<?> component) {
    final Map<Class<?>, Field> requiredComponents = new IdentityHashMap<>();
    final Map<Class<?>, Field> optionalComponents = new IdentityHashMap<>();
    final Map<Class<?>, Field> holders = new IdentityHashMap<>();

    ComponentStructure.find(component, Class::getSuperclass, fields -> {
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

    return new ComponentStructure(requiredComponents, optionalComponents, holders);
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

  /* package */ ComponentStructure(final Map<Class<?>, Field> requiredDependencies, final Map<Class<?>, Field> optionalDependencies, final Map<Class<?>, Field> holders) {
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
