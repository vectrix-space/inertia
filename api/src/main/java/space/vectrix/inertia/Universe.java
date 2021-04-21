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
package space.vectrix.inertia;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.component.ComponentResolver;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.component.ComponentTypes;
import space.vectrix.inertia.component.Components;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.holder.HolderResolver;
import space.vectrix.inertia.holder.Holders;
import space.vectrix.inertia.injector.InjectionMethod;
import space.vectrix.inertia.injector.InjectionStructure;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The universe of {@code H} holders and {@code C} components.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public interface Universe<H extends Holder<C>, C> {
  /**
   * Returns the universe identifier.
   *
   * @return The identifier
   * @since 0.1.0
   */
  @NonNull String id();

  /**
   * Creates a new holder and returns an {@code int} index.
   *
   * @return The new holder index
   * @since 0.1.0
   */
  int createHolder();

  /**
   * Creates a new {@code T} holder using the specified {@link HolderResolver.HolderFunction}
   * with a new {@code int} index.
   *
   * @param holderFunction The holder function
   * @param <T> The specific holder type
   * @return A completable future containing the holder
   * @since 0.1.0
   */
  <T extends H> @NonNull CompletableFuture<T> createHolder(final HolderResolver.@NonNull HolderFunction<H, C, T> holderFunction);

  /**
   * Resolves a new {@link ComponentType} for the specified component
   * {@link Class}.
   *
   * @param component The component class
   * @return A completable future containing the component type
   * @since 0.1.0
   */
  @NonNull CompletableFuture<ComponentType> resolveComponent(final @NonNull Class<? extends C> component);

  /**
   * Creates a new {@code T} component for the specified {@code int} holder
   * and {@link ComponentType}.
   *
   * @param holder The holder index
   * @param componentType The component type
   * @param <T> The specific component type
   * @return A completable future containing the component
   * @since 0.1.0
   */
  <T extends C> @NonNull CompletableFuture<T> createComponent(final int holder, final @NonNull ComponentType componentType);

  /**
   * Creates a new {@code T} component for the specified {@code H} holder
   * and {@link ComponentType}.
   *
   * @param holder The holder instance
   * @param componentType The component type
   * @param <T> The specific component type
   * @return A completable future containing the component
   * @since 0.1.0
   */
  <T extends C> @NonNull CompletableFuture<T> createComponent(final @NonNull H holder, final @NonNull ComponentType componentType);

  /**
   * Removes the holder with the specified {@code int} index.
   *
   * @param index The holder index
   * @since 0.1.0
   */
  boolean removeHolder(final int index);

  /**
   * Removes the specified {@code T} holder.
   *
   * @param holder The holder
   * @since 0.1.0
   */
  boolean removeHolder(final @NonNull H holder);

  /**
   * Removes the component for the specified {@code int} holder
   * with the specified {@link ComponentType}.
   *
   * @param holder The holder index
   * @param componentType The component type
   * @return True if the component was removed, otherwise false
   * @since 0.1.0
   */
  boolean removeComponent(final int holder, final @NonNull ComponentType componentType);

  /**
   * Removes the component for the specified {@code H} holder with the
   * specified {@link ComponentType}.
   *
   * @param holder The holder instance
   * @param componentType The component type
   * @return True if the component was removed, otherwise false
   * @since 0.1.0
   */
  boolean removeComponent(final @NonNull H holder, final @NonNull ComponentType componentType);

  /**
   * Removes all components for the specified {@code int} holder.
   *
   * @param holder The holder index
   * @since 0.1.0
   */
  void removeComponents(final int holder);

  /**
   * Removes all components for the specified {@code H} holder.
   *
   * @param holder The holder instance
   * @since 0.1.0
   */
  void removeComponents(final @NonNull H holder);

  /**
   * Returns the {@code T} holder with the specified {@code int}
   * index, if it exists.
   *
   * @param index The holder index
   * @param <T> The specific holder type
   * @return The holder instance, if present
   * @since 0.1.0
   */
  <T extends H> @NonNull Optional<T> getHolder(final int index);

  /**
   * Returns the {@code T} component for the specified {@code int} holder
   * with the specified {@link ComponentType}.
   *
   * @param holder The holder index
   * @param componentType The component type
   * @param <T> The specific component type
   * @return The component, if present
   */
  <T extends C> @NonNull Optional<T> getComponent(final int holder, final @NonNull ComponentType componentType);

  /**
   * Returns the {@code T} component for the specified {@code H} holder
   * with the specified {@link ComponentType}.
   *
   * @param holder The holder index
   * @param componentType The component type
   * @param <T> The specific component type
   * @return The component, if present
   */
  <T extends C> @NonNull Optional<T> getComponent(final @NonNull H holder, final @NonNull ComponentType componentType);

  /**
   * Returns the {@link Holders}.
   *
   * @return The holders registry
   * @since 0.1.0
   */
  @NonNull Holders<H, C> holders();

  /**
   * Returns the {@link Components}.
   *
   * @return The components registry
   * @since 0.1.0
   */
  @NonNull Components<H, C> components();

  /**
   * Returns the {@link ComponentTypes}.
   *
   * @return The component types registry
   * @since 0.1.0
   */
  @NonNull ComponentTypes componentTypes();

  interface Builder<H extends Holder<C>, C> {
    /**
     * Returns this {@link Builder} with the specified {@link String}
     * universe identifier.
     *
     * @param id The universe identifier
     * @return This builder
     * @since 0.1.0
     */
    @NonNull Builder<H, C> id(final @NonNull String id);

    /**
     * Returns this {@link Builder} with the specified {@link HolderResolver.Factory}
     * holder resolver.
     *
     * @param resolver The holder resolver
     * @return This builder
     * @since 0.1.0
     */
    @NonNull Builder<H, C> holderResolver(final HolderResolver.@NonNull Factory resolver);

    /**
     * Returns this {@link Builder} with the specified {@link ComponentResolver.Factory}
     * component resolver.
     *
     * @param resolver The component resolver
     * @return This builder
     * @since 0.1.0
     */
    @NonNull Builder<H, C> componentResolver(final ComponentResolver.@NonNull Factory resolver);

    /**
     * Returns this {@link Builder} with the specified {@link Holders} registry.
     *
     * @param registry The holder registry
     * @return This builder
     * @since 0.1.0
     */
    @NonNull Builder<H, C> holderRegistry(final @NonNull Holders<H, C> registry);

    /**
     * Returns this {@link Builder} with the specified {@link Components} registry.
     *
     * @param registry The component registry
     * @return The builder
     * @since 0.1.0
     */
    @NonNull Builder<H, C> componentRegistry(final @NonNull Components<H, C> registry);

    /**
     * Returns this {@link Builder} with the specified {@link InjectionMethod.Factory}
     * holder injector.
     *
     * @param injector The holder injector
     * @param structure The holder structure
     * @return This builder
     * @since 0.1.0
     */
    @NonNull Builder<H, C> holderInjector(final InjectionMethod.@NonNull Factory<?, H> injector,
                                          final InjectionStructure.@NonNull Factory<H, C> structure);

    /**
     * Returns this {@link Builder} with the specified {@link InjectionMethod.Factory}
     * component injector.
     *
     * @param injector The component injector
     * @param structure The component structure
     * @return This builder
     * @since 0.1.0
     */
    @NonNull Builder<H, C> componentInjector(final InjectionMethod.@NonNull Factory<?, C> injector,
                                             final InjectionStructure.@NonNull Factory<H, C> structure);

    /**
     * Returns a new {@link Universe} from this {@link Builder}.
     *
     * @return A new universe
     * @since 0.1.0
     */
    @NonNull Universe<H, C> build();
  }
}
