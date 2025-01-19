package agh.ics.oop.model.util;

import java.util.*;
import agh.ics.oop.model.*;

public class SimulationInputGenerator {

    private static final Random random = new Random();

    public static List<List<Integer>> generateRandomMoveSequences(int numberOfAnimals, int minLength, int maxLength) {
        List<List<Integer>> sequences = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < numberOfAnimals; i++) {
            int sequenceLength = random.nextInt(maxLength - minLength + 1) + minLength;
            if (sequenceLength <= 0) {
                throw new IllegalStateException("Generated sequence length is invalid: " + sequenceLength);
            }

            List<Integer> sequence = new ArrayList<>();
            for (int j = 0; j < sequenceLength; j++) {
                sequence.add(random.nextInt(8)); // Losowanie kierunku (0-7)
            }
            sequences.add(sequence);
        }

        return sequences;
    }


//    private static MoveDirection randomMoveDirection() {
//        MoveDirection[] directions = MoveDirection.values();
//        return directions[random.nextInt(directions.length)];
//    }
}
