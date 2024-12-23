package agh.ics.oop;

import agh.ics.oop.model.MoveDirection;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OptionsParserTest {
    @Test
    public void parseEmptyArray() {
        List<String> input = new ArrayList<>();
        assertEquals(0, OptionsParser.parse(input).size());
    }

    @Test
    public void parseArrayWithAllCorrectElement() {
        List<String> input = new ArrayList<>(List.of("f","f","b","l","r","b"));
        List<MoveDirection> expected = new ArrayList<>(Arrays.asList(MoveDirection.FORWARD,MoveDirection.FORWARD,MoveDirection.BACKWARD,MoveDirection.LEFT,MoveDirection.RIGHT,MoveDirection.BACKWARD));
        assertEquals(expected,OptionsParser.parse(input));
    }

    @Test
    public void parseArrayWithAllWrongValues() {
        List<String> input = new ArrayList<>(List.of("a","w","Hello"));
        assertThrows(IllegalArgumentException.class, () -> OptionsParser.parse(input));
    }

    @Test
    public void parseArrayWithMixedElements() {
        List<String> input = new ArrayList<>(List.of("a", "f", "w", "b", "Hello"));
        List<MoveDirection> expected = new ArrayList<>(Arrays.asList(MoveDirection.FORWARD, MoveDirection.BACKWARD));
        try {
            assertEquals(expected, OptionsParser.parse(input));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}