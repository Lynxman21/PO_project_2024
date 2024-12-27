package agh.ics.oop.presenter;

import agh.ics.oop.OptionsParser;
import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationEngine;
import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Boundary;
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
import javafx.stage.Stage;

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
        for (int w=xMin;w<=xMax;w++) {
            for (int h=yMin;h<=yMax;h++) {
                Vector2d position = new Vector2d(w,h);
                if (map.isOccupied(position)) {
                    Label label = new Label(map.objectAt(position).toString());
                    mapGrid.add(label,w-xMin+1,yMax-h+1);
                    mapGrid.setHalignment(mapGrid.getChildren().get(mapGrid.getChildren().size() - 1), HPos.CENTER);
                }
            }
        }
    }

    public void drawMap() {
        clearGrid();
        getCurrentParameters();

        mapGrid.getColumnConstraints().add(new ColumnConstraints(widthCeil));
        mapGrid.getRowConstraints().add(new RowConstraints(heightCeil));
        Label label = new Label("y/x");
        mapGrid.add(label, 0, 0);
        GridPane.setHalignment(label, HPos.CENTER);

        addCollumnsAndRows();
        addElements();
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            infoLabel.setText(message);
            drawMap();
        });
    }

    public void initializeSimulation(String moves) {
        List<MoveDirection> directions = OptionsParser.parse(List.of(moves.split(" ")));
        AbstractWorldMap map = new GrassField(10);
        List<Vector2d> positions = List.of(new Vector2d(1, 1), new Vector2d(3, 5));
        map.addObserver(this); // Obserwacja mapy

        Simulation simulation = new Simulation(positions, directions, map);
        this.setWorldMap(map);
        SimulationEngine simulationEngine = new SimulationEngine(List.of(simulation));
        new Thread(simulationEngine::runSync).start();
    }
}
