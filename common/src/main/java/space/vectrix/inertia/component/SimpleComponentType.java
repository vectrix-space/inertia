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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

public final class SimpleComponentType implements ComponentType {
  private final Set<ComponentType> requiredDependencies = new HashSet<>();
  private final Set<ComponentType> optionalDependencies = new HashSet<>();
  private final int index;
  private final String id;
  private final String name;
  private final Class<?> component;
  private final ComponentStructure structure;

  public SimpleComponentType(final int index,
                             final String id,
                             final String name,
                             final Class<?> component,
                             final ComponentStructure structure) {
    this.index = index;
    this.id = id;
    this.name = name;
    this.component = component;
    this.structure = structure;
  }

  @Override
  public int index() {
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
    return this.component;
  }

  @Override
  public @NonNull Set<ComponentType> requiredDependencies() {
    return this.requiredDependencies;
  }

  @Override
  public @NonNull Set<ComponentType> optionalDependencies() {
    return this.optionalDependencies;
  }

  public @NonNull ComponentStructure structure() {
    return this.structure;
  }
}
