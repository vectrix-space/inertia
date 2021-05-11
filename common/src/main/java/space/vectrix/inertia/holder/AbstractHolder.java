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
package space.vectrix.inertia.holder;

import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.component.ComponentType;

import java.util.Collection;
import java.util.Optional;

/**
 * {@inheritDoc}
 *
 * Provides a convenient implementation of the {@link Holder}
 * methods.
 *
 * @param <H> The holder type
 * @param <C> The component type
 * @since 0.1.0
 */
public abstract class AbstractHolder<H extends Holder<C>, C> implements Holder<C> {
  protected final Universe<H, C> universe;
  private final int index;

  protected AbstractHolder(final @NonNull Universe<H, C> universe, final int index) {
    this.universe = universe;
    this.index = index;
  }

  @Override
  public int index() {
    return this.index;
  }

  @Override
  public @NonNull <T extends C> Optional<T> get(final @NonNull ComponentType componentType) {
    requireNonNull(componentType, "componentType");
    return this.universe.getComponent(this.index, componentType);
  }

  @Override
  public boolean remove(final @NonNull ComponentType componentType) {
    requireNonNull(componentType, "componentType");
    return this.universe.removeComponent(this.index, componentType);
  }

  @Override
  public @NonNull Collection<? extends C> all() {
    return this.universe.components().all(this.index);
  }

  @Override
  public void clear() {
    this.universe.removeComponents(this.index);
  }

  @Override
  public int hashCode() {
    return this.index;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if(other == this) return true;
    if(!(other instanceof AbstractHolder)) return false;
    final AbstractHolder<?, ?> that = (AbstractHolder<?, ?>) other;
    return this.index == that.index();
  }

  @Override
  public String toString() {
    return "Holder{index=" + this.index + ", universe=" + this.universe.id() + "}";
  }
}
