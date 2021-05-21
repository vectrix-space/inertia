package space.vectrix.inertia.holder;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.flare.SyncMap;
import space.vectrix.flare.fastutil.Int2ObjectSyncMap;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.util.counter.IndexCounter;
import space.vectrix.inertia.util.version.Version;

import java.util.Collection;
import java.util.IdentityHashMap;

public final class HolderContainerImpl implements HolderContainer {
  private final IntSet holderRemovals = Int2ObjectSyncMap.hashset(50);
  private final Int2ObjectSyncMap<Holder> holders = Int2ObjectSyncMap.hashmap(500);
  private final IndexCounter holderCounter = IndexCounter.counter("holders", this.holders);
  private final Multimap<Class<?>, Holder> holderGroup = Multimaps.newMultimap(
    SyncMap.of(IdentityHashMap<Class<?>, SyncMap.ExpungingValue<Collection<Holder>>>::new, 500),
    () -> SyncMap.hashset(100)
  );
  private final Universe universe;

  public HolderContainerImpl(final @NonNull Universe universe) {
    this.universe = universe;
  }

  @Override
  public @NonNull Holder createHolder() {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Holder> @NonNull T createHolder(final @NonNull HolderFunction<T> function) {
    requireNonNull(function, "function");
    final T holder = this.holderCounter.next(index -> (T) this.holders.computeIfAbsent(index, key -> function.apply(this.universe, key)));
    this.holderGroup.put(holder.getClass(), holder);
    return holder;
  }

  @Override
  public boolean valid(final @NonNull Holder holder) {
    final Version holderVersion = holder.version();
    if(!holderVersion.belongs(this.universe)) return false;
    if(!this.holders.containsKey(holderVersion.index())) return false;
    return !this.holderRemovals.contains(holderVersion.index());
  }
}
