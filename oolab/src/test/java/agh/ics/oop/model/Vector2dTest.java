package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {
    @Test
    public void testEquals() {
        Vector2d vector1 = new Vector2d(1, 2);
        Vector2d vector2 = new Vector2d(1, 2);

        assertEquals(vector1, vector2);
    }

    @Test
    public void testToString() {
        Vector2d vector = new Vector2d(10, 20);

        assertEquals("(10,20)", vector.toString());
    }

    @Test
    public void precedes() {
        Vector2d vector1 = new Vector2d(1, 3);
        Vector2d vector2 = new Vector2d(2, 4);

        assertTrue(vector1.precedes(vector2));
        assertFalse(vector2.precedes(vector1));
    }

    @Test
    public void follows() {
        Vector2d vector1 = new Vector2d(1, 3);
        Vector2d vector2 = new Vector2d(2, 4);

        assertFalse(vector1.follows(vector2));
    }

    @Test
    public void upperRight() {
        Vector2d vector1 = new Vector2d(1, 1);
        Vector2d vector2 = new Vector2d(2, 1);

        assertEquals(new Vector2d(2, 1), vector1.upperRight(vector2));
    }

    @Test
    public void lowerLeft() {
        Vector2d vector1 = new Vector2d(1, 1);
        Vector2d vector2 = new Vector2d(2, 1);

        assertEquals(new Vector2d(1, 1), vector1.lowerLeft(vector2));
    }

    @Test
    public void add() {
        Vector2d vector1 = new Vector2d(1, 1);
        Vector2d vector2 = new Vector2d(2, 2);

        assertEquals(new Vector2d(3, 3), vector1.add(vector2));
    }

    @Test
    public void subtract() {
        Vector2d vector1 = new Vector2d(3, 4);
        Vector2d vector2 = new Vector2d(2, 2);

        assertEquals(new Vector2d(1, 2), vector1.subtract(vector2));
    }

    @Test
    public void opposite() {
        Vector2d vector = new Vector2d(10, 3);

        assertEquals(new Vector2d(-10, -3), vector.opposite());
    }
}
