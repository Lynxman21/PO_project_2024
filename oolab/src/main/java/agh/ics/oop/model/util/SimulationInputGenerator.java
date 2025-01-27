package agh.ics.oop.model.util;

import java.util.*;

import agh.ics.oop.model.*;

public class SimulationInputGenerator {

    public static List<List<Integer>> generateRandomMoveSequences(int numberOfAnimals, int len) {
        List<List<Integer>> sequences = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < numberOfAnimals; i++) {
            if (len <= 0) { // biorąc pod uwagę, że wartość len się nie zmienia, to czy jest sens sprawdzać ten warunek w pętli?
                throw new IllegalStateException("Generated sequence length is invalid: " + len);
            }

            List<Integer> sequence = new ArrayList<>();
            for (int j = 0; j < len; j++) {
                sequence.add(random.nextInt(8)); // Losowanie kierunku (0-7)
            }
            sequences.add(sequence);
        }
        return sequences;
    }
}
