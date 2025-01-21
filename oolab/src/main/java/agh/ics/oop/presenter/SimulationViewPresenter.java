package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.PositionGenerator;
import agh.ics.oop.model.util.SimulationInputGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;

public class SimulationViewPresenter implements MapChangeListener {
    private WorldMap map;

    @FXML
    private Button pauseButton;

    @FXML
    private Label infoLabel;

    @FXML
    private Label dayDisplay;

    @FXML
    private Label animalCountDisplay;

    @FXML
    private Label plantDisplay;

    @FXML
    private Label emptyCellsDisplay;

    @FXML
    private Label mostCommonGenomDisplay;

    @FXML
    private Label averageEnergyDisplay;

    @FXML
    private Label averageLifeDisplay;

    @FXML
    private Label averageChildrenCountDisplay;

    @FXML
    private TextField moveList;

    @FXML
    private GridPane mapGrid;

    @FXML
    private Label selectedAnimalPosition;

    @FXML
    private Label selectedAnimalEnergy;

    @FXML
    private Label selectedAnimalChildren;

    @FXML
    private Label selectedAnimalLifeLength;

    @FXML
    private Label selectedAnimalGenotype;



    private int widthCeil = 50;
    private int heightCeil = 50;
    private int mapWidth;
    private int mapHeight;
    private Boundary boundary;
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;
    private Simulation simulation;
    private Thread simulationThread;
    private Statistics statistics;
    private int minEnergy;


    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) mapGrid.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                System.out.println("Close request received.");

                if (simulation != null) {
                    simulation.stop();
                    if (statistics != null) {
                        // Generowanie nazwy pliku z dynamiczną datą i czasem
                        String fileName = "statistics_" +
                                java.time.LocalDateTime.now().format(
                                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                                ) + ".csv";
                        statistics.saveToCSV(fileName); // Zapis danych do pliku
                    }
                    if (simulationThread != null && simulationThread.isAlive()) {
                        simulationThread.interrupt();
                        System.out.println("Simulation thread interrupted.");
                    }
                } else {
                    System.out.println("Simulation or thread was null.");
                }
            });
        });

        if (statistics != null) {
            bindStatistics(statistics); // Powiąż statystyki z elementami GUI
        }
    }




    public void bindStatistics(Statistics statistics) {
        statistics.dayDisplay = dayDisplay;
        statistics.animalCountDisplay = animalCountDisplay;
        statistics.plantDisplay = plantDisplay;
        statistics.emptyCellsDisplay = emptyCellsDisplay;
        statistics.mostCommonGenomDisplay = mostCommonGenomDisplay;
        statistics.averageEnergyDisplay = averageEnergyDisplay;
        statistics.averageLifeDisplay = averageLifeDisplay;
        statistics.averageChildrenCountDisplay = averageChildrenCountDisplay;
    }


    public void setWorldMap(WorldMap map) {
        this.map = map;
    }

    private void clearGrid() {
        mapGrid.getChildren().clear(); // Usuń wszystkie dzieci
        mapGrid.getColumnConstraints().clear(); // Usuń wszystkie kolumny
        mapGrid.getRowConstraints().clear(); // Usuń wszystkie wiersze
    }

    private void getCurrentParameters() {
        boundary = map.getCurrentBounds();
        mapWidth = boundary.upperRight().getX() - boundary.lowerLeft().getX() + 1;
        mapHeight = boundary.upperRight().getY() - boundary.lowerLeft().getY() + 1;
        xMin = boundary.lowerLeft().getX();
        xMax = boundary.upperRight().getX();
        yMin = boundary.lowerLeft().getY();
        yMax = boundary.upperRight().getY();
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            infoLabel.setText(message); // Aktualizacja wiadomości w GUI
            updateDynamicElements(); // Regularna aktualizacja widoku

            if (worldMap instanceof EquatorialForest) {
                EquatorialForest forest = (EquatorialForest) worldMap;
                int freeFields = forest.getFreeFieldsCount();
                if (freeFields == 0) {
                    infoLabel.setText("No more free fields for plants.");
                }
            }
        });
    }



    public void initializeMap() {
        clearGrid(); // Usuń istniejącą zawartość siatki

        getCurrentParameters(); // Pobierz parametry mapy (wymiary, zakresy)

        // Wyraźnie ustaw dokładnie mapWidth kolumn i mapHeight wierszy
        for (int i = 0; i < mapWidth; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(800.0 / mapWidth); // Stała szerokość kolumny
            column.setMinWidth(800.0 / mapWidth); // Minimalna szerokość
            column.setMaxWidth(800.0 / mapWidth); // Maksymalna szerokość
            mapGrid.getColumnConstraints().add(column);
        }
        for (int i = 0; i < mapHeight; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(800.0 / mapHeight); // Stała wysokość wiersza
            row.setMinHeight(800.0 / mapHeight); // Minimalna wysokość
            row.setMaxHeight(800.0 / mapHeight); // Maksymalna wysokość
            mapGrid.getRowConstraints().add(row);
        }

        // Wypełnienie siatki żółtym tłem
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                Rectangle backgroundRectangle = new Rectangle(800.0 / mapWidth, 800.0 / mapHeight);
                backgroundRectangle.setFill(javafx.scene.paint.Color.LIGHTYELLOW); // Tło żółte
                mapGrid.add(backgroundRectangle, x, y);
            }
        }
    }

    public void updateDynamicElements() {
        mapGrid.getChildren().removeIf(node -> node instanceof Rectangle && !((Rectangle) node).getFill().equals(javafx.scene.paint.Color.LIGHTYELLOW));

        // Dodaj rośliny
        if (map instanceof EquatorialForest) {
            Map<Vector2d, Plant> plantsCopy;
            synchronized (map) { // Synchronizuj dostęp, jeśli inna część programu modyfikuje rośliny
                plantsCopy = new HashMap<>(((EquatorialForest) map).getPlants());
            }

            for (Map.Entry<Vector2d, Plant> entry : plantsCopy.entrySet()) {
                Vector2d position = entry.getKey();
                Plant plant = entry.getValue();

                if (plant != null) {
                    Rectangle plantRectangle = new Rectangle(800.0 / mapWidth - 2, 800.0 / mapHeight - 2); // Margines 2px
                    plantRectangle.setFill(plant.isLarge() ? javafx.scene.paint.Color.DARKGREEN : javafx.scene.paint.Color.LIGHTGREEN);

                    int gridX = position.getX() - xMin;
                    int gridY = yMax - position.getY();

                    if (gridX >= 0 && gridY >= 0) {
                        mapGrid.add(plantRectangle, gridX, gridY);
                    }
                }
            }
        }

        // Dodaj zwierzęta
        if (map instanceof AbstractWorldMap) {
            Map<Vector2d, List<Animal>> animalsCopy;
            synchronized (map) { // Synchronizuj dostęp do zwierząt
                animalsCopy = new HashMap<>(((AbstractWorldMap) map).getAnimals());
            }

            for (Map.Entry<Vector2d, List<Animal>> entry : animalsCopy.entrySet()) {
                Vector2d position = entry.getKey();
                List<Animal> animalsAtPosition = entry.getValue();

                if (animalsAtPosition != null && !animalsAtPosition.isEmpty()) {
                    for (Animal animal : animalsAtPosition) {
                        Rectangle animalRectangle = new Rectangle(800.0 / mapWidth - 2, 800.0 / mapHeight - 2);
                        animalRectangle.setFill(getColorForEnergy(animal.getEnergy()));

                        int gridX = position.getX() - xMin;
                        int gridY = yMax - position.getY();

                        if (gridX >= 0 && gridY >= 0) {
                            mapGrid.add(animalRectangle, gridX, gridY);

                            // Dodanie obsługi kliknięcia
                            animalRectangle.setOnMouseClicked(event -> handleAnimalClick(animal));
                        }
                    }
                }
            }
        }

        // Aktualizacja szczegółów wybranego zwierzaka
        updateSelectedAnimalDetails();
    }






    // Pomocnicza metoda do obliczenia koloru
    private javafx.scene.paint.Color getColorForEnergy(int energy) {
        if (energy < minEnergy) {
            return javafx.scene.paint.Color.BURLYWOOD; // Bardzo jasny brąz
        } else if (energy >= minEnergy && energy < 3 * minEnergy) {
            return javafx.scene.paint.Color.SADDLEBROWN; // Średni brąz
        } else {
            return javafx.scene.paint.Color.rgb(70, 30, 0); // Bardzo ciemny brąz (ręcznie zdefiniowany)
        }
    }





    public void initializeSimulation(
            int mapWidth, int mapHeight, int numberOfAnimals, int numberOfPlants,
            int plantEnergy, int animalEnergy, int minEnergy, int plantPerDay,int minMutations, int maxMutations,
            int genomLength, int energyForChild, int time
    ) {
        this.minEnergy = minEnergy; // Przypisanie minEnergy w SimulationViewPresenter

        PositionGenerator positionGenerator = new PositionGenerator(mapWidth, mapHeight);
        List<Vector2d> startPositions = new ArrayList<>();

        // Generowanie unikalnych pozycji
        for (int i = 0; i < numberOfAnimals; i++) {
            Vector2d position;
            do {
                position = positionGenerator.generateUniquePositions(1).get(0);
            } while (startPositions.contains(position)); // Sprawdzanie unikalności
            startPositions.add(position);
        }

        // Generowanie sekwencji ruchów
        List<List<Integer>> directionSequences = SimulationInputGenerator.generateRandomMoveSequences(numberOfAnimals, genomLength);
        System.out.println(directionSequences);

        AbstractWorldMap map = new EarthMap(mapWidth, mapHeight, minEnergy);

        if (map instanceof EquatorialForest) {
            ((EquatorialForest) map).growPlants(numberOfPlants, plantEnergy);
        }

        map.addObserver(this);
        this.setWorldMap(map);

        this.statistics = new Statistics(map, new ArrayList<>(), 0, 0, mapWidth, mapHeight, minEnergy);
        bindStatistics(statistics);

        Platform.runLater(this::initializeMap);

        simulation = new Simulation(
                startPositions, directionSequences, map, plantEnergy, animalEnergy,
                minEnergy, mapWidth, mapHeight, plantPerDay, statistics, minMutations,maxMutations,genomLength,
                energyForChild, time
        );
        simulationThread = new Thread(simulation);
        simulationThread.start();
        System.out.println("Simulation initialized and thread started.");
    }

    private boolean isPaused = false;

    @FXML
    private void handlePauseButtonClick() {
        isPaused = !isPaused; // Przełącz między pauzą a wznowieniem
        if (simulation != null) {
            if (isPaused) {
                simulation.pause(); // Wstrzymaj symulację
                pauseButton.setText("Resume");
            } else {
                simulation.resume(); // Wznów symulację
                pauseButton.setText("Pause");
            }
        }
    }
    private Animal selectedAnimal; // Pole do przechowywania wybranego zwierzaka

    private void handleAnimalClick(Animal animal) {
        selectedAnimal = animal;

        // Znajdź indeks wybranego zwierzaka w directionSequences
        if (simulation != null) {
            selectedAnimalIndex = simulation.getAnimalIndex(animal);
        }

        updateSelectedAnimalDetails();
    }



    private int selectedAnimalIndex = -1; // Indeks wybranego zwierzaka w directionSequences

    private void clearSelectedAnimalDetails() {
        selectedAnimal = null;
        selectedAnimalIndex = -1;

        selectedAnimalPosition.setText("-");
        selectedAnimalEnergy.setText("-");
        selectedAnimalChildren.setText("-");
        selectedAnimalLifeLength.setText("-");
        selectedAnimalGenotype.setText("-");
    }

    private void updateSelectedAnimalDetails() {
        if (selectedAnimal != null) {
            // Sprawdź, czy zwierzak nadal istnieje
            if (!map.getAnimals().containsKey(selectedAnimal.getPosition())) {
                clearSelectedAnimalDetails(); // Wyczyść, jeśli zwierzak zniknął
                return;
            }

            AnimalStatistics stats = selectedAnimal.getStatistics();

            selectedAnimalPosition.setText(selectedAnimal.getPosition().toString());
            selectedAnimalEnergy.setText(String.valueOf(selectedAnimal.getEnergy())); // Dynamiczna energia
            selectedAnimalChildren.setText(String.valueOf(stats.getChildrenCount())); // Dynamiczna liczba dzieci
            selectedAnimalLifeLength.setText(String.valueOf(selectedAnimal.getLifeLen())); // Dynamiczna długość życia

            // Genotyp zwierzaka
            StringBuilder genotypeString = new StringBuilder();
            for (MoveDirection move : stats.getMoves()) {
                genotypeString.append(move.ordinal()).append(" ");
            }
            selectedAnimalGenotype.setText(genotypeString.toString().trim());
        }
    }



}