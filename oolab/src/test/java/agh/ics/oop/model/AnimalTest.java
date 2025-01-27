package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    @Test
    public void isAt() {
        Animal animal = new Animal();

        assertTrue(animal.isAt(new Vector2d(2, 2)));
        assertFalse(animal.isAt(new Vector2d(1, 5)));
    }

    @Test
    public void moveForward() {
        Animal animal = new Animal();
        EarthMap map = new EarthMap(5, 5, 20);


        animal.move(0, map, 5, 5);

        assertTrue(animal.isAt(new Vector2d(2, 3)));
    }

    @Test
    public void outOfBorder() {
        Animal animal = new Animal(new Vector2d(3, 5), 100, 10);
        EarthMap worldMap = new EarthMap(5, 5, 20);

        animal.move(0, worldMap, 5, 5);
        assertFalse(animal.isAt(new Vector2d(3, 6)));
        assertTrue(animal.getPosition().equals(new Vector2d(3, 4)));
    }

    @Test
    public void changeDirection() {
        Animal animal = new Animal();
        EarthMap worldMap = new EarthMap(5, 5, 20);

        animal.move(2, worldMap, 5, 5);

        assertEquals(animal.getDirection(), MapDirection.EAST);
        assertEquals(animal.getPosition(), new Vector2d(3, 2));
    }
}