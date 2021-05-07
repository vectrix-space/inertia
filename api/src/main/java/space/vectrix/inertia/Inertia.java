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
import space.vectrix.inertia.holder.Holder;

import java.util.Collection;
import java.util.function.Function;

/**
 * The static accessor to {@link Universe}s.
 *
 * @since 0.1.0
 */
public final class Inertia {
  /**
   * Creates a new {@link Universe} from the specified {@link Universe.Builder}
   * type passed into the {@link Function}.
   *
   * @param universeBuilder The universe builder type
   * @param buildingFunction The universe building function
   * @param <H> The holder type
   * @param <C> The component type
   * @return The new universe
   * @since 0.1.0
   */
  public static <H extends Holder<C>, C> Universe<H, C> create(final @NonNull Class<?> universeBuilder,
                                                               final @NonNull Function<Universe.Builder<H, C>, Universe<H, C>> buildingFunction) {
    final Universe.Builder<H, C> builder = Inertia.createBuilder(universeBuilder);
    final Universe<H, C> universe = buildingFunction.apply(builder);
    return Universes.compute(universe);
  }

  /**
   * Returns the {@link Universe} with the specified {@link String} identifier,
   * if it exists.
   *
   * @param identifier The universe identifier
   * @param <H> The holder type
   * @param <C> The component type
   * @return The universe, if present
   * @since 0.1.0
   */
  public static <H extends Holder<C>, C> @Nullable Universe<H, C> get(final @NonNull String identifier) {
    return Universes.get(identifier);
  }

  /**
   * Returns {@code true} if the {@link Universe} with the specified {@link String}
   * identifier is removed, otherwise returns {@code false}.
   *
   * @param identifier The universe identifier
   * @param <H> The holder type
   * @param <C> The component type
   * @return Whether the universe was removed
   * @since 0.1.0
   */
  public static <H extends Holder<C>, C> boolean destroy(final @NonNull String identifier) {
    final Universe<H, C> universe = Universes.remove(identifier);
    if(universe != null) {
      universe.clear();
      return true;
    }
    return false;
  }

  /**
   * Returns a {@link Collection} of the created {@link Universe}s.
   *
   * @return A collection of universes
   * @since 0.1.0
   */
  public static @NonNull Collection<Universe<?, ?>> all() {
    return Universes.getAll();
  }

  @SuppressWarnings("unchecked")
  private static <H extends Holder<C>, C> Universe.Builder<H, C> createBuilder(final @NonNull Class<?> universeBuilder) {
    try {
      return (Universe.Builder<H, C>) universeBuilder.getDeclaredConstructor().newInstance();
    } catch(final Throwable throwable) {
      throw new IllegalStateException("Unable to create universe builder!", throwable);
    }
  }
}
