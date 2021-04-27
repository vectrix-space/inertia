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
package space.vectrix.inertia;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import space.vectrix.inertia.component.ComponentType;

import java.util.concurrent.CompletableFuture;

class ComponentTest extends AbstractUniverseTest {
  @Test
  void testGet() {
    final Universe<TestHolders, Object> universe = new UniverseImpl.Builder<TestHolders, Object>()
      .id("holder_universe")
      .build();
    final CompletableFuture<TestHolder> holderFuture = universe.createHolder(TestHolder::new);
    final TestHolder holder = assertDoesNotThrow(() -> holderFuture.get());
    final CompletableFuture<ComponentType> typeFuture = universe.resolveComponent(TestComponent.class);
    final ComponentType componentType = assertDoesNotThrow(() -> typeFuture.get());
    assertDoesNotThrow(() -> universe.createComponent(holder, componentType).get());
    assertTrue(universe.types().get(0).isPresent());
    assertFalse(universe.types().get(1).isPresent());
    assertTrue(universe.types().get(TestComponent.class).isPresent());
    assertFalse(universe.types().get(Object.class).isPresent());
    assertTrue(universe.types().get("test").isPresent());
    assertFalse(universe.types().get("fake").isPresent());
    assertThat(universe.types().all()).hasSize(1);
  }
}
