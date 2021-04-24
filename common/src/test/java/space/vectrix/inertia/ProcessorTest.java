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

import net.kyori.coffee.math.range.i.IntRange;
import org.junit.jupiter.api.Test;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.processor.Processor;

class ProcessorTest extends AbstractUniverseTest {
  @Test
  void testGet() {
    final Universe<TestHolders, Object> universe = new UniverseImpl.Builder<TestHolders, Object>()
      .id("processor_universe")
      .build();
    assertDoesNotThrow(() -> universe.createProcessor(TestProcessor.class, TestProcessor::new).get());
    assertTrue(universe.processors().get(TestProcessor.class).isPresent());
    assertFalse(universe.processors().get(AnotherProcessor.class).isPresent());
    assertThat(universe.processors().all(0)).isNotEmpty();
    assertThat(universe.processors().all(1)).isEmpty();
    assertThat(universe.processors().all(IntRange.between(0, 1))).isNotEmpty();
    assertThat(universe.processors().all()).isNotEmpty();
    assertDoesNotThrow(() -> universe.createProcessor(AnotherProcessor.class, AnotherProcessor::new).get());
    assertThat(universe.processors().all(2)).isNotEmpty();
    assertThat(universe.processors().all(IntRange.between(1, 2))).isNotEmpty();
    assertThat(universe.processors().all(IntRange.between(3, 4))).isEmpty();
  }

  @Test
  void testProcessing() {
    final Universe<TestHolders, Object> universe = new UniverseImpl.Builder<TestHolders, Object>()
      .id("processor_universe")
      .build();
    final TestHolder holder = assertDoesNotThrow(() -> universe.createHolder(TestHolder::new).get());
    final ComponentType componentType = assertDoesNotThrow(() -> universe.resolveComponent(TestComponent.class).get());
    final TestComponent component = assertDoesNotThrow(() -> universe.<TestComponent>createComponent(holder, componentType).get());
    assertDoesNotThrow(() -> universe.createProcessor(TestProcessor.class, TestProcessor::new).get());
    assertEquals(0, universe.tick());
    assertEquals(1, component.getTick());
    assertDoesNotThrow(() -> universe.createProcessor(AnotherProcessor.class, AnotherProcessor::new).get());
    assertEquals(1, universe.tick());
    assertEquals(3, component.getTick());
  }

  @Test
  void testProcessorRemove() {
    final Universe<TestHolders, Object> universe = new UniverseImpl.Builder<TestHolders, Object>()
      .id("processor_universe")
      .build();
    assertDoesNotThrow(() -> universe.createProcessor(TestProcessor.class, TestProcessor::new).get());
    assertTrue(universe.processors().get(TestProcessor.class).isPresent());
    assertTrue(universe.removeProcessor(TestProcessor.class));
    assertFalse(universe.processors().get(TestProcessor.class).isPresent());
  }

  static final class TestProcessor implements Processor<TestHolders, Object> {
    private final Universe<TestHolders, Object> universe;

    public TestProcessor(final Universe<TestHolders, Object> universe) {
      this.universe = universe;
    }

    @Override
    public void execute() throws Throwable {
      final ComponentType testComponent = this.universe.resolveComponent(TestComponent.class).get();
      for(final Holder<Object> holder : this.universe.holders().all()) {
        holder.<TestComponent>get(testComponent).ifPresent(TestComponent::next);
      }
    }
  }

  static final class AnotherProcessor implements Processor<TestHolders, Object> {
    private final Universe<TestHolders, Object> universe;

    public AnotherProcessor(final Universe<TestHolders, Object> universe) {
      this.universe = universe;
    }

    @Override
    public int priority() {
      return 2;
    }

    @Override
    public void execute() throws Throwable {
      final ComponentType testComponent = this.universe.resolveComponent(TestComponent.class).get();
      for(final Holder<Object> holder : this.universe.holders().all()) {
        holder.<TestComponent>get(testComponent).ifPresent(TestComponent::next);
      }
    }
  }
}
