package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.injector.MemberInjector;

public interface ComponentResolver<C, H> {
  @NonNull ComponentType resolve(final @Nullable ComponentType parent, final @NonNull Class<?> type);
  <T extends C> @NonNull T create(final @NonNull H holder, final MemberInjector.@NonNull Factory<?, C> injector, final @NonNull ComponentType componentType);

  @FunctionalInterface
  interface Factory {
    <C, H> @NonNull ComponentResolver<C, H> create(final @NonNull Universe<C, H> universe);
  }
}
