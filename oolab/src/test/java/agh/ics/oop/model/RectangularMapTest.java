//package agh.ics.oop.model;
//
//import agh.ics.oop.model.exceptions.IncorrectPositionException;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class RectangularMapTest {
//    private static final Vector2d VECTOR = new Vector2d(2,3);
//    private static final Vector2d DEFAULT_VECTOR = new Vector2d(2,2);
//
//    @Test
//    public void IsMapWorking() {
//        RectangularMap map = new RectangularMap(9,6);
//        Animal animal = new Animal();
//        try {
//            map.place(animal);
//        } catch (IncorrectPositionException e) {
//            fail(e.getMessage());
//        }
//        map.move(animal,MoveDirection.FORWARD);
//        assertEquals(VECTOR,animal.getPosition());
//    }
//
//    @Test
//    public void moveToOccupiedPosition() {
//        RectangularMap map = new RectangularMap(9,6);
//        Animal animal1 = new Animal();
//        Animal animal2 = new Animal(VECTOR);
//        try {
//            map.place(animal1);
//            map.place(animal2);
//        } catch (IncorrectPositionException e) {
//            fail(e.getMessage());
//        }
//        map.move(animal1,MoveDirection.FORWARD);
//        assertEquals(animal1.getPosition(),DEFAULT_VECTOR);
//    }
//
//    @Test
//    public void moveToAbroad() {
//        Vector2d start = new Vector2d(0,0);
//        RectangularMap map = new RectangularMap(9,6);
//        Animal animal = new Animal(start);
//        try {
//            map.place(animal);
//        } catch (IncorrectPositionException e) {
//            fail(e.getMessage());
//        }
//        map.move(animal,MoveDirection.LEFT);
//        map.move(animal,MoveDirection.FORWARD);
//        assertEquals(animal.getPosition(),start);
//    }
//
//    @Test
//    public void CanMoveTo() {
//        RectangularMap map = new RectangularMap(9,6);
//        Animal animal = new Animal();
//        try {
//            map.place(animal);
//        } catch (IncorrectPositionException e) {
//            fail(e.getMessage());
//        }
//        assertTrue(map.canMoveTo(VECTOR));
//        assertFalse(map.canMoveTo(DEFAULT_VECTOR));
//    }
//
//    @Test
//    public void placeAndIsOccupied() {
//        Vector2d newPosition = new Vector2d(5,3);
//        RectangularMap map = new RectangularMap(9,6);
//        Animal animal1 = new Animal();
//        Animal animal2 = new Animal();
//        Animal animal3 = new Animal(newPosition);
//
//        try {
//            map.place(animal1);
//        } catch (IncorrectPositionException e) {
//            fail(e.getMessage());
//        }
//        assertThrows(IncorrectPositionException.class, () -> {
//            map.place(animal2);
//        });
//
//        assertDoesNotThrow(() -> {
//            map.place(animal3);
//        });
//    }
//
//    @Test
//    public void isOccupied() {
//        RectangularMap map = new RectangularMap(9,6);
//        Animal animal = new Animal();
//        try {
//            map.place(animal);
//        } catch (IncorrectPositionException e) {
//            fail(e.getMessage());
//        }
//
//        assertTrue(map.isOccupied(DEFAULT_VECTOR));
//        assertFalse(map.isOccupied(VECTOR));
//        assertFalse(map.isOccupied(new Vector2d(15,15)));
//        assertFalse(map.isOccupied(new Vector2d(-1,-1)));
//    }
//
//    @Test
//    public void objectAt() {
//        RectangularMap map = new RectangularMap(9,6);
//        Animal animal = new Animal();
//        try {
//            map.place(animal);
//        } catch (IncorrectPositionException e) {
//            fail(e.getMessage());
//        }
//
//        assertEquals(animal,map.objectAt(DEFAULT_VECTOR));
//    }
//
//    @Test
//    public void getElements() {
//        RectangularMap map = new RectangularMap(9,6);
//        Animal animal = new Animal();
//        try {
//            map.place(animal);
//        } catch (IncorrectPositionException e) {
//            fail(e.getMessage());
//        }
//        assertEquals(1,map.getElements().size());
//    }
//}