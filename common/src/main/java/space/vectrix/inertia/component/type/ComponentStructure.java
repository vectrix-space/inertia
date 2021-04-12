/*
 * This file is part of inertia, licensed under the MIT License (MIT).
 *
 * Copyright (c) vectrix.space <https://vectrix.space/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package space.vectrix.inertia.component.type;

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
