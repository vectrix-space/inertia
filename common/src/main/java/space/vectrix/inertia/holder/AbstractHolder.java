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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.inertia.component.ComponentType;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class AbstractHolder<C> implements Holder<C> {
  private final Int2ObjectMap<C> components = new Int2ObjectOpenHashMap<>(100);
  private final Map<Class<?>, C> componentsTyped = new IdentityHashMap<>(50);
  private final Map<String, C> componentsNamed = new HashMap<>(50);
  private final int index;

  protected AbstractHolder(final int index) {
    this.index = index;
  }

  @Override
  public int getIndex() {
    return this.index;
  }

  @Override
  public <T extends C> boolean addComponent(final @NonNull ComponentType type, final @NonNull T component) {
    requireNonNull(type, "type");
    requireNonNull(component, "component");
    if (this.components.putIfAbsent(type.index(), component) == null) {
      this.componentsTyped.put(type.type(), component);
      this.componentsNamed.put(type.id(), component);
      return true;
    }
    return false;
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final int index) {
    return Optional.ofNullable((T) this.components.get(index));
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final @NonNull Class<T> type) {
    requireNonNull(type, "type");
    return Optional.ofNullable((T) this.componentsTyped.get(type));
  }

  @Override
  public @NonNull <T extends C> Optional<T> getComponent(final @NonNull String identifier) {
    requireNonNull(identifier, "identifier");
    return Optional.ofNullable((T) this.componentsNamed.get(identifier));
  }

  @Override
  public @NonNull Collection<? extends C> getComponents() {
    return this.components.values();
  }

  @Override
  public boolean removeComponent(final @NonNull ComponentType type) {
    requireNonNull(type, "type");
    if (this.components.remove(type.index()) != null) {
      this.componentsTyped.remove(type.type());
      this.componentsNamed.remove(type.name());
      return true;
    }
    return false;
  }

  @Override
  public void clearComponents() {
    this.components.clear();
    this.componentsTyped.clear();
    this.componentsNamed.clear();
  }
}
