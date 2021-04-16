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
package space.vectrix.inertia.injector;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.ComponentDependency;
import space.vectrix.inertia.HolderDependency;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class LmbdaInjectionStructureFactory implements InjectionStructure.Factory<MethodHandle> {
  private final MethodHandles.Lookup lookup;

  public LmbdaInjectionStructureFactory() {
    this(MethodHandles.lookup());
  }

  public LmbdaInjectionStructureFactory(final MethodHandles.Lookup lookup) {
    this.lookup = lookup;
  }

  @Override
  public @NonNull InjectionStructure<MethodHandle> create(final @NonNull Class<?> target) {
    final Map<Class<?>, MethodHandle> required = new IdentityHashMap<>();
    final Map<Class<?>, MethodHandle> optional = new IdentityHashMap<>();
    final Map<Class<?>, MethodHandle> holders = new IdentityHashMap<>();

    this.find(target, Class::getSuperclass, fields -> {
      for(final Field field : fields) {
        final ComponentDependency componentDependency = field.getAnnotation(ComponentDependency.class);
        final HolderDependency holderDependency = field.getAnnotation(HolderDependency.class);
        try {
          final MethodHandle handle = this.lookup.unreflectSetter(field);
          if (componentDependency != null) {
            if (componentDependency.required()) {
              required.putIfAbsent(field.getType(), handle);
            } else {
              optional.putIfAbsent(field.getType(), handle);
            }
          } else if (holderDependency != null) {
            holders.putIfAbsent(field.getType(), handle);
          }
        } catch(final Throwable throwable) {
          throw new IllegalStateException("Unable to create method handle for '" + field.getName() + "'!", throwable);
        }
      }
    });

    return new LmbdaInjectionStructure(required, optional, holders);
  }

  private void find(final Class<?> component,
                    final Function<Class<?>, Class<?>> superType,
                    final Consumer<Field[]> fieldSearch) {
    Class<?> searchClass = component;
    while(searchClass != null) {
      final Field[] fields = searchClass.getDeclaredFields();
      fieldSearch.accept(fields);
      searchClass = superType.apply(searchClass);
    }
  }

  /* package */ final class LmbdaInjectionStructure implements InjectionStructure<MethodHandle> {
    private final Map<Class<?>, MethodHandle> required;
    private final Map<Class<?>, MethodHandle> optional;
    private final Map<Class<?>, MethodHandle> holders;

    /* package */ LmbdaInjectionStructure(final Map<Class<?>, MethodHandle> required,
                                          final Map<Class<?>, MethodHandle> optional,
                                          final Map<Class<?>, MethodHandle> holders) {
      this.required = required;
      this.optional = optional;
      this.holders = holders;
    }

    @Override
    public @NonNull Map<Class<?>, MethodHandle> getRequired() {
      return this.required;
    }

    @Override
    public @NonNull Map<Class<?>, MethodHandle> getOptional() {
      return this.optional;
    }

    @Override
    public @NonNull Map<Class<?>, MethodHandle> getHolders() {
      return this.holders;
    }
  }
}
