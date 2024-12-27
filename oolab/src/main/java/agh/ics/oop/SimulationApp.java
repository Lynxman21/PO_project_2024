package agh.ics.oop;

<<<<<<< Updated upstream
import agh.ics.oop.model.*;
import agh.ics.oop.presenter.SimulationPresenter;
=======
import agh.ics.oop.presenter.MainViewPresenter;
import agh.ics.oop.presenter.SimulationViewPresenter;
>>>>>>> Stashed changes
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class SimulationApp extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
<<<<<<< Updated upstream
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
=======
        loader.setLocation(getClass().getClassLoader().getResource("MainView.fxml"));
>>>>>>> Stashed changes
        BorderPane viewRoot = loader.load();
        MainViewPresenter presenter = loader.getController();
        configureStage(primaryStage,viewRoot);
        primaryStage.show();
    }

    private void configureStage(Stage primaryStage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation app");
        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }
}
