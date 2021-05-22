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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import space.vectrix.inertia.annotation.Component;
import space.vectrix.inertia.annotation.Inject;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.holder.AbstractHolder;
import space.vectrix.inertia.holder.Holder;

abstract class AbstractUniverseTest {
  /**
   * Creates a basic {@link Universe} to use in a test.
   *
   * @return the basic universe
   * @since 0.2.0
   */
  protected abstract @NonNull Universe createUniverse();

  // Holder

  @Test
  public void testHolderCreate() {
    final Universe universe = this.createUniverse();
    final Holder holder = universe.createHolder();
    assertEquals(universe, holder.universe(), "Holder should contain the owning universe.");
    assertEquals(0, holder.version().index(), "Holder should start at index 0.");
    assertEquals(universe.index(), holder.version().universe(), "Holder should contain universe index.");
    assertTrue(holder.valid(), "Holder should be valid when created.");
    assertFalse(universe.holders().isEmpty(), "Holders collection should not be empty.");
  }

  @Test
  public void testHolderCreateCustom() {
    final Universe universe = this.createUniverse();
    final Holder holder = universe.createHolder(HolderOne::new);
    assertEquals(universe, holder.universe(), "Holder should contain the owning universe.");
    assertEquals(0, holder.version().index(), "Holder should start at index 0.");
    assertEquals(universe.index(), holder.version().universe(), "Holder should contain universe index.");
    assertTrue(holder.valid(), "Holder should be valid when created.");
    assertFalse(universe.holders().isEmpty(), "Holders collection should not be empty.");
  }

  // Component Type

  @Test
  public void testComponentType() {
    final Universe universe = this.createUniverse();
    final ComponentType componentType;
    assertNotNull(componentType = universe.resolveType(ComponentOne.class), "Component type should exist on resolve.");
    assertEquals(componentType, universe.getType(0), "Component type should be returned.");
    assertEquals(componentType, universe.getType("one"), "Component type should be returned.");
    assertEquals(componentType, universe.getType(ComponentOne.class), "Component type should be returned.");
    assertFalse(universe.types().isEmpty(), "Component types collection should not be empty.");
  }

  // Component

  @Test
  public void testComponentAdd() {
    final Universe universe = this.createUniverse();
    final Holder holder = universe.createHolder();
    final ComponentType oneType = universe.resolveType(ComponentOne.class);
    final ComponentType twoType = universe.resolveType(ComponentTwo.class);
    final ComponentType threeType = universe.resolveType(ComponentThree.class);
    assertNotNull(universe.addComponent(holder, oneType), "Component should be created and returned.");
    assertNotNull(universe.addComponent(holder, twoType), "Component should be created and returned.");
    assertNotNull(universe.getComponent(holder, twoType), "Component should exist.");
    assertNull(universe.getComponent(holder, threeType), "Component should not exist.");
    assertNotNull(universe.removeComponent(holder, oneType), "Component should be removed.");
    assertNull(universe.removeComponent(holder, threeType), "Component should not be removed.");
    universe.clearComponents(holder);
  }

  // Holder Examples

  public static class HolderOne extends AbstractHolder {
    public HolderOne(final @NonNull Universe universe, final int index) {
      super(universe, index);
    }
  }

  public static class HolderTwo extends AbstractHolder {
    public HolderTwo(final @NonNull Universe universe, final int index) {
      super(universe, index);
    }
  }

  public static class HolderThree extends AbstractHolder {
    public HolderThree(final @NonNull Universe universe, final int index) {
      super(universe, index);
    }
  }

  public static class HolderFour extends AbstractHolder {
    public HolderFour(final @NonNull Universe universe, final int index) {
      super(universe, index);
    }
  }

  // Component Examples

  @Component(id = "one", name = "One")
  public static class ComponentOne {
    public ComponentOne() {}
  }

  @Component(id = "two", name = "Two")
  public static class ComponentTwo {
    private @Inject ComponentOne one;

    public ComponentTwo() {}
  }

  @Component(id = "three", name = "Three")
  public static class ComponentThree {
    private @Inject ComponentOne one;
    private @Inject ComponentTwo two;

    public ComponentThree() {}
  }

  @Component(id = "four", name = "Four")
  public static class ComponentFour {
    private @Inject ComponentOne one;
    private @Inject ComponentTwo two;
    private @Inject ComponentThree three;

    public ComponentFour() {}
  }
}
