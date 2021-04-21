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

import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.component.AbstractComponents;
import space.vectrix.inertia.component.ComponentResolver;
import space.vectrix.inertia.component.ComponentResolverImpl;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.component.ComponentTypes;
import space.vectrix.inertia.component.ComponentTypesImpl;
import space.vectrix.inertia.component.Components;
import space.vectrix.inertia.component.ComponentsImpl;
import space.vectrix.inertia.holder.AbstractHolders;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.holder.HolderResolver;
import space.vectrix.inertia.holder.HolderResolverImpl;
import space.vectrix.inertia.holder.Holders;
import space.vectrix.inertia.holder.HoldersImpl;
import space.vectrix.inertia.injector.DummyInjectionMethodFactory;
import space.vectrix.inertia.injector.DummyInjectionStructureFactory;
import space.vectrix.inertia.injector.InjectionMethod;
import space.vectrix.inertia.injector.InjectionStructure;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class UniverseImpl<H extends Holder<C>, C> implements Universe<H, C> {
  private final HolderResolver<H, C> holderResolver;
  private final ComponentResolver<H, C> componentResolver;
  private final InjectionMethod.Factory<?, H> holderInjector;
  private final InjectionStructure.Factory<H, C> holderStructure;
  private final InjectionMethod.Factory<?, C> componentInjector;
  private final InjectionStructure.Factory<H, C> componentStructure;
  private final Holders<H, C> holders;
  private final Components<H, C> components;
  private final ComponentTypes componentTypes;
  private final String id;

  /* package */ UniverseImpl(final UniverseImpl.Builder<H, C> builder) {
    this.id = builder.id;
    this.holderResolver = builder.holderResolver.create(this);
    this.componentResolver = builder.componentResolver.create(this);
    this.holderInjector = builder.holderInjector;
    this.holderStructure = builder.holderStructure;
    this.componentInjector = builder.componentInjector;
    this.componentStructure = builder.componentStructure;
    this.holders = builder.holderRegistry;
    this.components = builder.componentRegistry;
    this.componentTypes = new ComponentTypesImpl<H, C>();
  }

  @Override
  public @NonNull String id() {
    return this.id;
  }

  @Override
  public int createHolder() {
    return this.holderResolver.create();
  }

  @Override
  public @NonNull <T extends H> CompletableFuture<T> createHolder(final HolderResolver.@NonNull HolderFunction<H, C, T> holderFunction) {
    requireNonNull(holderFunction, "holderFunction");
    return CompletableFuture.supplyAsync(() -> this.holderResolver.create(holderFunction));
  }

  @Override
  public @NonNull CompletableFuture<ComponentType> resolveComponent(final @NonNull Class<? extends C> component) {
    return CompletableFuture.supplyAsync(() -> this.componentResolver.resolve(component, this.componentStructure, this.componentInjector, this.holderInjector));
  }

  @Override
  public @NonNull <T extends C> CompletableFuture<T> createComponent(final int holder, final @NonNull ComponentType componentType) {
    requireNonNull(componentType, "componentType");
    return CompletableFuture.supplyAsync(() -> this.componentResolver.create(holder, componentType));
  }

  @Override
  public @NonNull <T extends C> CompletableFuture<T> createComponent(final @NonNull H holder, final @NonNull ComponentType componentType) {
    requireNonNull(holder, "holder");
    requireNonNull(componentType, "componentType");
    return CompletableFuture.supplyAsync(() -> this.componentResolver.create(holder, componentType));
  }

  @Override
  public boolean removeHolder(final int index) {
    return ((AbstractHolders<H, C>) this.holders).remove(index);
  }

  @Override
  public boolean removeHolder(final @NonNull H holder) {
    requireNonNull(holder, "holder");
    return ((AbstractHolders<H, C>) this.holders).remove(holder.index());
  }

  @Override
  public boolean removeComponent(final int holder, final @NonNull ComponentType componentType) {
    requireNonNull(componentType, "componentType");
    return ((AbstractComponents<H, C>) this.components).remove(holder, componentType);
  }

  @Override
  public boolean removeComponent(final @NonNull H holder, final @NonNull ComponentType componentType) {
    requireNonNull(holder, "holder");
    requireNonNull(componentType, "componentType");
    return ((AbstractComponents<H, C>) this.components).remove(holder.index(), componentType);
  }

  @Override
  public void removeComponents(final int holder) {
    ((AbstractComponents<H, C>) this.components).remove(holder);
  }

  @Override
  public void removeComponents(final @NonNull H holder) {
    requireNonNull(holder, "holder");
    ((AbstractComponents<H, C>) this.components).remove(holder.index());
  }

  @Override
  public @NonNull <T extends H> Optional<T> getHolder(final int index) {
    return this.holders.get(index);
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final int holder, final @NonNull ComponentType componentType) {
    requireNonNull(componentType, "componentType");
    return this.components.get(holder, componentType);
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final @NonNull H holder, final @NonNull ComponentType componentType) {
    requireNonNull(holder, "holder");
    requireNonNull(componentType, "componentType");
    return this.components.get(holder, componentType);
  }

  @Override
  public @NonNull Holders<H, C> holders() {
    return this.holders;
  }

  @Override
  public @NonNull Components<H, C> components() {
    return this.components;
  }

  @Override
  public @NonNull ComponentTypes componentTypes() {
    return this.componentTypes;
  }

  public static final class Builder<H extends Holder<C>, C> implements Universe.Builder<H, C> {
    private HolderResolver.Factory holderResolver = new HolderResolverImpl.Factory();
    private ComponentResolver.Factory componentResolver = new ComponentResolverImpl.Factory();
    private Holders<H, C> holderRegistry = new HoldersImpl<>();
    private Components<H, C> componentRegistry = new ComponentsImpl<>();
    private InjectionMethod.Factory<?, H> holderInjector = new DummyInjectionMethodFactory<>();
    private InjectionStructure.Factory<H, C> holderStructure = new DummyInjectionStructureFactory<>();
    private InjectionMethod.Factory<?, C> componentInjector = new DummyInjectionMethodFactory<>();
    private InjectionStructure.Factory<H, C> componentStructure = new DummyInjectionStructureFactory<>();
    private String id;

    /* package */ Builder() {}

    @Override
    public Universe.@NonNull Builder<H, C> id(final @NonNull String id) {
      requireNonNull(id, "id");
      this.id = id;
      return this;
    }

    @Override
    public Universe.@NonNull Builder<H, C> holderResolver(final HolderResolver.@NonNull Factory resolver) {
      requireNonNull(resolver, "resolver");
      this.holderResolver = resolver;
      return this;
    }

    @Override
    public Universe.@NonNull Builder<H, C> componentResolver(final ComponentResolver.@NonNull Factory resolver) {
      requireNonNull(resolver, "resolver");
      this.componentResolver = resolver;
      return this;
    }

    @Override
    public Universe.@NonNull Builder<H, C> holderRegistry(final @NonNull Holders<H, C> registry) {
      requireNonNull(registry, "registry");
      this.holderRegistry = registry;
      return this;
    }

    @Override
    public Universe.@NonNull Builder<H, C> componentRegistry(final @NonNull Components<H, C> registry) {
      requireNonNull(registry, "registry");
      this.componentRegistry = registry;
      return this;
    }

    @Override
    public Universe.@NonNull Builder<H, C> holderInjector(final InjectionMethod.@NonNull Factory<?, H> injector,
                                                          final InjectionStructure.@NonNull Factory<H, C> structure) {
      requireNonNull(injector, "injector");
      requireNonNull(structure, "structure");
      this.holderInjector = injector;
      this.holderStructure = structure;
      return this;
    }

    @Override
    public Universe.@NonNull Builder<H, C> componentInjector(final InjectionMethod.@NonNull Factory<?, C> injector,
                                                             final InjectionStructure.@NonNull Factory<H, C> structure) {
      requireNonNull(injector, "injector");
      requireNonNull(structure, "structure");
      this.componentInjector = injector;
      this.componentStructure = structure;
      return this;
    }

    @Override
    public @NonNull Universe<H, C> build() {
      return new UniverseImpl<>(this);
    }
  }
}
