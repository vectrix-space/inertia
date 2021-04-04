package space.vectrix.inertia;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.component.ComponentRegistry;
import space.vectrix.inertia.component.ComponentResolver;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.component.SimpleComponentRegistry;
import space.vectrix.inertia.injector.MemberInjector;

import java.util.concurrent.CompletableFuture;

public final class SimpleUniverse<H, C> implements Universe<H, C> {
  private final String id;
  private final ComponentResolver<H, C> componentResolver;
  private final MemberInjector.Factory<?, C> componentInjector;
  private final MemberInjector.Factory<?, H> holderInjector;
  private final ComponentRegistry components;

  /* package */ SimpleUniverse(final SimpleUniverse.Builder<H, C> builder) {
    this.id = builder.id;
    this.componentResolver = builder.componentResolver.create(this);
    this.componentInjector = builder.componentInjector;
    this.holderInjector = builder.holderInjector;

    this.components = new SimpleComponentRegistry();
  }

  @Override
  public @NonNull String id() {
    return this.id;
  }

  @Override
  public @NonNull CompletableFuture<? extends C> component(final @NonNull H holder, final @NonNull Class<? extends C> component) {
    return this.component(component).thenCompose(componentType -> this.component(holder, componentType));
  }

  @Override
  public @NonNull CompletableFuture<? extends C> component(final @NonNull H holder, final @NonNull ComponentType componentType) {
    return CompletableFuture.supplyAsync(() -> this.componentResolver.create(holder, componentType, this.componentInjector, this.holderInjector));
  }

  @Override
  public @NonNull CompletableFuture<ComponentType> component(final @NonNull Class<? extends C> component) {
    return CompletableFuture.supplyAsync(() -> this.componentResolver.resolve(component));
  }

  @Override
  public @NonNull ComponentRegistry components() {
    return this.components;
  }

  public static final class Builder<H, C> implements Universe.Builder<H, C> {
    private String id;
    private ComponentResolver.Factory componentResolver;
    private MemberInjector.Factory<?, C> componentInjector;
    private MemberInjector.Factory<?, H> holderInjector;

    /* package */ Builder() {}

    @Override
    public Universe.@NonNull Builder<H, C> id(final @NonNull String id) {
      this.id = id;
      return this;
    }

    @Override
    public Universe.@NonNull Builder<H, C> componentResolver(final ComponentResolver.@NonNull Factory resolver) {
      this.componentResolver = resolver;
      return this;
    }

    @Override
    public Universe.@NonNull Builder<H, C> componentInjector(final MemberInjector.@NonNull Factory<?, C> injector) {
      this.componentInjector = injector;
      return this;
    }

    @Override
    public Universe.@NonNull Builder<H, C> holderInjector(final MemberInjector.@NonNull Factory<?, H> injector) {
      this.holderInjector = injector;
      return this;
    }

    @Override
    public @NonNull Universe<H, C> build() {
      return new SimpleUniverse<>(this);
    }
  }
}
