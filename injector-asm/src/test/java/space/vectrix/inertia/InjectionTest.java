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

import org.junit.jupiter.api.Test;
import space.vectrix.inertia.component.Component;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.entity.Entity;
import space.vectrix.inertia.injection.LmbdaInjectionStructureFactory;
import space.vectrix.inertia.system.Dependency;
import space.vectrix.inertia.system.System;
import space.vectrix.inertia.util.CustomIterator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class InjectionTest {
  @Test
  public void testUniverseTick() {
    final Universe universe = Universe.create();

    universe.injector(new LmbdaInjectionStructureFactory());

    final Entity firstEntity = universe.createEntity();
    final Entity secondEntity = universe.createEntity();

    final System firstSystem = () -> new System() {
      @Dependency(Foo.class) private ComponentType fooType;
      @Dependency(Bar.class) private ComponentType barType;
      @Dependency(value = Baz.class, optional = true) private ComponentType bazType;

      private boolean firstTick = false;
      private boolean initialized = false;

      @Override
      public boolean initialized() {
        return this.initialized;
      }

      @Override
      public void initialize() {
        firstEntity.add(this.fooType);
        secondEntity.add(this.fooType);
        secondEntity.add(this.barType);

        if(this.bazType != null) {
          fail("Baz should not be present.");
        }

        this.initialized = true;
      }

      @Override
      public void execute() {
        assertNotNull(this.fooType, "Component type should not be null.");
        assertNotNull(this.barType, "Component type should not be null.");

        final CustomIterator<Entity> firstIterator = universe.entities();
        assertTrue(firstIterator.hasNext(), "Iterator should have next.");
        assertNotNull(firstIterator.next(), "Entity iterator should have a next entity.");
        assertTrue(firstIterator.hasNext(), "Iterator should have next.");
        assertNotNull(firstIterator.next(), "Entity iterator should have a next entity.");
        assertFalse(firstIterator.hasNext(), "Iterator should not have next.");

        final CustomIterator<Entity> secondIterator = universe.entities()
          .with(entity -> entity.contains(this.fooType))
          .without(entity -> entity.contains(this.barType));

        assertNotNull(secondIterator, "Entity iterator should not be null.");
        assertTrue(secondIterator.hasNext(), "Entity iterator should have a next entity.");
        assertNotNull(secondIterator.next(), "Entity iterator should have a next entity.");
        assertFalse(secondIterator.hasNext(), "Entity iterator should not have a next entity.");

        if(this.firstTick && this.bazType == null) {
          fail("Baz component should be present.");
        }

        this.firstTick = true;
      }
    };

    final System secondSystem = () -> new System() {
      @Dependency(Foo.class) private ComponentType fooType;
      @Dependency(Bar.class) private ComponentType barType;
      @Dependency(value = Baz.class, optional = true) private ComponentType bazType;

      private boolean firstTick = false;
      private boolean initialized = false;

      @Override
      public boolean initialized() {
        return this.initialized;
      }

      @Override
      public void initialize() {
        firstEntity.add(this.fooType);
        secondEntity.add(this.fooType);
        secondEntity.add(this.barType);

        if(this.bazType != null) {
          fail("Baz should not be present.");
        }

        this.initialized = true;
      }

      @Override
      public void execute() {
        assertNotNull(this.fooType, "Component type should not be null.");
        assertNotNull(this.barType, "Component type should not be null.");

        final CustomIterator<Entity> firstIterator = universe.entities();
        assertTrue(firstIterator.hasNext(), "Iterator should have next.");
        assertNotNull(firstIterator.next(), "Entity iterator should have a next entity.");
        assertTrue(firstIterator.hasNext(), "Iterator should have next.");
        assertNotNull(firstIterator.next(), "Entity iterator should have a next entity.");
        assertFalse(firstIterator.hasNext(), "Iterator should not have next.");

        final CustomIterator<Entity> secondIterator = universe.entities()
          .with(entity -> entity.contains(this.fooType))
          .without(entity -> entity.contains(this.barType));

        assertNotNull(secondIterator, "Entity iterator should not be null.");
        assertTrue(secondIterator.hasNext(), "Entity iterator should have a next entity.");
        assertNotNull(secondIterator.next(), "Entity iterator should have a next entity.");
        assertFalse(secondIterator.hasNext(), "Entity iterator should not have a next entity.");

        if(this.firstTick && this.bazType == null) {
          fail("Baz component should be present.");
        }

        this.firstTick = true;
      }
    };

    universe.addSystem(firstSystem);
    universe.addSystem(secondSystem);

    final Universe.Tick firstTick = assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertEquals(0, firstTick.time(), "Tick time should be 0.");

    ComponentType.create(universe, Baz.class);

    final Universe.Tick secondTick = assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertEquals(1, secondTick.time(), "Tick time should be 1.");
  }

  @Component(id = "foo_example", name = "Foo Example")
  static final class Foo {
    // No-op
  }

  @Component(id = "bar_example", name = "Bar Example")
  static final class Bar {
    // No-op
  }

  @Component(id = "baz_example", name = "Baz Example")
  static final class Baz {
    // No-op
  }
}
