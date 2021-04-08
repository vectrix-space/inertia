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
    final Universe<Holder<Object>, Object> universe = new SimpleUniverse.Builder<>()
      .id("holder_universe")
      .build();
    assertDoesNotThrow(() -> universe.holder(TestHolder::new).get());
    assertTrue(universe.holders().get(0).isPresent());
    assertFalse(universe.holders().get(1).isPresent());
    assertFalse(universe.holders().get(TestHolder.class).isEmpty());
    assertThat(universe.holders().all()).hasSize(1);
  }

  @Test
  void testHolderIndex() {
    final Universe<Holder<Object>, Object> universe = new SimpleUniverse.Builder<>()
      .id("holder_universe")
      .build();
    final CompletableFuture<TestHolder> holderFuture = assertDoesNotThrow(() -> universe.holder(TestHolder::new));
    final TestHolder holder = assertDoesNotThrow(() -> holderFuture.get());
    assertEquals(0, holder.getIndex());
  }

  @Test
  void testHolderGet() {
    final Universe<Holder<Object>, Object> universe = new SimpleUniverse.Builder<>()
      .id("holder_universe")
      .build();
    final TestHolder holder = assertDoesNotThrow(() -> universe.holder(TestHolder::new).get());
    final ComponentType componentType = assertDoesNotThrow(() -> universe.component(TestComponent.class).get());
    assertDoesNotThrow(() -> universe.<TestComponent>component(holder, componentType).get());
    assertTrue(holder.getComponent(0).isPresent());
    assertFalse(holder.getComponent(1).isPresent());
    assertTrue(holder.getComponent(TestComponent.class).isPresent());
    assertFalse(holder.getComponent(Object.class).isPresent());
    assertTrue(holder.getComponent("test").isPresent());
    assertFalse(holder.getComponent("fake").isPresent());
    assertThat(holder.getComponents()).hasSize(1);
  }

  @Test
  void testHolderRemove() {
    final Universe<Holder<Object>, Object> universe = new SimpleUniverse.Builder<>()
      .id("holder_universe")
      .build();
    final TestHolder holder = assertDoesNotThrow(() -> universe.holder(TestHolder::new).get());
    final ComponentType componentType = assertDoesNotThrow(() -> universe.component(TestComponent.class).get());
    assertDoesNotThrow(() -> universe.<TestComponent>component(holder, componentType).get());
    assertTrue(holder.getComponent(componentType.index()).isPresent());
    assertTrue(holder.removeComponent(componentType));
    assertFalse(holder.getComponent(componentType.index()).isPresent());
  }

  @Test
  void testHolderClear() {
    final Universe<Holder<Object>, Object> universe = new SimpleUniverse.Builder<>()
      .id("holder_universe")
      .build();
    final TestHolder holder = assertDoesNotThrow(() -> universe.holder(TestHolder::new).get());
    final ComponentType componentType = assertDoesNotThrow(() -> universe.component(TestComponent.class).get());
    assertDoesNotThrow(() -> universe.<TestComponent>component(holder, componentType).get());
    assertTrue(holder.getComponent(componentType.index()).isPresent());
    assertDoesNotThrow(() -> holder.clearComponents());
    assertFalse(holder.getComponent(componentType.index()).isPresent());
  }
}
