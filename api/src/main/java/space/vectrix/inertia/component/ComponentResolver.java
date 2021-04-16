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
package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.component.type.ComponentType;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.injector.InjectionMethod;
import space.vectrix.inertia.injector.InjectionStructure;

/**
 * The component resolver.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public interface ComponentResolver<H extends Holder<C>, C> {
  /**
   * Resolves the {@link ComponentType} from the specified {@link Class}
   * type.
   *
   * @param type The component class
   * @param componentStructure The component structure
   * @return The component type
   * @since 0.1.0
   */
  @NonNull ComponentType resolve(final @NonNull Class<?> type, final InjectionStructure.@NonNull Factory<?> componentStructure);

  /**
   * Creates the {@code T} component for the specified {@code int} holder
   * using the {@link ComponentType} and {@link InjectionMethod}s.
   *
   * @param holder The holder index
   * @param componentType The component type
   * @param componentInjector The component injector
   * @param componentStructure The component structure
   * @param holderInjector The holder injector
   * @param holderStructure The holder structure
   * @param <T> The specific component type
   * @return The component
   * @since 0.1.0
   */
  <T extends C> @NonNull T create(final int holder,
                                  final @NonNull ComponentType componentType,
                                  final InjectionMethod.@NonNull Factory<?, C, ?> componentInjector,
                                  final InjectionStructure.@NonNull Factory<?> componentStructure,
                                  final InjectionMethod.@NonNull Factory<?, H, ?> holderInjector,
                                  final InjectionStructure.@NonNull Factory<?> holderStructure);

  /**
   * Creates the {@code T} component for the specified {@code H} holder
   * using the {@link ComponentType} and {@link InjectionMethod}s.
   *
   * @param holder The holder
   * @param componentType The component type
   * @param componentInjector The component injector
   * @param componentStructure The component structure
   * @param holderInjector The holder injector
   * @param holderStructure The holder structure
   * @param <T> The specific component type
   * @return The component
   * @since 0.1.0
   */
  <T extends C> @NonNull T create(final @NonNull H holder,
                                  final @NonNull ComponentType componentType,
                                  final InjectionMethod.@NonNull Factory<?, C, ?> componentInjector,
                                  final InjectionStructure.@NonNull Factory<?> componentStructure,
                                  final InjectionMethod.@NonNull Factory<?, H, ?> holderInjector,
                                  final InjectionStructure.@NonNull Factory<?> holderStructure);

  /**
   * The factory for creating an {@link ComponentResolver}.
   *
   * @since 0.1.0
   */
  @FunctionalInterface
  interface Factory {
    /**
     * Creates a new {@link ComponentResolver} for the specified {@link Universe}.
     *
     * @param universe The universe
     * @param <H> The holder type
     * @param <C> The component type
     * @return The component resolver
     * @since 0.1.0
     */
    <H extends Holder<C>, C> @NonNull ComponentResolver<H, C> create(final @NonNull Universe<H, C> universe);
  }
}
