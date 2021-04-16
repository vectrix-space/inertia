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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lanternpowered.lmbda.LambdaFactory;

import java.lang.invoke.MethodHandle;
import java.util.function.BiConsumer;

public final class LmbdaInjectionMethodFactory<T, M> implements InjectionMethod.Factory<T, M, MethodHandle> {
  private final LoadingCache<MethodHandle, LmbdaMemberInjector<T, M>> cache;

  public LmbdaInjectionMethodFactory() {
    this.cache = CacheBuilder.newBuilder()
      .initialCapacity(16)
      .weakValues()
      .build(CacheLoader.from(methodHandle -> {
        requireNonNull(methodHandle, "methodHandle");
        return new LmbdaMemberInjector<>(LambdaFactory.createBiConsumer(methodHandle));
      }));
  }

  @Override
  public @NonNull InjectionMethod<T, M> create(final @NonNull Object target, final @NonNull MethodHandle input) throws Exception {
    return this.cache.getUnchecked(input);
  }

  /* package */ static final class LmbdaMemberInjector<T, M> implements InjectionMethod<T, M> {
    private final BiConsumer<T, M> injector;

    /* package */ LmbdaMemberInjector(final BiConsumer<T, M> injector) {
      this.injector = injector;
    }

    @Override
    public void member(final @NonNull T target, final @NonNull M member) throws Throwable {
      this.injector.accept(target, member);
    }
  }
}
