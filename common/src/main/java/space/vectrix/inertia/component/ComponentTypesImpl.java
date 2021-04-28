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

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.flare.SyncMap;
import space.vectrix.inertia.holder.Holder;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public final class ComponentTypesImpl<H extends Holder<C>, C> extends AbstractComponentTypes<H, C> {
  private final Int2ObjectMap<ComponentTypeImpl<H, C>> components = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>(100));
  private final Map<Class<?>, ComponentTypeImpl<H, C>> typed = SyncMap.of(IdentityHashMap::new, 50);
  private final Map<String, ComponentTypeImpl<H, C>> named = SyncMap.hashmap(50);

  public ComponentTypesImpl() {}

  @Override
  public @NonNull Optional<ComponentType> get(final int index) {
    return Optional.ofNullable(this.components.get(index));
  }

  @Override
  public @NonNull Optional<ComponentType> get(final @NonNull Class<?> type) {
    requireNonNull(type, "type");
    return Optional.ofNullable(this.typed.get(type));
  }

  @Override
  public @NonNull Optional<ComponentType> get(final @NonNull String identifier) {
    requireNonNull(identifier, "identifier");
    return Optional.ofNullable(this.named.get(identifier));
  }

  @Override
  public @NonNull Collection<? extends ComponentType> all() {
    return this.components.values();
  }

  @Override
  public @NonNull ComponentTypeImpl<H, C> put(final @NonNull Class<?> type, final @NonNull Function<Class<?>, ComponentTypeImpl<H, C>> computation) {
    return this.typed.computeIfAbsent(type, key -> {
      final ComponentTypeImpl<H, C> componentType = computation.apply(key);
      this.components.put(componentType.index(), componentType);
      this.named.put(componentType.id(), componentType);
      return componentType;
    });
  }

  @Override
  public boolean contains(final int index) {
    return this.components.containsKey(index);
  }
}
