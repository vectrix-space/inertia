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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;

/**
 * Functional interface that can inject members on a defined field on a target
 * object when needed.
 *
 * @param <T> The target type
 * @param <M> The member type
 * @since 0.1.0
 */
@FunctionalInterface
public interface MemberInjector<T, M> {
  /**
   * Injects the member at the appropriate field on the given target.
   *
   * @param target The target to inject into
   * @param member The member to inject
   * @throws Throwable If an exception occurred
   * @since 0.1.0
   */
  void member(final @NonNull T target, final @NonNull M member) throws Throwable;

  /**
   * The factory for creating an {@link MemberInjector}.
   *
   * @param <T> The target type
   * @param <M> The member type
   * @since 0.1.0
   */
  @FunctionalInterface
  interface Factory<T, M> {
    /**
     * Creates a new {@link MemberInjector} for the specified {@link Object}
     * target and {@link Field} field.
     *
     * @param object The target object
     * @param field The target field
     * @return A new member injector
     * @throws Exception If an exception occurred
     * @since 0.1.0
     */
    @NonNull MemberInjector<T, M> create(final @NonNull Object object, final @NonNull Field field) throws Exception;
  }
}
