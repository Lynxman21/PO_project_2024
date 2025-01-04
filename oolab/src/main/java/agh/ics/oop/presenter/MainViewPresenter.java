package agh.ics.oop.presenter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainViewPresenter {

    @FXML
    private TextField moveList;

    @FXML
    private Label infoLabel;

    public void onSimulationStartClicked(ActionEvent actionEvent) {
        try {
            // Wczytanie widoku symulacji
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SimulationView.fxml"));
            Stage simulationStage = new Stage();
            simulationStage.setScene(new Scene(loader.load()));
            simulationStage.setTitle("Simulation");

            // Pobranie kontrolera widoku symulacji
            SimulationViewPresenter simulationPresenter = loader.getController();

            // Przekazanie danych do symulacji
            String moves = moveList.getText(); // Pobierz tekst z pola
            simulationPresenter.initializeSimulation(moves);

            // Otwórz okno symulacji
            simulationStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}