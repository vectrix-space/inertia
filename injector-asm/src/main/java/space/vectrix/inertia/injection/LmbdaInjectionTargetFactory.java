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
package space.vectrix.inertia.injection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.lanternpowered.lmbda.LambdaFactory;
import space.vectrix.flare.SyncMap;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

public final class LmbdaInjectionTargetFactory implements InjectionTarget.Factory {
  public static @NonNull LmbdaInjectionTargetFactory factory() {
    return new LmbdaInjectionTargetFactory();
  }

  private final Map<MethodHandle, LmbdaInjectionTarget> targets = SyncMap.hashmap(100);

  /* package */ LmbdaInjectionTargetFactory() {}

  @Override
  public @NonNull InjectionTarget create(final @NonNull Object input) throws Throwable {
    requireNonNull(input, "input");
    return this.targets.computeIfAbsent((MethodHandle) input, key -> new LmbdaInjectionTarget(LambdaFactory.createBiConsumer(key)));
  }

  /* package */ static final class LmbdaInjectionTarget implements InjectionTarget {
    private final BiConsumer<Object, Object> injector;

    /* package */ LmbdaInjectionTarget(final @NonNull BiConsumer<Object, Object> injector) {
      this.injector = injector;
    }

    @Override
    public void inject(final @NonNull Object target, final @NonNull Object member) throws Throwable {
      requireNonNull(target, "target");
      requireNonNull(member, "member");
      this.injector.accept(target, member);
    }
  }
}
