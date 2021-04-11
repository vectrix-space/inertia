package space.vectrix.inertia.component;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.holder.Holder;

import java.util.Collection;
import java.util.Optional;

public interface Components<H extends Holder<C>, C> {
  @NonNull Optional<C> get(final int holder, final @NonNull ComponentType componentType);
  @NonNull Optional<C> get(final @NonNull H holder, final @NonNull ComponentType componentType);
  @NonNull Collection<? extends C> all(final int holder);
  @NonNull Collection<? extends C> all(final @NonNull H holder);
  @NonNull Collection<? extends C> all();
}
