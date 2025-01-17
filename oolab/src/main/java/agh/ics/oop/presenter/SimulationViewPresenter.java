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

import static agh.ics.oop.OptionsParser.parse;

public class SimulationViewPresenter implements MapChangeListener {
    private WorldMap map;

    @FXML
    private Label infoLabel;

    @FXML
    private TextField moveList;

    @FXML
    private GridPane mapGrid;

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
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // hack to retain visible grid lines
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
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
            drawMap();                  // Odświeżenie mapy
        });
    }


    public void initializeSimulation(int mapWidth, int mapHeight, int numberOfAnimals, int numberOfPlants, int numberOfPlantEnergy, int numberOfAnimalEnergy) {
        PositionGenerator positionGenerator = new PositionGenerator(mapWidth, mapHeight);

        // Generuj pozycje startowe dla zwierząt
        List<Vector2d> animalPositions = positionGenerator.generateUniquePositions(numberOfAnimals);

        // Generowanie sekwencji ruchów
        List<List<MoveDirection>> directionSequences = SimulationInputGenerator.generateRandomMoveSequences(
                numberOfAnimals, 5, 20
        );
        System.out.println(directionSequences);

        AbstractWorldMap map = new EarthMap(mapWidth, mapHeight);

        // Dodaj początkowe rośliny
        if (map instanceof EquatorialForest) {
            ((EquatorialForest) map).growPlants(numberOfPlants,numberOfPlantEnergy); // Dodaj rośliny na start
        }

        map.addObserver(this);

        Simulation simulation = new Simulation(animalPositions, directionSequences, map, numberOfAnimalEnergy);
        this.setWorldMap(map);

        new Thread(simulation).start();
    }







}
