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

/**
 * Functional interface that can inject members on a defined field on a target
 * object when needed.
 *
 * @since 0.3.0
 */
@FunctionalInterface
public interface InjectionTarget {
  /**
   * Injects the instance at the appropriate field on the given target.
   *
   * @param target the target to inject into
   * @param instance the instance to inject
   * @throws Throwable if a problem occurred attempting to inject
   * @since 0.3.0
   */
  void inject(final @NonNull Object target, final @NonNull Object instance) throws Throwable;

  /**
   * The factory for creating an {@link InjectionTarget}.
   *
   * @since 0.3.0
   */
  @FunctionalInterface
  interface Factory {
    /**
     * Creates a new {@link InjectionTarget} for the specified input.
     *
     * @param input the target input
     * @return the injection method
     * @throws Throwable if a problem occurred attempting to inject
     * @since 0.3.0
     */
    @NonNull InjectionTarget create(final @NonNull Object input) throws Throwable;
  }
}
