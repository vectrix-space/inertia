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

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class SimpleComponentRegistry implements ComponentRegistry {
  private final Int2ObjectMap<ComponentType> components = new Int2ObjectOpenHashMap<>(100);
  private final Map<Class<?>, ComponentType> componentsTyped = new IdentityHashMap<>(50);
  private final Map<String, ComponentType> componentsNamed = new HashMap<>(50);

  public SimpleComponentRegistry() {}

  @Override
  public @Nullable ComponentType get(final int index) {
    return this.components.get(index);
  }

  @Override
  public @Nullable ComponentType get(final @NonNull Class<?> type) {
    return this.componentsTyped.get(type);
  }

  @Override
  public @Nullable ComponentType get(final @NonNull String identifier) {
    return this.componentsNamed.get(identifier);
  }

  public @NonNull ComponentType add(final @NonNull ComponentType type) {
    this.components.putIfAbsent(type.index(), type);
    this.componentsTyped.putIfAbsent(type.type(), type);
    this.componentsNamed.putIfAbsent(type.id(), type);
    return type;
  }

  public @NonNull ComponentType computeIfAbsent(final @NonNull Class<?> type, final @NonNull Function<Class<?>, ComponentType> computation) {
    return this.componentsTyped.computeIfAbsent(type, key -> {
      final ComponentType componentType = computation.apply(key);
      this.components.put(componentType.index(), componentType);
      this.componentsNamed.put(componentType.id(), componentType);
      return componentType;
    });
  }
}
