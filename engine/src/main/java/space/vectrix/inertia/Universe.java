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
package space.vectrix.inertia;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.component.ComponentContainer;
import space.vectrix.inertia.holder.HolderContainer;
import space.vectrix.inertia.injection.InjectionMethod;
import space.vectrix.inertia.injection.InjectionStructure;

import java.util.function.Consumer;

/**
 * Represents the universe of holders and components.
 *
 * @since 0.2.0
 */
public interface Universe extends HolderContainer, ComponentContainer {
  /**
   * Creates a new {@link Universe} and returns it.
   *
   * @return the new universe
   * @since 0.2.0
   */
  static @NonNull Universe create() {
    return Universes.create(ignored -> {});
  }

  /**
   * Creates a new {@link Universe} using the builder {@link Consumer} and
   * returns it.
   *
   * @param builderConsumer the builder consumer
   * @return the new universe
   * @since 0.2.0
   */
  static @NonNull Universe create(final @NonNull Consumer<Universe.Builder> builderConsumer) {
    return Universes.create(builderConsumer);
  }

  /**
   * Returns the {@link Universe} with the specified {@code index}.
   *
   * @param index the universe index
   * @return the universe, if present
   * @since 0.2.0
   */
  static @Nullable Universe get(final int index) {
    return Universes.get(index);
  }

  /**
   * Returns the index for this universe.
   *
   * @return the universe index
   * @since 0.2.0
   */
  int index();

  /**
   * Destroys the universe.
   *
   * @since 0.2.0
   */
  default void destroy() {
    Universes.remove(this.index());
  }

  /**
   * Represents a builder used to create a universe.
   *
   * @since 0.2.0
   */
  interface Builder {
    /**
     * Returns this {@link Builder} with the specified
     * {@link InjectionStructure.Factory}.
     *
     * @param factory the injection structure factory
     * @return this builder
     * @since 0.2.0
     */
    @NonNull Builder injectionStructure(final InjectionStructure.@NonNull Factory factory);

    /**
     * Returns this {@link Builder} with the specified
     * {@link InjectionMethod.Factory}.
     *
     * @param factory the injection method factory
     * @return this builder
     * @since 0.2.0
     */
    @NonNull Builder injectionMethod(final InjectionMethod.@NonNull Factory factory);
  }
}
