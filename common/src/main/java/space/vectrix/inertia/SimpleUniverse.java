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
import space.vectrix.inertia.component.ComponentResolver;
import space.vectrix.inertia.component.Components;
import space.vectrix.inertia.component.SimpleComponentResolver;
import space.vectrix.inertia.component.SimpleComponents;
import space.vectrix.inertia.component.type.ComponentType;
import space.vectrix.inertia.component.type.ComponentTypes;
import space.vectrix.inertia.component.type.SimpleComponentTypes;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.holder.HolderResolver;
import space.vectrix.inertia.holder.Holders;
import space.vectrix.inertia.holder.SimpleHolderResolver;
import space.vectrix.inertia.holder.SimpleHolders;
import space.vectrix.inertia.injector.DummyMemberInjectorFactory;
import space.vectrix.inertia.injector.MemberInjector;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class SimpleUniverse<H extends Holder<C>, C> implements Universe<H, C> {
  private final HolderResolver<H, C> holderResolver;
  private final ComponentResolver<H, C> componentResolver;
  private final MemberInjector.Factory<?, H> holderInjector;
  private final MemberInjector.Factory<?, C> componentInjector;
  private final Holders<H, C> holders;
  private final Components<H, C> components;
  private final ComponentTypes componentTypes;
  private final String id;

  /* package */ SimpleUniverse(final SimpleUniverse.Builder<H, C> builder) {
    this.id = builder.id;
    this.holderResolver = builder.holderResolver.create(this);
    this.componentResolver = builder.componentResolver.create(this);
    this.componentInjector = builder.componentInjector;
    this.holderInjector = builder.holderInjector;
    this.holders = builder.holderRegistry;
    this.components = builder.componentRegistry;
    this.componentTypes = new SimpleComponentTypes();
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
  public @NonNull <T extends C> CompletableFuture<T> createComponent(final int holder, final @NonNull ComponentType componentType) {
    return null;
  }

  @Override
  public @NonNull <T extends C> CompletableFuture<T> createComponent(final @NonNull H holder, final @NonNull ComponentType componentType) {
    return null;
  }

  @Override
  public void removeHolder(final int index) {

  }

  @Override
  public void removeHolder(final @NonNull H holder) {

  }

  @Override
  public boolean removeComponent(final int holder, final @NonNull ComponentType componentType) {
    return false;
  }

  @Override
  public boolean removeComponent(final @NonNull H holder, final @NonNull ComponentType componentType) {
    return false;
  }

  @Override
  public void removeComponents(final int holder) {

  }

  @Override
  public void removeComponents(final @NonNull H holder) {

  }

  @Override
  public @NonNull <T extends H> Optional<T> getHolder(final int index) {
    return Optional.empty();
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final int holder, final @NonNull ComponentType componentType) {
    return Optional.empty();
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final @NonNull H holder, final @NonNull ComponentType componentType) {
    return Optional.empty();
  }

  @Override
  public @NonNull Holders<H, C> holders() {
    return this.holders;
  }

  @Override
  public @NonNull Components<H, C> components() {
    return null;
  }

  @Override
  public @NonNull ComponentTypes componentTypes() {
    return this.componentTypes;
  }

  public static final class Builder<H extends Holder<C>, C> implements Universe.Builder<H, C> {
    private HolderResolver.Factory holderResolver = new SimpleHolderResolver.Factory();
    private ComponentResolver.Factory componentResolver = new SimpleComponentResolver.Factory();
    private Holders<H, C> holderRegistry = new SimpleHolders<>();
    private Components<H, C> componentRegistry = new SimpleComponents<>();
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
