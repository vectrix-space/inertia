package space.vectrix.inertia;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.component.ComponentRegistry;
import space.vectrix.inertia.component.ComponentResolver;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.injector.MemberInjector;

import java.util.concurrent.CompletableFuture;

public interface Universe<H, C> {
  @NonNull String id();
  @NonNull CompletableFuture<? extends C> component(final @NonNull H holder, final @NonNull Class<? extends C> component);
  @NonNull CompletableFuture<? extends C> component(final @NonNull H holder, final @NonNull ComponentType componentType);
  @NonNull CompletableFuture<ComponentType> component(final @NonNull Class<? extends C> component);
  @NonNull ComponentRegistry components();


  interface Builder<H, C> {
    @NonNull Builder<C, H> id(final @NonNull String id);
    @NonNull Builder<C, H> componentResolver(final ComponentResolver.@NonNull Factory resolver);
    @NonNull Builder<C, H> componentInjector(final MemberInjector.Factory<?, C> injector);
  }
}
