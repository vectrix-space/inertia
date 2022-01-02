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

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import space.vectrix.inertia.component.Component;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.entity.AbstractEntity;
import space.vectrix.inertia.entity.Entity;
import space.vectrix.inertia.entity.EntityStash;
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

  @Test
  public void testCreateEntity() {
    final Universe universe = Universe.create();
    final Entity firstEntity = assertDoesNotThrow(() -> universe.createEntity(), "Entity creation should not throw an exception.");

    assertNotNull(firstEntity, "Entity should be created.");
    assertEquals(firstEntity, universe.getEntity(firstEntity.index()), "Entity#get should equal the new entity.");
    assertTrue(universe.hasEntity(firstEntity), "Universe#hasEntity should return true.");

    final EntityExample secondEntity = assertDoesNotThrow(() -> universe.createEntity(EntityExample::new), "Entity creation should not throw an exception.");

    assertNotNull(secondEntity, "Entity should be created.");
    assertEquals(secondEntity, universe.getEntity(secondEntity.index()), "Entity#get should equal the new entity.");
    assertTrue(universe.hasEntity(secondEntity), "Universe#hasEntity should return true.");
  }

  @Test
  public void testRemoveEntity() {
    final Universe universe = Universe.create();
    final Entity firstEntity = universe.createEntity();

    assertDoesNotThrow(() -> universe.removeEntity(firstEntity), "Entity removal should not throw an exception.");
    assertNotNull(universe.getEntity(firstEntity.index()), "Entity should exist in the universe.");
    assertTrue(universe.hasEntity(firstEntity), "Universe#hasEntity should return true.");
    assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertNull(universe.getEntity(firstEntity.index()), "Entity should not exist in the universe.");
    assertFalse(universe.hasEntity(firstEntity), "Universe#hasEntity should return false.");

    final Entity secondEntity = universe.createEntity();

    assertDoesNotThrow(() -> universe.removeEntity(secondEntity.index()), "Entity removal should not throw an exception.");
    assertNotNull(universe.getEntity(secondEntity.index()), "Entity should exist in the universe.");
    assertTrue(universe.hasEntity(secondEntity), "Universe#hasEntity should return true.");
    assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertNull(universe.getEntity(secondEntity.index()), "Entity should not exist in the universe.");
    assertFalse(universe.hasEntity(secondEntity), "Universe#hasEntity should return false.");
  }

  @Test
  public void testIterateEntities() {
    final Universe universe = Universe.create();

    final Entity firstEntity = universe.createEntity();

    final Iterator<Entity> iteratorRaw = universe.entities();
    assertNotNull(iteratorRaw, "Entity iterator should not be null.");
    assertTrue(iteratorRaw.hasNext(), "Entity iterator should have a next entity.");
    assertNotNull(iteratorRaw.next(), "Entity iterator should have a next entity.");
    assertDoesNotThrow(iteratorRaw::remove, "Entity iterator should not throw an exception.");
    assertNotNull(universe.getEntity(firstEntity.index()), "Entity should exist in the universe.");
    assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertNull(universe.getEntity(firstEntity.index()), "Entity should not exist in the universe.");

    final EntityExample secondEntity = universe.createEntity(EntityExample::new);

    final Iterator<EntityExample> iteratorCustom = universe.entities(EntityExample.class);
    assertNotNull(iteratorCustom, "Entity iterator should not be null.");
    assertTrue(iteratorCustom.hasNext(), "Entity iterator should have a next entity.");
    assertNotNull(iteratorCustom.next(), "Entity iterator should have a next entity.");
    assertDoesNotThrow(iteratorCustom::remove, "Entity iterator should not throw an exception.");
    assertNotNull(universe.getEntity(secondEntity.index()), "Entity should exist in the universe.");
    assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertNull(universe.getEntity(secondEntity.index()), "Entity should not exist in the universe.");
  }

  @Test
  public void testCreateComponent() {
    final Universe universe = Universe.create();
    final Entity entity = universe.createEntity();

    final ComponentType type = assertDoesNotThrow(() -> ComponentType.create(universe, ComponentExample.class), "Component type creation should not throw an exception.");
    final ComponentExample component = assertDoesNotThrow(() -> universe.addComponent(entity, type), "Component addition should not throw an exception.");

    assertNotNull(component, "Component should be created.");
    assertEquals(type, universe.getType(type.index()), "Universe#getType should equal the component type.");
    assertEquals(type, universe.getType(ComponentExample.class), "Universe#getType should equal the component type.");
    assertEquals(component, universe.getComponent(entity, type), "Component#get should equal the new component.");
    assertTrue(universe.hasComponent(entity, type), "Universe#hasComponent should return true.");
  }

  @Test
  public void testRemoveComponent() {
    final Universe universe = Universe.create();
    final Entity entity = universe.createEntity();

    final ComponentType type = ComponentType.create(universe, ComponentExample.class);
    final ComponentExample firstComponent = universe.addComponent(entity, type);

    assertNotNull(firstComponent, "Component should be created.");
    assertEquals(firstComponent, universe.getComponent(entity, type), "Component#get should equal the new component.");
    assertTrue(universe.hasComponent(entity, type), "Universe#hasComponent should return true.");

    assertDoesNotThrow(() -> universe.removeComponent(entity, type), "Component removal should not throw an exception.");
    assertNotNull(universe.getComponent(entity, type), "Component should exist in the universe.");
    assertTrue(universe.hasComponent(entity, type), "Universe#hasComponent should return true.");
    assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertNull(universe.getComponent(entity, type), "Component should not exist in the universe.");
    assertFalse(universe.hasComponent(entity, type), "Universe#hasComponent should return false.");

    final ComponentExample secondComponent = universe.addComponent(entity, type);

    assertDoesNotThrow(() -> universe.clearComponents(entity), "Component clearing should not throw an exception.");
    assertEquals(secondComponent, universe.getComponent(entity, type), "Component#get should equal the new component.");
    assertTrue(universe.hasComponent(entity, type), "Universe#hasComponent should return true.");
    assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertNull(universe.getComponent(entity, type), "Component should not exist in the universe.");
    assertFalse(universe.hasComponent(entity, type), "Universe#hasComponent should return false.");
  }

  @Test
  public void testIterateComponents() {
    final Universe universe = Universe.create();
    final Entity entity = universe.createEntity();

    final ComponentType type = ComponentType.create(universe, ComponentExample.class);
    universe.addComponent(entity, type);

    final Iterator<ComponentType> typeIterator = universe.types();
    assertNotNull(typeIterator, "Component type iterator should not be null.");
    assertTrue(typeIterator.hasNext(), "Component type iterator should have a next component type.");
    assertNotNull(typeIterator.next(), "Component type iterator should have a next component type.");
    assertThrows(RuntimeException.class, typeIterator::next, "Component type iterator should throw an exception.");

    final Iterator<ComponentExample> specificIterator = universe.components(type);
    assertNotNull(specificIterator, "Component iterator should not be null.");
    assertTrue(specificIterator.hasNext(), "Component iterator should have a next component.");
    assertNotNull(specificIterator.next(), "Component iterator should have a next component.");
    assertThrows(RuntimeException.class, specificIterator::remove, "Component iterator should not throw an exception.");

    final Iterator<Object> entityIterator = universe.components(entity);
    assertNotNull(entityIterator, "Component iterator should not be null.");
    assertTrue(entityIterator.hasNext(), "Component iterator should have a next component.");
    assertNotNull(entityIterator.next(), "Component iterator should have a next component.");
    assertThrows(RuntimeException.class, entityIterator::remove, "Component iterator should not throw an exception.");

    final Iterator<Object> iterator = universe.components();
    assertNotNull(iterator, "Component iterator should not be null.");
    assertTrue(iterator.hasNext(), "Component iterator should have a next component.");
    assertNotNull(iterator.next(), "Component iterator should have a next component.");
    assertThrows(RuntimeException.class, iterator::remove, "Component iterator should not throw an exception.");
  }

  @Test
  public void testEntityStash() {
    final Universe universe = Universe.create();

    final Entity firstEntity = universe.createEntity();
    final Entity secondEntity = universe.createEntity();

    final EntityStash stash = assertDoesNotThrow(() -> EntityStash.create(universe), "Entity stash creation should not throw an exception.");
    assertNotNull(stash, "Entity stash should not be null.");

    assertDoesNotThrow(() -> stash.add(firstEntity), "Adding an entity to the stash should not throw an exception.");
    assertTrue(stash.contains(firstEntity), "Stash should contain the entity.");
    assertFalse(stash.contains(secondEntity), "Stash should not contain the entity.");
    assertFalse(stash.remove(secondEntity), "Stash should not remove the entity.");
    assertTrue(stash.remove(firstEntity), "Stash should remove the entity.");

    assertTrue(stash.add(secondEntity), "Stash should add the entity.");
    assertFalse(stash.add(secondEntity), "Stash should not add the entity.");

    final Iterator<Entity> iterator = stash.iterator();
    assertNotNull(iterator, "Entity stash iterator should not be null.");
    assertTrue(iterator.hasNext(), "Entity stash iterator should have a next entity.");
    assertEquals(secondEntity, iterator.next(), "Entity stash iterator should have a next entity.");
    assertDoesNotThrow(iterator::remove, "Removing an entity from the stash should not throw an exception.");

    final Entity thirdEntity = universe.createEntity();
    assertTrue(stash.add(thirdEntity), "Stash should add the entity.");
    assertDoesNotThrow(() -> universe.removeEntity(thirdEntity), "Removing an entity from the universe should not throw an exception.");
    assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertFalse(stash.contains(thirdEntity), "Stash should not contain the entity.");
  }

  @Test
  public void testUniverseTick() {
    final Universe universe = Universe.create();

    universe.addSystem(new SystemExample());
    universe.addSystem(() -> {
      throw new RuntimeException("Example exception.");
    });

    final Universe.Tick firstTick = assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertEquals(0, firstTick.time(), "Tick time should be 0.");
    assertEquals(1, firstTick.errors().size(), "Tick errors should contain 1 error.");

    final Universe.Tick secondTick = assertDoesNotThrow(universe::tick, "Tick should not throw an exception.");
    assertEquals(1, secondTick.time(), "Tick time should be 0.");
    assertEquals(1, secondTick.errors().size(), "Tick errors should contain 1 error.");
  }

  static final class SystemExample implements System {
    @Override
    public void execute() {}
  }

  static final class EntityExample extends AbstractEntity {
    private EntityExample(final @NonNull Universe universe, final @NonNegative int index) {
      super(universe, index);
    }
  }

  @Component(id = "component_example", name = "Component Example")
  static final class ComponentExample {
    // No-op
  }
}
