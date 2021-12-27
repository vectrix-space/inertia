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
import space.vectrix.inertia.system.System;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class UniverseTest {
  @Test
  public void testCreateUniverse() {
    final Universe universe = Universe.create();
    final int index = universe.index();

    assertNotNull(universe, "Universe should be created.");
    assertTrue(universe.active(), "Universe should be active.");
    assertEquals(universe, Universe.get(index), "Universe#get should equal the new universe.");
  }

  @Test
  public void testRemoveUniverse() {
    final Universe universe = Universe.create();
    final int index = universe.index();

    assertTrue(universe.active(), "Universe should be active.");
    assertEquals(universe, Universe.get(index), "Universe#get should equal the new universe.");
    universe.destroy();
    assertFalse(universe.active(), "Universe should be inactive.");
    assertThrows(InactiveUniverseException.class, universe::createEntity, "Universe#createEntity should throw an exception.");
    assertNull(Universe.get(index), "Universe should be null.");
  }

  @Test
  public void testIterateUniverses() {
    final Universe universe = Universe.create();
    final int index = universe.index();

    final Iterator<Universe> iterator = Universe.universes();
    assertNotNull(iterator, "Universe iterator should not be null.");
    assertTrue(iterator.hasNext(), "Universe iterator should have a next universe.");
    while(iterator.hasNext()) {
      final Universe element = iterator.next();
      final int elementIndex = element.index();

      if(elementIndex == index) {
        assertDoesNotThrow(iterator::remove, "Universe iterator should not throw an exception.");
        assertNull(Universe.get(index), "Universe should be null.");
        return;
      }
    }

    fail("Could not locate the created universe in the iterator.");
  }

  @Test
  public void testAddSystem() {
    final Universe universe = Universe.create();
    final System system = new SystemExample();

    assertDoesNotThrow(() -> universe.addSystem(system), "System addition should not throw an exception.");
    assertNotNull(universe.getSystem(SystemExample.class), "System should exist in the universe.");
    assertNull(universe.getSystem(System.class), "System should not exist in the universe.");
  }

  @Test
  public void testRemoveSystem() {
    final Universe universe = Universe.create();
    final System system = new SystemExample();

    universe.addSystem(system);

    assertNotNull(universe.getSystem(SystemExample.class), "System should exist in the universe.");
    assertDoesNotThrow(() -> universe.removeSystem(SystemExample.class), "System removal should not throw an exception.");
    assertNull(universe.getSystem(SystemExample.class), "System should not exist in the universe.");
  }

  @Test
  public void testIterateSystems() {
    final Universe universe = Universe.create();
    final System system = new SystemExample();

    universe.addSystem(system);

    final Iterator<System> iterator = universe.systems();
    assertNotNull(iterator, "System iterator should not be null.");
    assertTrue(iterator.hasNext(), "System iterator should have a next system.");
    while(iterator.hasNext()) {
      final System element = iterator.next();
      final Class<? extends System> elementClass = element.getClass();

      if(elementClass == SystemExample.class) {
        assertDoesNotThrow(iterator::remove, "System iterator should not throw an exception.");
        assertNull(universe.getSystem(SystemExample.class), "System should not exist in the universe.");
        return;
      }
    }

    fail("Could not locate the created system in the iterator.");
  }

  static final class SystemExample implements System {
    @Override
    public void execute() throws Throwable {}
  }
}
