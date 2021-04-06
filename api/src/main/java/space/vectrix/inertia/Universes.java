package space.vectrix.inertia;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.flare.SyncMap;
import space.vectrix.inertia.holder.Holder;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unchecked")
/* package */ final class Universes {
  private static final SyncMap<String, Universe<?, ?>> UNIVERSES = SyncMap.hashmap(5);

  /* package */ static <H extends Holder<C>, C> @NonNull Universe<H, C> compute(final @NonNull Universe<H, C> universe) {
    return (Universe<H, C>) Universes.UNIVERSES.computeIfAbsent(universe.id(), key -> universe);
  }

  /* package */ static <H extends Holder<C>, C> @Nullable Universe<H, C> get(final @NonNull String identifier) {
    return (Universe<H, C>) Universes.UNIVERSES.get(identifier);
  }

  /* package */ static @NonNull Collection<Universe<?, ?>> getAll() {
    return Collections.unmodifiableCollection(Universes.UNIVERSES.values());
  }
}
