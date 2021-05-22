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
import space.vectrix.inertia.annotation.Inject;

import java.util.Map;

/**
 * Represents the dependency structure of the specified target.
 *
 * @since 0.2.0
 */
public interface InjectionStructure {
  /**
   * Returns a {@link Map} of injectors.
   *
   * @return a map of injectors
   * @since 0.2.0
   */
  @NonNull Map<Class<?>, Entry> injectors();

  /**
   * A dependency structure entry.
   *
   * @since 0.2.0
   */
  interface Entry {
    /**
     * Returns the {@link Inject} dependency annotation.
     *
     * @return the dependency annotation
     * @since 0.2.0
     */
    @NonNull Inject annotation();

    /**
     * Returns the {@link InjectionMethod} for the dependency.
     *
     * @return the injection method
     * @since 0.2.0
     */
    @NonNull InjectionMethod method();
  }

  /**
   * The factory for creating {@link InjectionStructure}.
   *
   * @since 0.2.0
   */
  interface Factory {
    /**
     * Creates a new {@link InjectionStructure} for the specified {@link Class}
     * target and specified {@link InjectionMethod.Factory}s.
     *
     * @param target the target
     * @param injectionFactory the injection factory
     * @return the injection structure
     * @since 0.2.0
     */
    @NonNull InjectionStructure create(final @NonNull Class<?> target,
                                       final InjectionMethod.@NonNull Factory injectionFactory);
  }
}
