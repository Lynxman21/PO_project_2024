package agh.ics.oop.model.util;

import java.util.*;
import agh.ics.oop.model.*;

public class SimulationInputGenerator {

    private static final Random random = new Random();

    public static List<List<MoveDirection>> generateRandomMoveSequences(int numberOfAnimals, int minLength, int maxLength) {
        List<List<MoveDirection>> sequences = new ArrayList<>();

        for (int i = 0; i < numberOfAnimals; i++) {
            int sequenceLength = random.nextInt(maxLength - minLength + 1) + minLength; // Losowa długość w zakresie [minLength, maxLength]
            List<MoveDirection> sequence = new ArrayList<>();

            for (int j = 0; j < sequenceLength; j++) {
                sequence.add(randomMoveDirection());
            }

            sequences.add(sequence);
        }

        return sequences;
    }

    private static MoveDirection randomMoveDirection() {
        MoveDirection[] directions = MoveDirection.values();
        return directions[random.nextInt(directions.length)];
    }
}
