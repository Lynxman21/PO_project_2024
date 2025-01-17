package agh.ics.oop;

import agh.ics.oop.model.exceptions.IncorrectPositionException;
import agh.ics.oop.model.*;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {
    private final List<Animal> animals;
    private final List<List<MoveDirection>> directionSequences; // Sekwencje ruchów dla każdego zwierzaka
    private final WorldMap map;
    private final int startEnergy;
    private Statistics stats;

    public Simulation(List<Vector2d> startPositions, List<List<MoveDirection>> directionSequences, WorldMap map, int energy) {
        this.animals = new ArrayList<>();
        this.directionSequences = directionSequences;
        this.map = map;
        this.startEnergy = energy;
        this.stats = new Statistics();

        if (startPositions.size() != directionSequences.size()) {
            throw new IllegalArgumentException("Number of valid start positions must match the number of direction sequences.");
        }

        // Sprawdzenie zgodności liczby pozycji startowych i sekwencji ruchów
        if (startPositions.size() != directionSequences.size()) {
            throw new IllegalArgumentException("Mismatch between number of start positions and direction sequences.");
        }

        for (Vector2d position : startPositions) {
            if (!map.isOccupied(position)) {
                Animal animal = new Animal(position,startEnergy);
                try {
                    map.place(animal);
                    animals.add(animal);
                    System.out.println("Animal added at: " + position);
                } catch (IncorrectPositionException e) {
                    System.out.println("Error placing animal at " + position + ": " + e.getMessage());
                }
            } else {
                System.out.println("Position " + position + " is already occupied.");
            }
        }

        // Sprawdzenie zgodności liczby zwierząt i sekwencji ruchów po dodaniu zwierząt
        if (animals.size() != directionSequences.size()) {
            throw new IllegalStateException("Mismatch between number of animals and direction sequences after placement.");
        }

        System.out.println("Number of animals added: " + animals.size());
        System.out.println("Number of direction sequences: " + directionSequences.size());
    }




    @Override
    public void run() {
        List<Thread> animalThreads = new ArrayList<>();

        for (int i = 0; i < animals.size(); i++) {
            int animalIndex = i;
            Thread animalThread = new Thread(() -> simulateAnimal(animalIndex));
            animalThreads.add(animalThread);
            animalThread.start();
            System.out.println("Started thread for Animal " + animalIndex + " at position: " + animals.get(i).getPosition());
            if (i==animals.size()-1) {
                stats.incrementDay();

            }
        }

        // Czekaj na zakończenie wątków (te wątki nigdy się nie kończą)
        for (Thread thread : animalThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public WorldMap getMap() {
        return map;
    }

    private void simulateAnimal(int animalIndex) {
        Animal animal = animals.get(animalIndex);
        List<MoveDirection> directions = directionSequences.get(animalIndex);
        int directionCount = directions.size();

        int step = 0;
        while (true) { // Nieskończona pętla
            MoveDirection direction = directions.get(step % directionCount); // Pobierz ruch w pętli
            map.move(animal, direction);

            if (animal.getEnergy()<=0) {
                map.removeAnimal(animal.getPosition());
            }

            System.out.println("Animal " + animalIndex + " moved: " + direction + " to position " + animal.getPosition());

            step++;

            // Opóźnienie dla symulacji
            try {
                Thread.sleep(1000); // 1 sekunda między ruchami
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}



