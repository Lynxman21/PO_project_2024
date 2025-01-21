package agh.ics.oop;

import agh.ics.oop.model.exceptions.IncorrectPositionException;
import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationInputGenerator;
import agh.ics.oop.presenter.SimulationViewPresenter;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Simulation implements Runnable {
    private final List<Animal> animals;
    private final List<List<Integer>> directionSequences; // Sekwencje ruchów dla każdego zwierzaka
    private final WorldMap map;
    private final int plantEnergy;
    private final int animalEnergy;
    private final int minEnergy;
    private final Statistics statistics;
    private volatile boolean running = true;
    private final int dailyPlantCount;
    private final int minMutations;
    private final int maxMutations;
    private final int genomLength;
    private final int energyForChild;
    private final int time;
    private Animal selectedAnimal;


    public Simulation(List<Vector2d> startPositions, List<List<Integer>> directionSequences, WorldMap map,
                      int plantEnergy, int animalEnergy, int minEnergy, int columns, int rows,
                      int dailyPlantCount, Statistics statistics, int minMutations, int maxMutations, int genomLength,
                      int energyForChild, int time) {
        this.animals = new ArrayList<>();
        this.directionSequences = directionSequences;
        this.map = map;
        this.plantEnergy = plantEnergy;
        this.animalEnergy = animalEnergy;
        this.minEnergy = minEnergy;
        this.statistics = statistics; // Zapisanie referencji do obiektu statystyk
        this.dailyPlantCount = dailyPlantCount;
        this.minMutations = minMutations;
        this.maxMutations = maxMutations;
        this.genomLength = genomLength;
        this.energyForChild = energyForChild;
        this.time = time;


        if (startPositions.size() != directionSequences.size()) {
            throw new IllegalArgumentException("Number of valid start positions must match the number of direction sequences.");
        }

        for (int i = 0; i < startPositions.size(); i++) {
            Vector2d position = startPositions.get(i);
            if (!map.isOccupied(position)) {
                Animal animal = new Animal(position, animalEnergy, genomLength);
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

        System.out.println("Number of animals added: " + animals.size());
        System.out.println("Number of direction sequences: " + directionSequences.size());
    }

    public void stop() {
        running = false; // Zatrzymaj symulację
        System.out.println("Simulation stopping... Running set to false.");
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void run() {
        System.out.println("Simulation started.");

        while (running) {
            synchronized (this) {
                while (paused) { // Sprawdzaj, czy symulacja jest wstrzymana
                    try {
                        wait(); // Czekaj, aż pauza zostanie wyłączona
                    } catch (InterruptedException e) {
                        System.out.println("Simulation interrupted during pause.");
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            for (int i = 0; i < animals.size(); i++) {
                Animal animal = animals.get(i);

                if (animal.getEnergy() > 0) {
                    List<Integer> directions = directionSequences.get(i);

                    if (directions.isEmpty()) {
                        System.err.println("Error: Direction sequence is unexpectedly empty for animal at index " + i);
                        continue;
                    }

                    int step = statistics.getDay() % directionSequences.get(i).size();
                    map.move(animal, directionSequences.get(i).get(step));

                    if (animal.getEnergy() <= 0) {
                        System.out.println("Animal died at position: " + animal.getPosition());
                        map.removeAnimal(animal.getPosition(), animal);
                        statistics.newAverageLifeLen(animal);
                    } else {
                        animal.incrementLifeLen();
                    }
                }
            }

            for (Vector2d position : map.getAnimals().keySet()) {
                reproduceAnimalsAt(position, energyForChild);
            }

            Platform.runLater(() -> {
                if (map instanceof EquatorialForest) {
                    EquatorialForest forest = (EquatorialForest) map;
                    forest.growPlants(dailyPlantCount, plantEnergy);
                    statistics.updateEmptyFields(forest);
                }
            });

            statistics.newEmptyCells();
            statistics.newAverageEnergy(animals);
            statistics.incrementDay();

            // Aktualizacja statystyk
            statistics.updateCounts();

            if (map instanceof EquatorialForest) {
                statistics.newPlantCount(((EquatorialForest) map).getPlants());
            }

//            Platform.runLater(() -> {
//                if (selectedAnimal != null) {
//                    handleAnimalClick(selectedAnimal); // Odśwież dane wybranego zwierzęcia
//                }
//            });

            Platform.runLater(statistics::displayStats); // Wywołanie metody w wątku GUI

            if (animals.stream().allMatch(a -> a.getEnergy() <= 0)) {
                System.out.println("All animals are dead. Stopping simulation.");
                break;
            }

            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                System.out.println("Simulation interrupted.");
                Thread.currentThread().interrupt();
                return;
            }
        }

        System.out.println("Simulation ended.");
    }


    public int getAnimalIndex(Animal animal) {
        return animals.indexOf(animal);
    }

    public List<Integer> getAnimalGenotype(int index) {
        if (index >= 0 && index < directionSequences.size()) {
            return directionSequences.get(index);
        }
        return Collections.emptyList();
    }


    public WorldMap getMap() {
        return map;
    }

    private void reproduceAnimalsAt(Vector2d position, int energyForChild) {
        List<Animal> animalsAtPosition = map.getAnimals().get(position);
        if (animalsAtPosition.size() < 2) return;

        animalsAtPosition.sort(Comparator.comparingInt(Animal::getEnergy).reversed());
        Animal parent1 = animalsAtPosition.get(0);
        Animal parent2 = animalsAtPosition.get(1);

        if (parent1.getMoves().isEmpty() || parent2.getMoves().isEmpty()) {
            System.err.println("Error: One or both parents have empty genotypes. Skipping reproduction.");
            return;
        }

        if (parent1.getEnergy() >= energyForChild && parent2.getEnergy() >= energyForChild) {
            if (map instanceof EarthMap) {
                EarthMap earthMap = (EarthMap) map;
                Animal child = earthMap.reproduce(parent1, parent2, statistics, minMutations, maxMutations, genomLength);
                try {
                    map.place(child); // Dodaj dziecko do mapy

                    // Dodaj dziecko do listy `animals`
                    animals.add(child);

                    // Wygeneruj nową sekwencję ruchów dla dziecka
                    List<Integer> childDirections = SimulationInputGenerator.generateRandomMoveSequences(1, genomLength).get(0);
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

    private volatile boolean paused = false;

    public synchronized void pause() {
        paused = true;
        System.out.println("Simulation paused.");
    }

    public synchronized void resume() {
        paused = false;
        notify(); // Powiadom wątek, aby kontynuował
        System.out.println("Simulation resumed.");
    }

}