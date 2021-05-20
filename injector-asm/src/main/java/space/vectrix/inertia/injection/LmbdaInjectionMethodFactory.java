package space.vectrix.inertia.injection;

import static java.util.Objects.requireNonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lanternpowered.lmbda.LambdaFactory;

import java.lang.invoke.MethodHandle;
import java.util.function.BiConsumer;

public final class LmbdaInjectionMethodFactory implements InjectionMethod.Factory {
  private final LoadingCache<MethodHandle, LmbdaMemberInjector> cache;

  public LmbdaInjectionMethodFactory() {
    this.cache = CacheBuilder.newBuilder()
      .initialCapacity(16)
      .weakValues()
      .build(CacheLoader.from(methodHandle -> {
        requireNonNull(methodHandle, "methodHandle");
        return new LmbdaMemberInjector(LambdaFactory.createBiConsumer(methodHandle));
      }));
  }

  @Override
  public @NonNull InjectionMethod create(final @NonNull Object input) throws Throwable {
    return this.cache.getUnchecked((MethodHandle) input);
  }

  /* package */ static final class LmbdaMemberInjector implements InjectionMethod {
    private final BiConsumer<Object, Object> injector;

    /* package */ LmbdaMemberInjector(final BiConsumer<Object, Object> injector) {
      this.injector = injector;
    }

    @Override
    public void member(final @NonNull Object target, final @NonNull Object member) throws Throwable {
      this.injector.accept(target, member);
    }
  }
}
