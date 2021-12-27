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
package space.vectrix.inertia.entity;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.util.CustomIterator;

/**
 * The abstract {@link Entity}.
 *
 * @since 0.3.0
 */
public abstract class AbstractEntity implements Entity {
  private final Universe universe;
  private final int index;

  protected AbstractEntity(final @NonNull Universe universe, final @NonNegative int index) {
    this.universe = universe;
    this.index = index;
  }

  @Override
  public @NonNull Universe universe() {
    return this.universe;
  }

  @Override
  public @NonNegative int index() {
    return this.index;
  }

  @Override
  public boolean contains(final @NonNull ComponentType type) {
    return this.universe.hasComponent(this, type);
  }

  @Override
  public <T> @Nullable T get(final @NonNull ComponentType type) {
    return this.universe.getComponent(this, type);
  }

  @Override
  public <T> @NonNull T add(final @NonNull ComponentType type) {
    return this.universe.addComponent(this, type);
  }

  @Override
  public void remove(final @NonNull ComponentType type) {
    this.universe.removeComponent(this, type);
  }

  @Override
  public void clear() {
    this.universe.clearComponents(this);
  }

  @Override
  public void destroy() {
    this.universe.removeEntity(this.index);
  }

  @Override
  public @NonNull CustomIterator<Object> components() {
    return this.universe.components(this);
  }
}
