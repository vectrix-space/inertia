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
import space.vectrix.inertia.injector.LmbdaInjectionMethodFactory;
import space.vectrix.inertia.injector.LmbdaInjectionStructureFactory;

class InjectorTest {
  final Universe.Builder<TestHolders, Object> builderDefaults(final Universe.Builder<TestHolders, Object> builder) {
    return builder
      .holderInjector(new LmbdaInjectionMethodFactory<>(), new LmbdaInjectionStructureFactory<>())
      .componentInjector(new LmbdaInjectionMethodFactory<>(), new LmbdaInjectionStructureFactory<>());
  }

  @Test
  void testInjection() {
    final Universe<TestHolders, Object> universe = this.builderDefaults(new UniverseImpl.Builder<>())
      .id("holder_universe")
      .build();
    final TestHolder holder = assertDoesNotThrow(() -> universe.createHolder(TestHolder::new).get());
    final ComponentType appleComponentType = assertDoesNotThrow(() -> universe.resolveComponent(AppleComponent.class).get());
    final ComponentType orangeComponentType = assertDoesNotThrow(() -> universe.resolveComponent(OrangeComponent.class).get());
    final OrangeComponent orangeComponent = assertDoesNotThrow(() -> universe.<OrangeComponent>createComponent(holder, orangeComponentType).get());
    assertNotNull(orangeComponent.getApple());
    assertNotNull(orangeComponent.getHolder());
    assertTrue(holder.get(appleComponentType).isPresent());
  }

  interface TestHolders extends Holder<Object> {}

  @Component(id = "apple", name = "Apple")
  public static final class AppleComponent {
    @HolderDependency private Holder<Object> holder;

    public AppleComponent() {}
  }

  @Component(id = "orange", name = "Orange")
  public static final class OrangeComponent {
    @ComponentDependency private AppleComponent appleComponent;
    @HolderDependency private Holder<Object> holder;

    public OrangeComponent() {}

    public AppleComponent getApple() {
      return this.appleComponent;
    }

    public Holder<Object> getHolder() {
      return this.holder;
    }
  }

  static final class TestHolder extends AbstractHolder<TestHolders, Object> implements TestHolders {
    private final Universe<TestHolders, Object> universe;

    protected TestHolder(final Universe<TestHolders, Object> universe, final int index) {
      super(universe, index);

      this.universe = universe;
    }

    public Universe<TestHolders, Object> universe() {
      return this.universe;
    }
  }
}
