package agh.ics.oop.presenter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainViewPresenter {

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

    @FXML
    private TextField plantEnergy;

    @FXML
    private TextField animalEnergy;

    @FXML
    private TextField minEnergy;

    @FXML
    private TextField plantPerDay;

    @FXML
    private TextField minMutationNumber;

    @FXML
    private TextField maxMutationNumber;

    @FXML
    private TextField genomLen;

    @FXML
    private TextField energyForChild;

    @FXML
    private TextField time;

    @FXML
    private CheckBox saveToCSVSwitch;

    public void onSimulationStartClicked(ActionEvent actionEvent) {
        try {
            // Pobierz parametry mapy i symulacji
            int mapWidth = Integer.parseInt(mapWidthField.getText().trim());
            int mapHeight = Integer.parseInt(mapHeightField.getText().trim());
            int numberOfAnimals = Integer.parseInt(animalCountField.getText().trim());
            int numberOfPlants = Integer.parseInt(plantCountField.getText().trim());
            int defaultPlantEnergy = Integer.parseInt(plantEnergy.getText().trim());
            int deafultAnimalEnergy = Integer.parseInt(animalEnergy.getText().trim());
            int minimumEnergy = Integer.parseInt(minEnergy.getText().trim());
            int plantPerDayNum = Integer.parseInt(plantPerDay.getText().trim());
            int minMutations = Integer.parseInt(minMutationNumber.getText().trim());
            int maxMutations = Integer.parseInt(maxMutationNumber.getText().trim());
            int genomLength = Integer.parseInt(genomLen.getText().trim());
            int energyForNewAnimal = Integer.parseInt(energyForChild.getText().trim());
            int dayTime = Integer.parseInt(time.getText().trim());

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
            if (defaultPlantEnergy <= 0) {
                infoLabel.setText("Error: Plant energy must be greater than 0.");
                return;
            }
            if (deafultAnimalEnergy < 0) {
                infoLabel.setText("Error: Animal energy cannot be negative.");
                return;
            }
            if (minimumEnergy < 0) {
                infoLabel.setText("Error: Minimum energy cannot be negative.");
                return;
            }
            if (plantPerDayNum <= 0) {
                infoLabel.setText("Error: Count of plants cannot be negative.");
                return;
            }
            if (minMutations < 0) {
                infoLabel.setText("Error: Minimum mutations cannot be negative.");
                return;
            }
            if (maxMutations < 0) {
                infoLabel.setText("Error: Maximum mutations cannot be negative.");
                return;
            }
            if (genomLength <= 0) {
                infoLabel.setText("Error: Genom length cannot be negative.");
                return;
            }
            if (energyForNewAnimal <= 0) {
                infoLabel.setText("Error: Energy for child cannot be negative.");
                return;
            }
            if (dayTime <= 0) {
                infoLabel.setText("Error: Day time cannot be negative.");
                return;
            }


            // Wczytaj nową scenę symulacji
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SimulationView.fxml"));
            Stage simulationStage = new Stage();
            simulationStage.setScene(new Scene(loader.load()));
            simulationStage.setTitle("Simulation");


            // Pobierz kontroler i zainicjalizuj symulację
            SimulationViewPresenter simulationPresenter = loader.getController();
            simulationPresenter.setSaveToCSVEnabled(saveToCSVSwitch != null && saveToCSVSwitch.isSelected());
            simulationPresenter.initializeSimulation(
                    mapWidth, mapHeight, numberOfAnimals, numberOfPlants,
                    defaultPlantEnergy, deafultAnimalEnergy, minimumEnergy,
                    plantPerDayNum, minMutations, maxMutations, genomLength,
                    energyForNewAnimal, dayTime
            );


            // Przekazanie wartości przełącznika saveToCSVSwitch
            boolean saveToCSV = saveToCSVSwitch != null && saveToCSVSwitch.isSelected();
            simulationPresenter.setSaveToCSVEnabled(saveToCSV);

            // Otwórz okno symulacji
            simulationStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Unexpected error occurred.");
        }
    }
}
