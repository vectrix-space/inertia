package space.vectrix.inertia.injector;

import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;

public final class DummyMemberInjectorFactory<T, M> implements MemberInjector.Factory<T, M> {
  private final DummyMemberInjector<T, M> dummyInjector = new DummyMemberInjector<>();

  public DummyMemberInjectorFactory() {}

  @Override
  public @NonNull MemberInjector<T, M> create(final @NonNull Object target, final @NonNull Field field) throws Exception {
    requireNonNull(target, "target");
    requireNonNull(field, "field");
    return this.dummyInjector;
  }

  /* package */ static final class DummyMemberInjector<T, M> implements MemberInjector<T, M> {
    @Override
    public void member(final @NonNull T target, final @NonNull M member) throws Throwable {
      // no-op
    }
  }
}
