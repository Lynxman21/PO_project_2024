package agh.ics.oop.model;

import agh.ics.oop.model.exceptions.IncorrectPositionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrassFieldTest {
    private final Vector2d basicStart = new Vector2d(2,1);

    @Test
    public void isWorking() {
        GrassField map = new GrassField(5);
        Animal animal = new Animal(basicStart);
        try {
            map.place(animal);
        } catch (IncorrectPositionException e) {
            fail(e.getMessage());
        }
        map.move(animal,MoveDirection.FORWARD);
        assertEquals(new Vector2d(2,2),animal.getPosition());
    }

    @Test
    public void moveToOccupiedPositionByAnimal() {
        GrassField map = new GrassField(5);
        Animal animal1 = new Animal();
        Animal animal2 = new Animal(basicStart);
        try {
            map.place(animal1);
            map.place(animal2);
        } catch (IncorrectPositionException e) {
            fail(e.getMessage());
        }
        map.move(animal2,MoveDirection.FORWARD);
        assertEquals(basicStart,animal2.getPosition());
    }

    @Test
    public void CanMoveTo() {
        GrassField map = new GrassField(5);
        Animal animal = new Animal(basicStart);
        try {
            map.place(animal);
        } catch (IncorrectPositionException e) {
            fail(e.getMessage());
        }
        assertTrue(map.canMoveTo(new Vector2d(2,2)));
        assertFalse(map.canMoveTo(basicStart));
    }

    @Test
    public void placeAndIsOccupied() {
        GrassField map = new GrassField(5);
        Animal animal1 = new Animal();
        Animal animal2 = new Animal();
        Animal animal3 = new Animal(basicStart);
        try {
            map.place(animal1);
        } catch (IncorrectPositionException e) {
            fail(e.getMessage());
        }
        assertThrows(IncorrectPositionException.class, () -> {
            map.place(animal2);
        });

        assertDoesNotThrow(() -> {
            map.place(animal3);
        });
    }

    @Test
    public void objectAt() {
        GrassField map = new GrassField(5);
        Animal animal = new Animal(basicStart);
        try {
            map.place(animal);
        } catch (IncorrectPositionException e) {
            fail(e.getMessage());
        }

        assertEquals(animal,map.objectAt(basicStart));
    }

    @Test
    public void getElements() {
        GrassField map = new GrassField(5);
        Animal animal = new Animal(basicStart);
        try {
            map.place(animal);
        } catch (IncorrectPositionException e) {
            fail(e.getMessage());
        }
        assertEquals(6,map.getElements().size());
    }
}