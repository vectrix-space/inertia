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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.holder.AbstractHolder;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.injector.ASMMemberInjectorFactory;

class InjectorTest {
  final Universe.Builder<Holder<Object>, Object> builderDefaults(final Universe.Builder<Holder<Object>, Object> builder) {
    return builder
      .holderInjector(new ASMMemberInjectorFactory<>())
      .componentInjector(new ASMMemberInjectorFactory<>());
  }

  @Test
  void testInjection() {
    final Universe<Holder<Object>, Object> universe = this.builderDefaults(new SimpleUniverse.Builder<>())
      .id("holder_universe")
      .build();
    final TestHolder holder = assertDoesNotThrow(() -> universe.holder(TestHolder::new).get());
    final ComponentType appleComponentType = assertDoesNotThrow(() -> universe.component(AppleComponent.class).get());
    final ComponentType orangeComponentType = assertDoesNotThrow(() -> universe.component(OrangeComponent.class).get());
    final OrangeComponent orangeComponent = assertDoesNotThrow(() -> universe.<OrangeComponent>component(holder, orangeComponentType).get());
    assertNotNull(orangeComponent.getApple());
    assertNotNull(orangeComponent.getHolder());
    assertTrue(holder.getComponent(AppleComponent.class).isPresent());
  }

  @Component(id = "apple", name = "Apple")
  public static final class AppleComponent {
    @HolderDependency public Holder<Object> holder;

    public AppleComponent() {}
  }

  @Component(id = "orange", name = "Orange")
  public static final class OrangeComponent {
    @ComponentDependency public AppleComponent appleComponent;
    @HolderDependency public Holder<Object> holder;

    public OrangeComponent() {}

    public AppleComponent getApple() {
      return this.appleComponent;
    }

    public Holder<Object> getHolder() {
      return this.holder;
    }
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
}
