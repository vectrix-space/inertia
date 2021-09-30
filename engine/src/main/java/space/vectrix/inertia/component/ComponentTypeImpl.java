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
package space.vectrix.inertia.component;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/* package */ final class ComponentTypeImpl implements ComponentType {
  private final int index;
  private final String id;
  private final String name;
  private final Class<?> type;

  /* package */ ComponentTypeImpl(final @NonNegative int index,
                                  final @NonNull String id,
                                  final @NonNull String name,
                                  final @NonNull Class<?> type) {
    this.index = index;
    this.id = id;
    this.name = name;
    this.type = type;
  }

  @Override
  public @NonNegative int index() {
    return this.index;
  }

  @Override
  public @NonNull String id() {
    return this.id;
  }

  @Override
  public @NonNull String name() {
    return this.name;
  }

  @Override
  public @NonNull Class<?> type() {
    return this.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.index(), this.id(), this.name(), this.type());
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if(other == this) return true;
    if(!(other instanceof ComponentType)) return false;
    final ComponentType that = (ComponentType) other;
    return Objects.equals(this.index(), that.index())
      && Objects.equals(this.id(), that.id())
      && Objects.equals(this.name(), that.name())
      && Objects.equals(this.type(), that.type());
  }

  @Override
  public @NonNull String toString() {
    return "ComponentType{index=" + this.index +
      ", id=" + this.id + ", name=" + this.name +
      ", type=" + this.type + "}";
  }
}
