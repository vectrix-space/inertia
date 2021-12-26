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

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniverseTest {
  @Test
  public void testUniverseCreate() {
    final Universe universe = Universe.create();
    assertNotNull(universe, "Universe should be created.");
    assertEquals(0, universe.index(), "Universe index should be 0.");
    assertEquals(universe, Universe.get(0), "Universe#get should equal the new universe.");
  }

  @Test
  public void testUniverseDelete() {
    final Universe universe = Universe.get(0);
    assertNotNull(universe, "Universe should exist.");
    assertEquals(0, universe.index(), "Universe index should be 0.");
    assertTrue(universe.active(), "Universe should be active.");
    universe.destroy();
    assertFalse(universe.active(), "Universe should be inactive.");
    assertThrows(IllegalStateException.class, universe::createEntity, "Universe#createEntity should throw an exception.");
    assertNull(Universe.get(0), "Universe should be null.");
  }

  @Test
  public void testUniverseIterator() {
    final Universe secondUniverse = Universe.create();
    assertNotNull(secondUniverse, "Universe should be created.");
    assertEquals(1, secondUniverse.index(), "Universe index should be 1.");
    assertEquals(secondUniverse, Universe.get(1), "Universe#get should equal the new universe.");
    final Iterator<Universe> iterator = Universe.universes();
    assertNotNull(iterator, "Universe iterator should not be null.");
    assertTrue(iterator.hasNext(), "Universe iterator should have a next universe.");
    assertEquals(1, iterator.next().index(), "Universe iterator should have the universe.");
    assertFalse(iterator.hasNext(), "Universe iterator shouldn't have another universe.");
    assertDoesNotThrow(iterator::remove, "Universe iterator should not throw an exception.");
  }
}
