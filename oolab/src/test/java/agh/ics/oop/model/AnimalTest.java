package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    @Test
    public void isAt()
    {
        Animal animal = new Animal();

        assertTrue(animal.isAt(new Vector2d(2, 2)));
        assertFalse(animal.isAt(new Vector2d(1, 5)));
    }

    @Test
    public void move()
    {
        Animal animal = new Animal();
        RectangularMap worldMap = new RectangularMap(5,5);

        animal.move(MoveDirection.FORWARD, worldMap);

        assertTrue(animal.isAt(new Vector2d(2, 3)));
    }

    @Test
    public void outOfBorder()
    {
        Animal animal = new Animal(new Vector2d(5,5));
        RectangularMap worldMap = new RectangularMap(5,5);

        animal.move(MoveDirection.FORWARD, worldMap);
        assertFalse(animal.isAt(new Vector2d(5,6)));
        assertTrue(animal.isAt(new Vector2d(5,5)));
    }

    @Test
    public void changeDirection()
    {
        Animal animal = new Animal();
        RectangularMap worldMap = new RectangularMap(5,5);

        animal.move(MoveDirection.RIGHT, worldMap);

        assertEquals(animal.getDirection(), MapDirection.EAST);
    }
}