/*
 * This file is part of inertia, licensed under the MIT License (MIT).
 *
 * Copyright (c) vectrix.space <https://vectrix.space/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package space.vectrix.inertia.injector;

import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.ComponentDependency;
import space.vectrix.inertia.HolderDependency;

import java.util.Collections;
import java.util.Map;

public final class DummyInjectionStructureFactory<H, C> implements InjectionStructure.Factory<H, C> {
  private final DummyInjectionStructure<H, C> dummyStructure = new DummyInjectionStructure<>();

  public DummyInjectionStructureFactory() {}

  @Override
  public @NonNull InjectionStructure<H, C> create(final @NonNull Class<?> target,
                                                  final InjectionMethod.@NonNull Factory<?, C> componentInjectionFactory,
                                                  final InjectionMethod.@NonNull Factory<?, H> holderInjectionFactory) {
    requireNonNull(target, "target");
    return this.dummyStructure;
  }

  /* package */ static final class DummyInjectionStructure<H, C> implements InjectionStructure<H, C> {
    @Override
    public @NonNull Map<Class<?>, Entry<ComponentDependency, ?, C>> components() {
      return Collections.emptyMap();
    }

    @Override
    public @NonNull Map<Class<?>, Entry<HolderDependency, ?, H>> holders() {
      return Collections.emptyMap();
    }
  }
}
