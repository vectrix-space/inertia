package space.vectrix.inertia.holder;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;

import java.util.concurrent.atomic.AtomicInteger;

public final class SimpleHolderResolver<H extends Holder<C>, C> implements HolderResolver<H, C> {
  public static SimpleHolderResolver.Factory FACTORY = new SimpleHolderResolver.Factory();

  private final AtomicInteger index = new AtomicInteger();

  private final Universe<H, C> universe;

  /* package */ SimpleHolderResolver(final Universe<H, C> universe) {
    this.universe = universe;
  }

  @Override
  public <T extends H> @NonNull T create(final @NonNull HolderFunction<H, C, T> holderFunction) {
    // TODO: Store this somewhere and do other injection things.
    return holderFunction.apply(this.universe, this.index.getAndIncrement());
  }

  public static final class Factory implements HolderResolver.Factory {
    /* package */ Factory() {}

    @Override
    public @NonNull <H extends Holder<C>, C> HolderResolver<H, C> create(final @NonNull Universe<H, C> universe) {
      return new SimpleHolderResolver<>(universe);
    }
  }
}
