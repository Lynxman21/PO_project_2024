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
    private TextField plantEnergy;
    @FXML
    private TextField animalEnergy;
    @FXML
    private TextField mapWidthField;

    @FXML
    private TextField mapHeightField;

    @FXML
    private TextField animalCountField;

    @FXML
    private TextField plantCountField;

    @FXML
    private Label infoLabel;

    public void onSimulationStartClicked(ActionEvent actionEvent) {
        try {
            // Pobierz parametry mapy i symulacji
            int mapWidth = Integer.parseInt(mapWidthField.getText().trim());
            int mapHeight = Integer.parseInt(mapHeightField.getText().trim());
            int numberOfAnimals = Integer.parseInt(animalCountField.getText().trim());
            int numberOfPlants = Integer.parseInt(plantCountField.getText().trim());
            int numberOfPlantEnergy = Integer.parseInt(plantEnergy.getText().trim());
            int numberOfAnimalEnergy = Integer.parseInt(animalEnergy.getText().trim());

            // Walidacja danych
            if (mapWidth <= 0 || mapHeight <= 0) {
                infoLabel.setText("Error: Map dimensions must be greater than 0.");
                return;
            }
            if (numberOfAnimals <= 0) {
                infoLabel.setText("Error: Number of animals must be greater than 0.");
                return;
            }
            if (numberOfPlants < 0) {
                infoLabel.setText("Error: Number of plants cannot be negative.");
                return;
            }
            if (numberOfPlantEnergy < 0) {
                infoLabel.setText("Error: Number of energy from plants cannot be negative.");
                return;
            }
            if (numberOfAnimalEnergy < 0) {
                infoLabel.setText("Error: Number of animal's energy cannot be negative.");
                return;
            }


            // Wczytanie widoku symulacji
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SimulationView.fxml"));
            Stage simulationStage = new Stage();
            simulationStage.setScene(new Scene(loader.load()));
            simulationStage.setTitle("Simulation");

            // Przekazanie danych do symulacji
            SimulationViewPresenter simulationPresenter = loader.getController();
            simulationPresenter.initializeSimulation(mapWidth, mapHeight, numberOfAnimals, numberOfPlants,numberOfPlantEnergy,numberOfAnimalEnergy);

            // Otwórz okno symulacji
            simulationStage.show();
        } catch (NumberFormatException e) {
            infoLabel.setText("Error: Please enter valid numerical values.");
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Unexpected error occurred.");
        }
    }
}
