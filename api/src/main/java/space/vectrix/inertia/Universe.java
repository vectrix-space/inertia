package space.vectrix.inertia;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.component.ComponentRegistry;
import space.vectrix.inertia.component.ComponentResolver;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.holder.HolderResolver;
import space.vectrix.inertia.injector.MemberInjector;

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
   * Creates a new {@code T} holder using the specified {@link HolderResolver.HolderFunction}.
   *
   * @param holderFunction The holder function
   * @param <T> The specific holder type
   * @return A completable future containing the holder
   * @since 0.1.0
   */
  <T extends H> @NonNull CompletableFuture<T> holder(final HolderResolver.@NonNull HolderFunction<H, C, T> holderFunction);

  /**
   * Creates a new {@code C} component from the specified {@link Class} for
   * the {@code H} holder.
   *
   * @param holder The holder
   * @param component The component class
   * @return A completable future containing the component
   * @since 0.1.0
   */
  <T extends C> @NonNull CompletableFuture<T> component(final @NonNull H holder, final @NonNull Class<T> component);

  /**
   * Returns a new {@link CompletableFuture} with the specified component
   * {@link ComponentType}.
   *
   * @param holder The holder
   * @param componentType The component type
   * @return A completable future containing the component
   * @since 0.1.0
   */
  <T extends C> @NonNull CompletableFuture<T> component(final @NonNull H holder, final @NonNull ComponentType componentType);

  /**
   * Returns a new {@link CompletableFuture} with the specified component
   * {@link Class} as a {@link ComponentType}.
   *
   * @param component The component class
   * @return A completable future with a component type
   * @since 0.1.0
   */
  <T extends C> @NonNull CompletableFuture<ComponentType> component(final @NonNull Class<T> component);

  /**
   * Returns the {@link ComponentRegistry}.
   *
   * @return The component registry
   * @since 0.1.0
   */
  @NonNull ComponentRegistry components();


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
     * Returns this {@link Builder} with the specified {@link MemberInjector.Factory}
     * holder injector.
     *
     * @param injector The holder injector
     * @return This builder
     * @since 0.1.0
     */
    @NonNull Builder<H, C> holderInjector(final MemberInjector.@NonNull Factory<?, H> injector);

    /**
     * Returns this {@link Builder} with the specified {@link MemberInjector.Factory}
     * component injector.
     *
     * @param injector The component injector
     * @return This builder
     * @since 0.1.0
     */
    @NonNull Builder<H, C> componentInjector(final MemberInjector.@NonNull Factory<?, C> injector);

    /**
     * Returns a new {@link Universe} from this {@link Builder}.
     *
     * @return A new universe
     * @since 0.1.0
     */
    @NonNull Universe<H, C> build();
  }
}
