package agh.ics.oop;

import agh.ics.oop.model.exceptions.IncorrectPositionException;
import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationInputGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Simulation implements Runnable {
    private final List<Animal> animals;
    private final List<List<Integer>> directionSequences; // Sekwencje ruchów dla każdego zwierzaka
    private final WorldMap map;
    private final int plantEnergy;
    private final int animalEnergy;
    private final int minEnergy;
    private final Statistics stats;
    private volatile boolean running = true;
    private final int dailyPlantCount;


    public Simulation(List<Vector2d> startPositions, List<List<Integer>> directionSequences, WorldMap map, int plantEnergy, int animalEnergy, int minEnergy, int dailyPlantCount) {
        this.animals = new ArrayList<>();
        this.directionSequences = directionSequences;
        this.map = map;
        this.plantEnergy=plantEnergy;
        this.animalEnergy=animalEnergy;
        this.minEnergy=minEnergy;
        this.stats = new Statistics();
        this.dailyPlantCount = dailyPlantCount;

        if (startPositions.size() != directionSequences.size()) {
            throw new IllegalArgumentException("Number of valid start positions must match the number of direction sequences.");
        }

        // Sprawdzenie zgodności liczby pozycji startowych i sekwencji ruchów
        if (startPositions.size() != directionSequences.size()) {
            throw new IllegalArgumentException("Mismatch between number of start positions and direction sequences.");
        }

        for (Vector2d position : startPositions) {
            if (!map.isOccupied(position)) {
                Animal animal = new Animal(position, animalEnergy);
                try {
                    map.place(animal);
                    animals.add(animal); // Dodaj zwierzę do listy zwierząt
                    System.out.println(animalEnergy);
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
        running = false; // Zatrzymaj symulację
        System.out.println("Simulation stopping... Running set to false.");
    }





    @Override
    public void run() {
        System.out.println("Simulation started.");

        while (running) { // Główna pętla symulacji
            for (int i = 0; i < animals.size(); i++) {
                Animal animal = animals.get(i);

                if (animal.getEnergy() > 0) {
                    List<Integer> directions = directionSequences.get(i);

                    // Sprawdzenie, czy lista kierunków nie jest pusta
                    if (directions.isEmpty()) {
                        System.err.println("Error: Direction sequence is unexpectedly empty for animal at index " + i);
                        continue;
                    }

                    // Pobranie kierunku z zapętloną sekwencją
                    int step = stats.getDay() % directions.size();
                    int direction = directions.get(step);

                    map.move(animal, direction);

                    // Sprawdź, czy zwierzę umarło po ruchu
                    if (animal.getEnergy() <= 0) {
                        System.out.println("Animal died at position: " + animal.getPosition());
                        map.removeAnimal(animal.getPosition(), animal);
                    }
                }
            }


            // Rozmnażanie zwierząt na każdym polu
            for (Vector2d position : map.getAnimals().keySet()) {
                reproduceAnimalsAt(position);
            }

            // Generowanie nowych roślin
            if (map instanceof EquatorialForest) {
                ((EquatorialForest) map).growPlants(dailyPlantCount, plantEnergy);
            }

            // Zwiększ dzień symulacji
            stats.incrementDay();
            System.out.println("Day " + stats.getDay() + " ended.");

            // Zakończ symulację, jeśli nie ma już zwierząt
            if (animals.stream().allMatch(a -> a.getEnergy() <= 0)) {
                System.out.println("All animals are dead. Stopping simulation.");
                break;
            }

            try {
                Thread.sleep(500); // Opóźnienie między dniami
            } catch (InterruptedException e) {
                System.out.println("Simulation interrupted.");
                Thread.currentThread().interrupt();
                return;
            }
        }

        System.out.println("Simulation ended.");
    }









    public WorldMap getMap() {
        return map;
    }

    private void simulateAnimal(Animal animal) {
        List<Integer> directions = directionSequences.get(animals.indexOf(animal));
        int directionCount = directions.size();

        int step = 0;
        while (running && animal.getEnergy() > 0) {
            int direction = directions.get(step % directionCount);
            map.move(animal, direction);

            // Jeśli zwierzę umiera, kończymy pętlę
            if (animal.getEnergy() <= 0) {
                System.out.println("Animal died at position: " + animal.getPosition());
                break;
            }

            System.out.println("Animal moved to position: " + animal.getPosition());
            step++;

            try {
                Thread.sleep(1000); // Opóźnienie między ruchami
            } catch (InterruptedException e) {
                System.out.println("Animal thread interrupted.");
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Animal thread ended for: " + animal);
    }

    private Animal resolveConflict(List<Animal> candidates) {
        candidates.sort(Comparator
                .comparingInt(Animal::getEnergy)
                .thenComparingInt(Animal::getAge)
                .thenComparingInt(Animal::getChildrenCount)
                .reversed()
        );
        return candidates.get(0);
    }




    private void reproduceAnimalsAt(Vector2d position) {
        List<Animal> animalsAtPosition = map.getAnimals().get(position);
        if (animalsAtPosition.size() < 2) return;

        animalsAtPosition.sort(Comparator.comparingInt(Animal::getEnergy).reversed());
        Animal parent1 = animalsAtPosition.get(0);
        Animal parent2 = animalsAtPosition.get(1);

        if (parent1.getMoves().isEmpty() || parent2.getMoves().isEmpty()) {
            System.err.println("Error: One or both parents have empty genotypes. Skipping reproduction.");
            return;
        }

        if (parent1.getEnergy() >= minEnergy && parent2.getEnergy() >= minEnergy) {
            if (map instanceof EarthMap) {
                EarthMap earthMap = (EarthMap) map;
                Animal child = earthMap.reproduce(parent1, parent2);
                try {
                    map.place(child); // Dodaj dziecko do mapy

                    // Dodaj dziecko do listy `animals`
                    animals.add(child);

                    // Wygeneruj nową sekwencję ruchów dla dziecka
                    List<Integer> childDirections = SimulationInputGenerator.generateRandomMoveSequences(1, 5, 20).get(0);
                    directionSequences.add(childDirections);

                    // Debug: Informacja o nowo narodzonym zwierzęciu
                    System.out.println("New animal born at " + child.getPosition() + " with energy: " + child.getEnergy());
                } catch (IncorrectPositionException e) {
                    System.err.println("Error placing animal: " + e.getMessage());
                }
            } else {
                System.err.println("Reproduction is not supported on this map type.");
            }
        }
    }







}