package agh.ics.oop;

import agh.ics.oop.model.exceptions.IncorrectPositionException;
import agh.ics.oop.model.*;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {
    private final List<Animal> animals;
    private final List<List<MoveDirection>> directionSequences; // Sekwencje ruchów dla każdego zwierzaka
    private final WorldMap map;
<<<<<<< Updated upstream
=======
    private final int plantEnergy;
    private final int animalEnergy;
    private final int minEnergy;
    private final Statistics stats;
    private volatile boolean running = true;

>>>>>>> Stashed changes

    public Simulation(List<Vector2d> startPositions, List<List<MoveDirection>> directionSequences, WorldMap map) {
        this.animals = new ArrayList<>();
        this.directionSequences = directionSequences;
        this.map = map;

        if (startPositions.size() != directionSequences.size()) {
            throw new IllegalArgumentException("Number of valid start positions must match the number of direction sequences.");
        }

        // Sprawdzenie zgodności liczby pozycji startowych i sekwencji ruchów
        if (startPositions.size() != directionSequences.size()) {
            throw new IllegalArgumentException("Mismatch between number of start positions and direction sequences.");
        }

        for (Vector2d position : startPositions) {
            if (!map.isOccupied(position)) {
                Animal animal = new Animal(position);
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
//        if (animals.size() != directionSequences.size()) {
//            throw new IllegalStateException("Mismatch between number of animals and direction sequences after placement.");
//        }

        System.out.println("Number of animals added: " + animals.size());
        System.out.println("Number of direction sequences: " + directionSequences.size());
    }

    public void stop() {
        running = false; // Ustaw flagę kontrolną na false
        System.out.println("Stopping simulation...");
//        Thread.currentThread().interrupt(); // Przerwij bieżący wątek
    }




    @Override
    public void run() {
        List<Thread> animalThreads = new ArrayList<>();
<<<<<<< Updated upstream

        for (int i = 0; i < animals.size(); i++) {
            int animalIndex = i;
            Thread animalThread = new Thread(() -> simulateAnimal(animalIndex));
            animalThreads.add(animalThread);
            animalThread.start();
            System.out.println("Started thread for Animal " + animalIndex + " at position: " + animals.get(i).getPosition());
        }
=======
        while (running) { // Pętla działa, dopóki running == true
            for (int i = 0; i < animals.size(); i++) {
                int animalIndex = i;
                Thread animalThread = new Thread(() -> simulateAnimal(animalIndex));
                animalThreads.add(animalThread);
                animalThread.start();
                System.out.println("Started thread for Animal " + animalIndex + " at position: " + animals.get(i).getPosition());
                if (i == animals.size() - 1) {
                    stats.incrementDay(); // Zwiększ dzień po zakończeniu tury
                }
            }
>>>>>>> Stashed changes

            // Czekaj na zakończenie wątków zwierząt
            for (Thread thread : animalThreads) {
                try {
                    thread.join(); // Oczekiwanie na zakończenie wątku
                } catch (InterruptedException e) {
                    System.out.println("Simulation thread interrupted.");
                    Thread.currentThread().interrupt(); // Zatrzymanie wątku
                    return; // Kończymy symulację
                }
            }

            // Po zakończeniu jednej tury sprawdzamy flagę running
            if (!running) {
                System.out.println("Stopping simulation...");
                break;
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
        while (running) { // Pętla działa, dopóki running == true
            MoveDirection direction = directions.get(step % directionCount); // Pobierz ruch w pętli
            map.move(animal, direction);

<<<<<<< Updated upstream
=======
            if (animal.getEnergy() <= 0) {
                map.removeAnimal(animal.getPosition(), animal);
                directions.remove(animalIndex);
                break; // Zwierzę umiera, kończymy pętlę
            }

>>>>>>> Stashed changes
            System.out.println("Animal " + animalIndex + " moved: " + direction + " to position " + animal.getPosition());

            step++;

            // Opóźnienie dla symulacji
            try {
                Thread.sleep(1000); // 1 sekunda między ruchami
            } catch (InterruptedException e) {
                System.out.println("Animal thread interrupted.");
                Thread.currentThread().interrupt(); // Zatrzymanie wątku
                break;
            }
        }
    }


}



