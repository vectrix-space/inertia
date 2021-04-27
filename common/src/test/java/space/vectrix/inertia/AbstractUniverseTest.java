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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.holder.AbstractHolder;
import space.vectrix.inertia.holder.Holder;

import java.util.concurrent.CompletableFuture;

abstract class AbstractUniverseTest {
  @Test
  void testCreate() {
    assertThrows(IllegalStateException.class, () -> Inertia.create(Object.class, builder -> builder
      .id("invalid_universe")
      .build()));
    final Universe<Holder<Object>, Object> universe = assertDoesNotThrow(() -> Inertia.create(UniverseImpl.Builder.class, builder -> builder
      .id("valid_universe")
      .build()));
    assertEquals("valid_universe", universe.id());
  }

  @Test
  void testCreateHolder() {
    final Universe<TestHolders, Object> universe = new UniverseImpl.Builder<TestHolders, Object>()
      .id("holder_universe")
      .build();
    final CompletableFuture<TestHolder> holderFuture = assertDoesNotThrow(() -> universe.createHolder(TestHolder::new));
    final TestHolder holder = assertDoesNotThrow(() -> holderFuture.get());
    assertEquals(universe, holder.universe());
  }

  @Test
  void testResolveComponent() {
    final Universe<Holder<Object>, Object> universe = new UniverseImpl.Builder<>()
      .id("component_universe")
      .build();
    final CompletableFuture<ComponentType> typeFuture = universe.resolveComponent(TestComponent.class);
    assertDoesNotThrow(() -> typeFuture.get());
  }

  @Test
  void testCreateComponent() {
    final Universe<TestHolders, Object> universe = new UniverseImpl.Builder<TestHolders, Object>()
      .id("component_universe")
      .build();
    final CompletableFuture<TestHolder> holderFuture = universe.createHolder(TestHolder::new);
    final TestHolder holder = assertDoesNotThrow(() -> holderFuture.get());
    final CompletableFuture<ComponentType> typeFuture = universe.resolveComponent(TestComponent.class);
    final ComponentType componentType = assertDoesNotThrow(() -> typeFuture.get());
    final CompletableFuture<TestComponent> componentFuture = universe.createComponent(holder, componentType);
    assertDoesNotThrow(() -> componentFuture.get());
  }

  @Component(id = "test", name = "Test")
  public static final class TestComponent {
    private int tick;

    public TestComponent() {}

    public int getTick() {
      return this.tick;
    }

    public int next() {
      return ++this.tick;
    }
  }

  interface TestHolders extends Holder<Object> {}

  @Component(id = "another", name = "Another")
  public static final class AnotherComponent {
    public AnotherComponent() {}
  }

  static final class TestHolder extends AbstractHolder<TestHolders, Object> implements TestHolders {
    protected TestHolder(final Universe<TestHolders, Object> universe, final int index) {
      super(universe, index);
    }

    public Universe<TestHolders, Object> universe() {
      return this.universe;
    }
  }

  static final class AnotherHolder extends AbstractHolder<TestHolders, Object> implements TestHolders {
    protected AnotherHolder(final Universe<TestHolders, Object> universe, final int index) {
      super(universe, index);
    }

    public Universe<TestHolders, Object> universe() {
      return this.universe;
    }
  }
}
