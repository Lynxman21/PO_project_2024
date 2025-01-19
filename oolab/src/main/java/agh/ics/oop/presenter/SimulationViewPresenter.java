package agh.ics.oop.presenter;

import agh.ics.oop.OptionsParser;
import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationEngine;
import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.PositionGenerator;
import agh.ics.oop.model.util.SimulationInputGenerator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static agh.ics.oop.OptionsParser.parse;

public class SimulationViewPresenter implements MapChangeListener {
    private WorldMap map;

    @FXML
    private Label infoLabel;

    @FXML
    private TextField moveList;

    @FXML
    private GridPane mapGrid;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) mapGrid.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (simulation != null) {
                    simulation.stop(); // Zatrzymaj symulację
                    if (simulationThread != null && simulationThread.isAlive()) {
                        simulationThread.interrupt(); // Przerwij wątek symulacji
                    }
                }
            });
        });
    }


    private int widthCeil = 50;
    private int heightCeil = 50;

    private int mapWidth;
    private int mapHeight;
    private Boundary boundary;
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;

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

    private void addCollumnsAndRows() {
        for (int index=0;index<mapWidth;index++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(widthCeil));
            Label labelW = new Label(Integer.toString(boundary.lowerLeft().getX() + index+1));
            mapGrid.add(labelW,index+1,0);
            GridPane.setHalignment(labelW, HPos.CENTER);
        }
        for (int index=0;index<mapHeight;index++) {
            mapGrid.getRowConstraints().add(new RowConstraints(heightCeil));
            Label labelH = new Label(Integer.toString(boundary.lowerLeft().getY() + index+1));
            mapGrid.add(labelH,0,index+1);
            GridPane.setHalignment(labelH, HPos.CENTER);
        }
    }

    private void addElements() {
        for (int w = xMin; w <= xMax; w++) {
            for (int h = yMin; h <= yMax; h++) {
                Vector2d position = new Vector2d(w, h);
                if (map.isOccupied(position)) {
                    WorldElement element = map.objectAt(position);
                    if (element != null) {
                        Label label = new Label(element.toString());
                        mapGrid.add(label, w - xMin + 1, yMax - h + 1);
                        GridPane.setHalignment(label, HPos.CENTER);
                    }
                }
            }
        }
    }


    public void drawMap() {
        clearGrid(); // Usuń poprzednią zawartość siatki

        getCurrentParameters(); // Pobierz aktualne parametry mapy

        mapGrid.getColumnConstraints().add(new ColumnConstraints(widthCeil));
        mapGrid.getRowConstraints().add(new RowConstraints(heightCeil));
        Label label = new Label("y/x");
        mapGrid.add(label, 0, 0);
        GridPane.setHalignment(label, HPos.CENTER);

        addCollumnsAndRows();

        // Rysowanie zawartości mapy
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                Vector2d position = new Vector2d(x, y);
                Rectangle rectangle = new Rectangle(widthCeil - 2, heightCeil - 2); // Prostokąt z marginesem

                // Ustaw kolor w zależności od zawartości pola
                if (map.isOccupied(position)) {
                    Object object = map.objectAt(position);
                    if (object instanceof Plant plant) {
                        if (plant.isLarge()) {
                            rectangle.setFill(javafx.scene.paint.Color.DARKGREEN); // Duże drzewo
                        } else {
                            rectangle.setFill(javafx.scene.paint.Color.LIGHTGREEN); // Zwykłe drzewo
                        }
                    } else if (object instanceof Animal) {
                        rectangle.setFill(javafx.scene.paint.Color.BURLYWOOD); // Zwierzę
                    }
                } else {
                    rectangle.setFill(javafx.scene.paint.Color.BEIGE); // Puste pole
                }

                // Dodanie prostokąta do siatki
                mapGrid.add(rectangle, x - xMin + 1, yMax - y + 1); // Pozycja w siatce
            }
        }
    }


    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            infoLabel.setText(message); // Aktualizacja wiadomości w GUI
            updateDynamicElements();   // Aktualizuj tylko dynamiczne elementy
        });
    }


    private double cellSize; // Rozmiar komórki, obliczany dynamicznie


    public void initializeMap() {
        clearGrid(); // Usuń istniejącą zawartość siatki

        getCurrentParameters(); // Pobierz parametry mapy (wymiary, zakresy)

        // Wyraźnie ustaw dokładnie `mapWidth` kolumn i `mapHeight` wierszy
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







    private void scaleCells() {
        double cellWidth = 800.0 / mapWidth;
        double cellHeight = 800.0 / mapHeight;

        // Ustaw rozmiary kolumn i wierszy
        for (ColumnConstraints col : mapGrid.getColumnConstraints()) {
            col.setPrefWidth(cellWidth);
            col.setMinWidth(cellWidth);
            col.setMaxWidth(cellWidth);
        }
        for (RowConstraints row : mapGrid.getRowConstraints()) {
            row.setPrefHeight(cellHeight);
            row.setMinHeight(cellHeight);
            row.setMaxHeight(cellHeight);
        }

        // Debugowanie
        System.out.println("Cell size: " + cellWidth + "x" + cellHeight);
    }







    public void updateDynamicElements() {
        // Usuń dynamiczne elementy
        mapGrid.getChildren().removeIf(node -> node instanceof Rectangle && !((Rectangle) node).getFill().equals(javafx.scene.paint.Color.LIGHTYELLOW));

        // Dodaj rośliny
        if (map instanceof EquatorialForest) {
            Map<Vector2d, Plant> plants = ((EquatorialForest) map).getPlants();
            for (Vector2d position : plants.keySet()) {
                Plant plant = plants.get(position);
                if (plant != null) {
                    Rectangle plantRectangle = new Rectangle(800.0 / mapWidth - 2, 800.0 / mapHeight - 2); // Margines 2px
                    plantRectangle.setFill(plant.isLarge() ? javafx.scene.paint.Color.DARKGREEN : javafx.scene.paint.Color.LIGHTGREEN);
                    mapGrid.add(plantRectangle, position.getX(), position.getY());
                }
            }
        }

        // Dodaj zwierzęta
        if (map instanceof AbstractWorldMap) {
            Map<Vector2d, List<Animal>> animals = ((AbstractWorldMap) map).getAnimals();
            for (Vector2d position : animals.keySet()) {
                List<Animal> animalsAtPosition = animals.get(position);
                if (animalsAtPosition != null && !animalsAtPosition.isEmpty()) {
                    Rectangle animalRectangle = new Rectangle(800.0 / mapWidth - 2, 800.0 / mapHeight - 2); // Margines 2px
                    animalRectangle.setFill(javafx.scene.paint.Color.BURLYWOOD); // Kolor zwierząt
                    mapGrid.add(animalRectangle, position.getX(), position.getY());
                }
            }
        }
    }






    private Thread simulationThread;
    private Simulation simulation;



    public void initializeSimulation(int mapWidth, int mapHeight, int numberOfAnimals, int numberOfPlants, int plantEnergy, int animalEnergy, int minEnergy) {
        PositionGenerator positionGenerator = new PositionGenerator(mapWidth, mapHeight);
        List<Vector2d> startPositions = new ArrayList<>();

        // Generowanie unikalnych pozycji
        for (int i = 0; i < numberOfAnimals; i++) {
            Vector2d position;

            // Pętla zapewniająca unikalność pozycji
            do {
                position = positionGenerator.generateUniquePositions(1).get(0);
            } while (startPositions.contains(position)); // Sprawdzanie unikalności

            startPositions.add(position);
        }

        // Generowanie sekwencji ruchów
        List<List<MoveDirection>> directionSequences = SimulationInputGenerator.generateRandomMoveSequences(
                numberOfAnimals, 5, 20
        );

        AbstractWorldMap map = new EarthMap(mapWidth, mapHeight, minEnergy);

        // Dodaj początkowe rośliny
        if (map instanceof EquatorialForest) {
            ((EquatorialForest) map).growPlants(numberOfPlants, plantEnergy);
        }

        map.addObserver(this);
        this.setWorldMap(map);

        // Wywołaj jednorazową inicjalizację mapy
        Platform.runLater(() -> {
            initializeMap();
        });

        simulation = new Simulation(startPositions, directionSequences, map, plantEnergy, animalEnergy, minEnergy);
        new Thread(simulation).start();
    }









}