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
package space.vectrix.inertia.injection;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.system.Dependency;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class LmbdaInjectionStructureFactory implements InjectionStructure.Factory {
  private final InjectionTarget.Factory factory;
  private final MethodHandles.Lookup lookup;

  public LmbdaInjectionStructureFactory() {
    this(LmbdaInjectionTargetFactory.factory(), MethodHandles.lookup());
  }

  public LmbdaInjectionStructureFactory(final InjectionTarget.@NonNull Factory factory, final MethodHandles.@NonNull Lookup lookup) {
    this.factory = factory;
    this.lookup = lookup;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull InjectionStructure create(final @NonNull Class<?> target) {
    requireNonNull(target, "target");
    final Map<Class<?>, InjectionStructure.Entry> dependencies = new IdentityHashMap<>();
    final List<Class<?>> ancestors = (List<Class<?>>) this.ancestors(target);
    for(final Class<?> ancestor : ancestors) {
      for(final Field field : ancestor.getDeclaredFields()) {
        final Dependency dependencyAnnotation = field.getAnnotation(Dependency.class);
        try {
          if(dependencyAnnotation != null) {
            field.setAccessible(true);
            final MethodHandle handle = this.lookup.unreflectSetter(field);
            dependencies.put(dependencyAnnotation.value(), new LmbdaInjectionStructureEntry(
              dependencyAnnotation,
              this.factory.create(handle)
            ));
          }
        } catch(final Throwable throwable) {
          throw new IllegalStateException("Unable to create method handle for '" + field.getName() + "'!", throwable);
        }
      }
    }
    return new LmbdaInjectionStructure(dependencies);
  }

  @SuppressWarnings("unchecked")
  private <T> @NonNull List<Class<? super T>> ancestors(final @NonNull Class<T> target) {
    final List<Class<? super T>> types = new ArrayList<>();
    types.add(target);
    for(int i = 0; i < types.size(); i++) {
      final Class<?> type = types.get(i);
      final Class<?> parent = type.getSuperclass();
      if(parent != null) types.add((Class<? super T>) parent);
    }
    return types;
  }

  /* package */ static final class LmbdaInjectionStructure implements InjectionStructure {
    private final Map<Class<?>, Entry> injectors;

    /* package */ LmbdaInjectionStructure(final @NonNull Map<Class<?>, Entry> injectors) {
      this.injectors = injectors;
    }

    @Override
    public @NonNull Map<Class<?>, Entry> injectors() {
      return this.injectors;
    }
  }

  /* package */ static final class LmbdaInjectionStructureEntry implements InjectionStructure.Entry {
    private final Dependency annotation;
    private final InjectionTarget target;

    /* package */ LmbdaInjectionStructureEntry(final @NonNull Dependency annotation, final @NonNull InjectionTarget target) {
      this.annotation = annotation;
      this.target = target;
    }

    @Override
    public @NonNull Dependency annotation() {
      return this.annotation;
    }

    @Override
    public @NonNull InjectionTarget target() {
      return this.target;
    }
  }
}
