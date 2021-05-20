package space.vectrix.inertia.injection;

import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Map;

public final class DummyInjectionStructureFactory implements InjectionStructure.Factory {
  private final DummyInjectionStructure structure = new DummyInjectionStructure();

  @Override
  public @NonNull InjectionStructure create(final @NonNull Class<?> target, final InjectionMethod.@NonNull Factory injectionFactory) {
    requireNonNull(target, "target");
    return this.structure;
  }

  /* package */ static final class DummyInjectionStructure implements InjectionStructure {
    @Override
    public @NonNull Map<Class<?>, Entry> injectors() {
      return Collections.emptyMap();
    }
  }
}
