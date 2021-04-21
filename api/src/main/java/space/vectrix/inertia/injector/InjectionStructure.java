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
import space.vectrix.inertia.holder.Holder;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Represents the dependency structure of the specified target.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public interface InjectionStructure<H extends Holder<C>, C> {
  /**
   * Returns a {@link Map} of {@link Class} types to {@link Entry}s
   * for the component dependencies.
   *
   * @return A map of component dependencies
   * @since 0.1.0
   */
  @NonNull Map<Class<?>, Entry<ComponentDependency, ?, C>> components();

  /**
   * Returns a {@link Map} of {@link Class} types to {@link Entry}s
   * for the holder dependencies.
   *
   * @return A map of holder dependencies
   * @since 0.1.0
   */
  @NonNull Map<Class<?>, Entry<HolderDependency, ?, H>> holders();

  /**
   * A dependency structure entry.
   *
   * @param <A> The dependency annotation type
   * @param <T> The target type
   * @param <M> The member type
   * @since 0.1.0
   */
  interface Entry<A extends Annotation, T, M> {
    /**
     * Returns the {@code A} dependency annotation.
     *
     * @return The dependency annotation
     * @since 0.1.0
     */
    @NonNull A annotation();

    /**
     * Returns the {@link InjectionMethod} for the dependency.
     *
     * @return The injection method
     * @since 0.1.0
     */
    @NonNull InjectionMethod<T, M> method();
  }

  /**
   * The factory for creating an {@link InjectionStructure}.
   *
   * @param <H> The holder type
   * @param <C> The component type
   * @since 0.1.0
   */
  @FunctionalInterface
  interface Factory<H extends Holder<C>, C> {
    /**
     * Creates a new {@link InjectionStructure} for the specified {@link Class}
     * target and specified {@link InjectionMethod.Factory}s.
     *
     * @param target The structure target
     * @param componentInjectionFactory The component injection factory
     * @param holderInjectionFactory The holder injection factory
     * @return The new injection structure
     * @since 0.1.0
     */
    @NonNull InjectionStructure<H, C> create(final @NonNull Class<?> target,
                                             final InjectionMethod.@NonNull Factory<?, C> componentInjectionFactory,
                                             final InjectionMethod.@NonNull Factory<?, H> holderInjectionFactory);
  }
}
