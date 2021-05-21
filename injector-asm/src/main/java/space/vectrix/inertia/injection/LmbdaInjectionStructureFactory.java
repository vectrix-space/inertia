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
