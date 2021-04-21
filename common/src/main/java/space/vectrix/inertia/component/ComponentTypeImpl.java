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

import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.flare.SyncMap;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.injector.InjectionStructure;

import java.util.Set;

public final class ComponentTypeImpl<H extends Holder<C>, C> implements ComponentType {
  private final Set<ComponentLink> dependencies = SyncMap.hashset(10);
  private final int index;
  private final String id;
  private final String name;
  private final Class<?> component;
  private final InjectionStructure<H, C> structure;

  public ComponentTypeImpl(final int index,
                           final String id,
                           final String name,
                           final Class<?> component,
                           final InjectionStructure<H, C> structure) {
    this.index = index;
    this.id = requireNonNull(id, "id");
    this.name = requireNonNull(name, "name");
    this.component = requireNonNull(component, "component");
    this.structure = requireNonNull(structure, "structure");
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
  public @NonNull Set<ComponentLink> dependencies() {
    return this.dependencies;
  }

  /**
   * Returns the {@link InjectionStructure} for this component
   * type.
   *
   * @return The injection structure
   * @since 0.1.0
   */
  public @NonNull InjectionStructure<H, C> structure() {
    return this.structure;
  }

  /**
   * Adds the specified {@link ComponentLink} dependency to this
   * component type.
   *
   * @param link The dependency link
   * @since 0.1.0
   */
  public void dependency(final @NonNull ComponentLink link) {
    this.dependencies.add(link);
  }
}
