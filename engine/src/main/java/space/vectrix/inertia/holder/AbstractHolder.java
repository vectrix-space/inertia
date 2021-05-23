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
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.util.version.Version;

import java.util.Collection;
import java.util.Optional;

/**
 * The abstract {@link Holder}.
 *
 * @since 0.2.0
 */
public abstract class AbstractHolder implements Holder {
  private final Universe universe;
  private final Version version;

  protected AbstractHolder(final @NonNull Universe universe, final int index) {
    this.universe = universe;
    this.version = Version.version(index, universe.index());
  }

  @Override
  public @NonNull Universe universe() {
    return this.universe;
  }

  @Override
  public @NonNull Version version() {
    return this.version;
  }

  @Override
  public boolean valid() {
    return this.universe.valid(this);
  }

  @Override
  public boolean contains(final @NonNull ComponentType componentType) {
    return this.universe.containsComponent(this, componentType);
  }

  @Override
  public <T> @Nullable T get(final @NonNull ComponentType componentType) {
    return this.universe.getComponent(this, componentType);
  }

  @Override
  public @NonNull <T> Optional<T> getPresent(final @NonNull ComponentType componentType) {
    return this.universe.getPresentComponent(this, componentType);
  }

  @Override
  public <T> @NonNull T add(final @NonNull ComponentType componentType) {
    return this.universe.addComponent(this, componentType);
  }

  @Override
  public <T> @Nullable T remove(final @NonNull ComponentType componentType) {
    return this.universe.removeComponent(this, componentType);
  }

  @Override
  public void clear() {
    this.universe.clearComponents(this);
  }

  @Override
  public @NonNull Collection<Object> components() {
    return this.universe.components(this);
  }
}
