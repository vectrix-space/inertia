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

import net.kyori.coffee.reflection.Types;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.ComponentDependency;
import space.vectrix.inertia.HolderDependency;
import space.vectrix.inertia.holder.Holder;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class LmbdaInjectionStructureFactory<H extends Holder<C>, C> implements InjectionStructure.Factory<H, C> {
  private final MethodHandles.Lookup lookup;

  public LmbdaInjectionStructureFactory() {
    this(MethodHandles.lookup());
  }

  public LmbdaInjectionStructureFactory(final MethodHandles.Lookup lookup) {
    this.lookup = lookup;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @NonNull InjectionStructure<H, C> create(final @NonNull Class<T> target,
                                                      final InjectionMethod.@NonNull Factory<?, C> componentInjectionFactory,
                                                      final InjectionMethod.@NonNull Factory<?, H> holderInjectionFactory) {
    final Map<Class<? extends C>, InjectionStructure.Entry<ComponentDependency, ?, C>> components = new IdentityHashMap<>();
    final Map<Class<? extends H>, InjectionStructure.Entry<HolderDependency, ?, H>> holders = new IdentityHashMap<>();
    final List<Class<? super T>> ancestors = Types.ancestors(target);
    for(final Class<? super T> ancestor : ancestors) {
      if(ancestor.isInterface()) continue;
      for(final Field field : ancestor.getDeclaredFields()) {
        final ComponentDependency componentDependency = field.getAnnotation(ComponentDependency.class);
        final HolderDependency holderDependency = field.getAnnotation(HolderDependency.class);
        try {
          if(componentDependency != null || holderDependency != null) {
            final Class<?> type = field.getType();
            field.setAccessible(true);
            final MethodHandle handle = this.lookup.unreflectSetter(field);
            if (componentDependency != null) {
              components.put((Class<? extends C>) type, new LmbdaInjectionStructureEntry<>(
                componentDependency,
                componentInjectionFactory.create(handle)
              ));
            } else {
              holders.put((Class<? extends H>) type, new LmbdaInjectionStructureEntry<>(
                holderDependency,
                holderInjectionFactory.create(handle)
              ));
            }
          }
        } catch(final Throwable throwable) {
          throw new IllegalStateException("Unable to create method handle for '" + field.getName() + "'!", throwable);
        }
      }
    }
    return new LmbdaInjectionStructure<>(components, holders);
  }

  /* package */ static final class LmbdaInjectionStructure<H extends Holder<C>, C> implements InjectionStructure<H, C> {
    private final Map<Class<? extends C>, Entry<ComponentDependency, ?, C>> components;
    private final Map<Class<? extends H>, Entry<HolderDependency, ?, H>> holders;

    /* package */ LmbdaInjectionStructure(final Map<Class<? extends C>, Entry<ComponentDependency, ?, C>> components,
                                          final Map<Class<? extends H>, Entry<HolderDependency, ?, H>> holders) {
      this.components = components;
      this.holders = holders;
    }

    @Override
    public @NonNull Map<Class<? extends C>, Entry<ComponentDependency, ?, C>> components() {
      return this.components;
    }

    @Override
    public @NonNull Map<Class<? extends H>, Entry<HolderDependency, ?, H>> holders() {
      return this.holders;
    }
  }

  /* package */ static final class LmbdaInjectionStructureEntry<A extends Annotation, T, M> implements InjectionStructure.Entry<A, T, M> {
    private final A annotation;
    private final InjectionMethod<T, M> method;

    /* package */ LmbdaInjectionStructureEntry(final A annotation, final InjectionMethod<T, M> method) {
      this.annotation = annotation;
      this.method = method;
    }

    @Override
    public @NonNull A annotation() {
      return this.annotation;
    }

    @Override
    public @NonNull InjectionMethod<T, M> method() {
      return this.method;
    }
  }
}
