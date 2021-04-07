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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import space.vectrix.inertia.component.SimpleComponentResolver;
import space.vectrix.inertia.holder.AbstractHolder;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.holder.SimpleHolderResolver;
import space.vectrix.inertia.injector.MemberInjector;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

abstract class AbstractUniverseTest {
  final Universe.Builder<Holder<Object>, Object> builderDefaults(final Universe.Builder<Holder<Object>, Object> builder) {
    return builder
      .holderResolver(SimpleHolderResolver.FACTORY)
      .componentResolver(SimpleComponentResolver.FACTORY)
      .holderInjector(new TestMemberInjectorFactory<>())
      .componentInjector(new TestMemberInjectorFactory<>());
  }

  @Test
  void testCreate() {
    assertThrows(IllegalStateException.class, () -> Inertia.create(Object.class, builder -> this.builderDefaults(builder)
      .id("valid_universe")
      .build()));
    final Universe<Holder<Object>, Object> universe = assertDoesNotThrow(() -> Inertia.create(SimpleUniverse.Builder.class, builder -> this.builderDefaults(builder)
      .id("valid_universe")
      .build()));
    assertEquals("valid_universe", universe.id());
  }

  @Test
  void testCreateHolder() {
    final Universe<Holder<Object>, Object> universe = this.builderDefaults(new SimpleUniverse.Builder<>())
      .id("holder_universe")
      .build();
    final CompletableFuture<TestHolder> holderFuture = assertDoesNotThrow(() -> universe.holder(TestHolder::new));
    final TestHolder holder = assertDoesNotThrow(() -> holderFuture.get());
    assertEquals(universe, holder.universe());
  }

  @Test
  void testCreateComponent() {
    final Universe<Holder<Object>, Object> universe = this.builderDefaults(new SimpleUniverse.Builder<>())
      .id("component_universe")
      .build();
    final CompletableFuture<TestHolder> holderFuture = universe.holder(TestHolder::new);
    final TestHolder holder = assertDoesNotThrow(() -> holderFuture.get());
    final CompletableFuture<TestComponent> componentFuture = universe.component(holder, TestComponent.class);
    assertDoesNotThrow(() -> componentFuture.get());
  }

  @Component(id = "test", name = "Test")
  public static final class TestComponent {
    public TestComponent() {}
  }

  static final class TestHolder extends AbstractHolder<Object> {
    private final Universe<Holder<Object>, Object> universe;

    protected TestHolder(final Universe<Holder<Object>, Object> universe, final int index) {
      super(index);

      this.universe = universe;
    }

    public Universe<Holder<Object>, Object> universe() {
      return this.universe;
    }
  }

  static final class TestMemberInjectorFactory<M> implements MemberInjector.Factory<Object, M> {
    @Override
    public @NonNull MemberInjector<Object, M> create(final @NonNull Object target, final @NonNull Field field) throws Exception {
      return new TestMemberInjector<>();
    }
  }

  static final class TestMemberInjector<M> implements MemberInjector<Object, M> {
    @Override
    public void member(final @NonNull Object target, final @NonNull M member) throws Throwable {
      // No-op
    }
  }
}
