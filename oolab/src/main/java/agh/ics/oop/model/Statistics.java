package agh.ics.oop.model;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.Animal;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Statistics {

    @FXML
    public Label dayDisplay;

    @FXML
    public Label animalCountDisplay;

    @FXML
    public Label plantDisplay;

    @FXML
    public Label emptyCellsDisplay;

    @FXML
    public Label mostCommonGenomDisplay;

    @FXML
    public Label averageEnergyDisplay;

    @FXML
    public Label averageLifeDisplay;

    @FXML
    public Label averageChildrenCountDisplay;

    private int day;
    private int animalCount;
    private int plantCount;
    private int emptyCells;
    private WorldMap map;
    private MoveDirection mostCommonGenom;
    private int averageEnergy;
    private int averageLifeLen;
    private int averageCountOfChildren;
    private int cells;
    private final int additionalPlants;
    private int columns;
    private int rows;
    private Simulation simulation;


    public Statistics(WorldMap map, List<Animal> animals, int animalCount, int plantCount, int columns, int rows, int minEnergy) {
        this.day = 0;
        this.map = map;
        this.animalCount = animalCount;
        this.plantCount = plantCount;
        this.emptyCells = columns * rows - plantCount - animalCount;
        this.averageEnergy = minEnergy;
        this.averageLifeLen = 0;
        this.averageCountOfChildren = 0;
        this.cells = rows * columns;
        this.mostCommonGenom = calculateMostCommonGenom(animals); // Oblicz najpopularniejszy genom
        this.additionalPlants = plantCount;
        this.columns = columns;
        this.rows = rows;
    }

    private MoveDirection calculateMostCommonGenom(List<Animal> animals) {
        Map<MoveDirection, Integer> genomFrequency = new HashMap<>();
        for (Animal animal : animals) {
            MoveDirection genom = animal.getMostCommonGenom();
            genomFrequency.put(genom, genomFrequency.getOrDefault(genom, 0) + 1);
        }

        return genomFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null); // Zwróć null, jeśli lista zwierząt jest pusta
    }

    public int getDay() {
        return day;
    }

    public int getAnimalCount() {
        return animalCount;
    }

    public int getPlantCount() {
        return plantCount;
    }

    public int getEmptyCells() {
        return emptyCells;
    }

    public MoveDirection getMostCommonGenom() {
        return mostCommonGenom;
    }

    public int getAverageEnergy() {
        return averageEnergy;
    }

    public int getAverageLifeLen() {
        return averageLifeLen;
    }

    public int getAverageCountOfChildren() {
        return averageCountOfChildren;
    }

    public void incrementDay() {
        day++;
    }

    public void newEmptyCells() {
        emptyCells = 0;
    }

    public void newAverageEnergy(List<Animal> animals) {
        averageEnergy = (int) animals.stream()
                .mapToInt(Animal::getEnergy)
                .average()
                .orElse(0.0);
    }

    public void newAverageLifeLen(Animal animal) {
        averageLifeLen = (averageLifeLen + animal.getLifeLen()) / 2;
    }

    public void newAverageChildrenCount(Animal animal) {
        averageCountOfChildren = (averageCountOfChildren + animal.getChildrenCount()) / 2;
    }

    public void newMostCommonGenom(List<Animal> animals) {
        this.mostCommonGenom = calculateMostCommonGenom(animals);
    }

    public void displayStats() {
        System.out.println("dayDisplay: " + dayDisplay); // Sprawdzenie, czy pole jest null
        System.out.println("Day: " + day);

        if (dayDisplay != null) dayDisplay.setText(String.valueOf(day));
        if (animalCountDisplay != null) animalCountDisplay.setText(String.valueOf(animalCount));
        if (plantDisplay != null) plantDisplay.setText(String.valueOf(plantCount));
        if (emptyCellsDisplay != null) emptyCellsDisplay.setText(String.valueOf(emptyCells));
        if (mostCommonGenomDisplay != null) mostCommonGenomDisplay.setText(mostCommonGenom != null ? mostCommonGenom.toString() : "None");
        if (averageEnergyDisplay != null) averageEnergyDisplay.setText(String.valueOf(averageEnergy));
        if (averageLifeDisplay != null) averageLifeDisplay.setText(String.valueOf(averageLifeLen));
        if (averageChildrenCountDisplay != null) averageChildrenCountDisplay.setText(String.valueOf(averageCountOfChildren));
    }

    public void updateEmptyFields(EquatorialForest forest) {
        emptyCells = forest.getFreeFieldsCount(); // Pobierz liczbę wolnych pól tylko względem zwierząt
        if (emptyCellsDisplay != null) {
            emptyCellsDisplay.setText(String.valueOf(emptyCells)); // Zaktualizuj wyświetlanie w GUI
        }
    }

    public void updateCounts() {
        this.animalCount = map.getAnimals().values().stream().mapToInt(List::size).sum(); // Suma wszystkich zwierząt
        this.plantCount = map instanceof EquatorialForest ? ((EquatorialForest) map).getPlants().size() : 0; // Liczba roślin (jeśli mapa obsługuje rośliny)
    }

    public void newPlantCount(Map<Vector2d, Plant> plants) {
        Set<Plant> uniquePlants = new HashSet<>(plants.values());
        this.plantCount = uniquePlants.size();
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void saveToCSV(String fileName, List<Animal> allAnimals, Simulation simulation) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Sekcja 1: Nagłówki ogólnych statystyk
            writer.write("Day  ;  Animal Count  ;  Plant Count  ;  Empty Cells  ;  Most Common Genotype  ;  Average Energy  ;  Average Life Length  ;  Average Children Count\n");

            // Dane ogólnych statystyk
            writer.write(day + "  ;  " +
                    animalCount + "  ;  " +
                    plantCount + "  ;  " +
                    emptyCells + "  ;  " +
                    (mostCommonGenom != null ? mostCommonGenom.toString() : "None") + "  ;  " +
                    averageEnergy + "  ;  " +
                    averageLifeLen + "  ;  " +
                    averageCountOfChildren + "\n");

            // Dodanie separatora między sekcjami
            writer.write("\nDetails of Animals:\n");

            // Nagłówki dla danych zwierząt
            writer.write("\nAnimal Status  ;  Position  ;  Energy  ;  Children Count  ;  Life Length  ;  Genotype\n");

            // Dane dla każdego zwierzęcia
            int index = 0; // Indeks zwierzęcia w simulation
            for (Animal animal : allAnimals) {
                boolean isAlive = animal.getEnergy() > 0;
                String status = isAlive ? "Alive" : "Dead";
                AnimalStatistics stats = animal.getStatistics();

                // Pobranie genotypu zwierzęcia
                List<Integer> genotype = simulation.getAnimalGenotype(index);

                writer.write(status + "  ;  " +
                        animal.getPosition() + "  ;  " +
                        animal.getEnergy() + "  ;  " +
                        stats.getChildrenCount() + "  ;  " +
                        stats.getLifeLen() + "  ;  " +
                        genotype.toString() +
                        "\n");

                index++;
            }

            System.out.println("Statistics and animal details saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving statistics to CSV: " + e.getMessage());
        }
    }
}
