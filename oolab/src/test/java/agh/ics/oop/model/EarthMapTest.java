package agh.ics.oop.model;

import agh.ics.oop.model.exceptions.IncorrectPositionException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EarthMapTest {
    @Test
    public void placeTest() throws IncorrectPositionException {
        EarthMap map = new EarthMap(5, 5, 100);
        Animal a1 = new Animal(new Vector2d(2, 2), 100, 3);
        Animal a2 = new Animal(new Vector2d(3, 1), 100, 3);

        map.place(a1);
        map.place(a2);

        assertEquals(a1.getPosition(), new Vector2d(2, 2));
        assertEquals(a2.getPosition(), new Vector2d(3, 1));
    }

    @Test
    public void regularMoveTest() throws IncorrectPositionException {
        EarthMap map = new EarthMap(5, 5, 100);
        Animal animal = new Animal(new Vector2d(2, 2), 100, 3);

        animal.setMoves(new ArrayList<MoveDirection>(List.of(MoveDirection.FORWARD, MoveDirection.RIGHT)));

        map.place(animal);

        for (MoveDirection move : animal.getMoves()) {
            map.move(animal, move);
        }
        assertEquals(new Vector2d(3, 4), animal.getPosition());
        assertEquals(MapDirection.NORTH_EAST, animal.getDirection());
    }

    @Test
    public void abroadMove() throws IncorrectPositionException {
        EarthMap map = new EarthMap(5, 5, 100);
        Animal animal = new Animal(new Vector2d(5, 5), 100, 3);

        map.place(animal);
        map.move(animal, MoveDirection.FORWARD);

        assertEquals(new Vector2d(5, 5), animal.getPosition());
        assertEquals(MapDirection.SOUTH, animal.getDirection());
    }

    @Test
    public void abroadMoveHorizontally() throws IncorrectPositionException {
        EarthMap map = new EarthMap(5, 5, 100);
        Animal animal = new Animal(new Vector2d(5, 3), 100, 3);

        map.place(animal);
        map.move(animal, MoveDirection.RIGHT);
        map.move(animal, MoveDirection.RIGHT);
        map.move(animal, MoveDirection.FORWARD);

        assertEquals(new Vector2d(2, 4), animal.getPosition());
        assertEquals(MapDirection.EAST, animal.getDirection());
    }
}