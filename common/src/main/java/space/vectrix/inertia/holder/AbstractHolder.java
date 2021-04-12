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

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.component.type.ComponentType;

import java.util.Collection;
import java.util.Optional;

public abstract class AbstractHolder<C> implements Holder<C> {
  private final Universe<Holder<C>, C> universe;
  private final int index;

  protected AbstractHolder(final @NonNull Universe<Holder<C>, C> universe, final int index) {
    this.universe = universe;
    this.index = index;
  }

  @Override
  public int getIndex() {
    return this.index;
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final @NonNull ComponentType componentType) {
    return this.universe.getComponent(this, componentType);
  }

  @Override
  public boolean removeComponent(final @NonNull ComponentType type) {
    return this.universe.removeComponent(this, type);
  }

  @Override
  public @NonNull Collection<? extends C> getComponents() {
    return this.universe.components().all(this);
  }

  @Override
  public void clearComponents() {
    this.universe.removeComponents(this);
  }
}
