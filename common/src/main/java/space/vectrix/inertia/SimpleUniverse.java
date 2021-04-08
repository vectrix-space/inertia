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
import space.vectrix.inertia.component.ComponentRegistry;
import space.vectrix.inertia.component.ComponentResolver;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.component.SimpleComponentRegistry;
import space.vectrix.inertia.component.SimpleComponentResolver;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.holder.HolderRegistry;
import space.vectrix.inertia.holder.HolderResolver;
import space.vectrix.inertia.holder.SimpleHolderRegistry;
import space.vectrix.inertia.holder.SimpleHolderResolver;
import space.vectrix.inertia.injector.DummyMemberInjectorFactory;
import space.vectrix.inertia.injector.MemberInjector;

import java.util.concurrent.CompletableFuture;

public final class SimpleUniverse<H extends Holder<C>, C> implements Universe<H, C> {
  private final HolderResolver<H, C> holderResolver;
  private final ComponentResolver<H, C> componentResolver;
  private final MemberInjector.Factory<?, H> holderInjector;
  private final MemberInjector.Factory<?, C> componentInjector;
  private final HolderRegistry<H, C> holders;
  private final ComponentRegistry components;
  private final String id;

  /* package */ SimpleUniverse(final SimpleUniverse.Builder<H, C> builder) {
    this.id = builder.id;
    this.holderResolver = builder.holderResolver.create(this);
    this.componentResolver = builder.componentResolver.create(this);
    this.componentInjector = builder.componentInjector;
    this.holderInjector = builder.holderInjector;
    this.holders = new SimpleHolderRegistry<>();
    this.components = new SimpleComponentRegistry();
  }

  @Override
  public @NonNull String id() {
    return this.id;
  }

  @Override
  public <T extends H> @NonNull CompletableFuture<T> holder(final HolderResolver.@NonNull HolderFunction<H, C, T> holderFunction) {
    requireNonNull(holderFunction, "holderFunction");
    return CompletableFuture.supplyAsync(() -> this.holderResolver.create(holderFunction));
  }

  @Override
  public <T extends C> @NonNull CompletableFuture<T> component(final @NonNull H holder, final @NonNull Class<T> component) {
    requireNonNull(holder, "holder");
    requireNonNull(component, "component");
    return this.component(component).thenCompose(componentType -> this.component(holder, componentType));
  }

  @Override
  public <T extends C> @NonNull CompletableFuture<T> component(final @NonNull H holder, final @NonNull ComponentType componentType) {
    requireNonNull(holder, "holder");
    requireNonNull(componentType, "componentType");
    return CompletableFuture.supplyAsync(() -> this.componentResolver.create(holder, componentType, this.componentInjector, this.holderInjector));
  }

  @Override
  public <T extends C> @NonNull CompletableFuture<ComponentType> component(final @NonNull Class<T> component) {
    requireNonNull(component, "component");
    return CompletableFuture.supplyAsync(() -> this.componentResolver.resolve(component));
  }

  @Override
  public @NonNull HolderRegistry<H, C> holders() {
    return this.holders;
  }

  @Override
  public @NonNull ComponentRegistry components() {
    return this.components;
  }

  public static final class Builder<H extends Holder<C>, C> implements Universe.Builder<H, C> {
    private HolderResolver.Factory holderResolver = new SimpleHolderResolver.Factory();
    private ComponentResolver.Factory componentResolver = new SimpleComponentResolver.Factory();
    private MemberInjector.Factory<?, H> holderInjector = new DummyMemberInjectorFactory<>();
    private MemberInjector.Factory<?, C> componentInjector = new DummyMemberInjectorFactory<>();
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
    public Universe.@NonNull Builder<H, C> holderInjector(final MemberInjector.@NonNull Factory<?, H> injector) {
      requireNonNull(injector, "injector");
      this.holderInjector = injector;
      return this;
    }

    @Override
    public Universe.@NonNull Builder<H, C> componentInjector(final MemberInjector.@NonNull Factory<?, C> injector) {
      requireNonNull(injector, "injector");
      this.componentInjector = injector;
      return this;
    }

    @Override
    public @NonNull Universe<H, C> build() {
      return new SimpleUniverse<>(this);
    }
  }
}
