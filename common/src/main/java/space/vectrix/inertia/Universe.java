package space.vectrix.inertia;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.component.ComponentRegistry;
import space.vectrix.inertia.component.ComponentResolver;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.injector.MemberInjector;

import java.util.concurrent.CompletableFuture;

public final class Universe<C, H> {
  public static <C1, H1> Universe.Builder<C1, H1> builder() {
    return new Universe.Builder<>();
  }

  private final String id;
  private final ComponentResolver<C, H> componentResolver;
  private final MemberInjector.Factory<?, C> componentInjector;
  private final MemberInjector.Factory<?, H> holderInjector;
  private final ComponentRegistry components;

  /* package */ Universe(final Universe.Builder<C, H> builder) {
    this.id = builder.id;
    this.componentResolver = builder.componentResolver.create(this);
    this.componentInjector = builder.componentInjector;
    this.holderInjector = builder.holderInjector;

    this.components = new ComponentRegistry();
  }

  /**
   * Returns the universe identifier.
   *
   * @return The identifier
   */
  public @NonNull String id() {
    return this.id;
  }

  /**
   *
   * @param holder
   * @param component
   * @return
   */
  public CompletableFuture<? extends C> component(final @NonNull H holder, final @NonNull Class<? extends C> component) {
    return this.component(component).thenCompose(componentType -> this.component(holder, componentType));
  }

  /**
   *
   * @param holder
   * @param componentType
   * @return
   */
  public CompletableFuture<? extends C> component(final @NonNull H holder, final @NonNull ComponentType componentType) {
    return CompletableFuture.supplyAsync(() -> this.componentResolver.create(holder, this.componentInjector, componentType));
  }

  /**
   * Returns a {@link CompletableFuture} with the specified component
   * {@link Class} as a {@link ComponentType}.
   *
   * @param component The component class
   * @return A completable future with a component type
   */
  public CompletableFuture<ComponentType> component(final @NonNull Class<? extends C> component) {
    return CompletableFuture.supplyAsync(() -> this.componentResolver.resolve(null, component));
  }

  /**
   * Returns the {@link ComponentRegistry}.
   *
   * @return The component registry
   */
  public @NonNull ComponentRegistry components() {
    return this.components;
  }

  public static class Builder<C, H> {
    private String id;
    private ComponentResolver.Factory componentResolver;
    private MemberInjector.Factory<?, C> componentInjector;
    private MemberInjector.Factory<?, H> holderInjector;

    /* package */ Builder() {}

    public @NonNull Builder<C, H> id(final @NonNull String id) {
      this.id = id;
      return this;
    }

    public @NonNull Builder<C, H> componentResolver(final ComponentResolver.Factory resolver) {
      this.componentResolver = resolver;
      return this;
    }

    public @NonNull Builder<C, H> componentInjector(final MemberInjector.Factory<?, C> injector) {
      this.componentInjector = injector;
      return this;
    }

    public @NonNull Builder<C, H> holderInjector(final MemberInjector.Factory<?, H> injector) {
      this.holderInjector = injector;
      return this;
    }

    public @NonNull Universe<C, H> build() {
      return new Universe<>(this);
    }
  }
}
