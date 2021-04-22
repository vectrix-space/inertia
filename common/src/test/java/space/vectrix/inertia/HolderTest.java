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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.holder.Holder;

import java.util.concurrent.CompletableFuture;

class HolderTest extends AbstractUniverseTest {
  @Test
  void testGet() {
    final Universe<Holder<Object>, Object> universe = new UniverseImpl.Builder<>()
      .id("holder_universe")
      .build();
    assertDoesNotThrow(() -> universe.createHolder(TestHolder::new).get());
    assertTrue(universe.holders().get(0).isPresent());
    assertFalse(universe.holders().get(1).isPresent());
    assertTrue(universe.holders().all(AnotherHolder.class).isEmpty());
    assertFalse(universe.holders().all(TestHolder.class).isEmpty());
    assertThat(universe.holders().all()).hasSize(1);
  }

  @Test
  void testHolderIndex() {
    final Universe<Holder<Object>, Object> universe = new UniverseImpl.Builder<>()
      .id("holder_universe")
      .build();
    final CompletableFuture<TestHolder> holderFuture = assertDoesNotThrow(() -> universe.createHolder(TestHolder::new));
    final TestHolder holder = assertDoesNotThrow(() -> holderFuture.get());
    assertEquals(0, holder.index());
  }

  @Test
  void testHolderGet() {
    final Universe<Holder<Object>, Object> universe = new UniverseImpl.Builder<>()
      .id("holder_universe")
      .build();
    final TestHolder holder = assertDoesNotThrow(() -> universe.createHolder(TestHolder::new).get());
    final ComponentType firstType = assertDoesNotThrow(() -> universe.resolveComponent(TestComponent.class).get());
    final ComponentType secondType = assertDoesNotThrow(() -> universe.resolveComponent(AnotherComponent.class).get());
    assertDoesNotThrow(() -> universe.<TestComponent>createComponent(holder, firstType).get());
    assertTrue(holder.get(firstType).isPresent());
    assertFalse(holder.get(secondType).isPresent());
    assertThat(holder.all()).hasSize(1);
  }

  @Test
  void testHolderRemove() {
    final Universe<Holder<Object>, Object> universe = new UniverseImpl.Builder<>()
      .id("holder_universe")
      .build();
    final TestHolder holder = assertDoesNotThrow(() -> universe.createHolder(TestHolder::new).get());
    final ComponentType componentType = assertDoesNotThrow(() -> universe.resolveComponent(TestComponent.class).get());
    assertDoesNotThrow(() -> universe.<TestComponent>createComponent(holder, componentType).get());
    assertTrue(holder.get(componentType).isPresent());
    assertTrue(holder.remove(componentType));
    assertFalse(holder.get(componentType).isPresent());
  }

  @Test
  void testHolderClear() {
    final Universe<Holder<Object>, Object> universe = new UniverseImpl.Builder<>()
      .id("holder_universe")
      .build();
    final TestHolder holder = assertDoesNotThrow(() -> universe.createHolder(TestHolder::new).get());
    final ComponentType componentType = assertDoesNotThrow(() -> universe.resolveComponent(TestComponent.class).get());
    assertDoesNotThrow(() -> universe.<TestComponent>createComponent(holder, componentType).get());
    assertTrue(holder.get(componentType).isPresent());
    assertDoesNotThrow(() -> holder.clear());
    assertFalse(holder.get(componentType).isPresent());
  }
}
