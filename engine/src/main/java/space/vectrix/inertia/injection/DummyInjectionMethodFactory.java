package space.vectrix.inertia.injection;

import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class DummyInjectionMethodFactory implements InjectionMethod.Factory {
  private final DummyInjectionMethod injection = new DummyInjectionMethod();

  @Override
  public @NonNull InjectionMethod create(final @NonNull Object input) throws Throwable {
    requireNonNull(input, "input");
    return this.injection;
  }

  /* package */ static final class DummyInjectionMethod implements InjectionMethod {
    @Override
    public void member(final @NonNull Object target, final @NonNull Object member) throws Throwable {
      // no-op
    }
  }
}
