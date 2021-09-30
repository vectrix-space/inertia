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
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.UniverseImpl;

import static java.util.Objects.requireNonNull;

/* package */ final class ComponentTypes {
  /* package */ static @NonNull ComponentType resolve(final @NonNull Universe universe, final @NonNull Class<?> target) {
    final UniverseImpl internal = (UniverseImpl) universe;
    return internal.resolveComponent(target, index -> ComponentTypes.create(index, target));
  }

  /* package */ static @NonNull ComponentType create(final @NonNegative int index, final @NonNull Class<?> target) {
    final Component annotation = target.getAnnotation(Component.class);
    if(annotation == null) throw new IllegalArgumentException("Target class '" + target.getSimpleName() + "' must have an @Component annotation!");
    return new ComponentTypeImpl(index, requireNonNull(annotation.id(), "id"), requireNonNull(annotation.name(), "name"), target);
  }
}
