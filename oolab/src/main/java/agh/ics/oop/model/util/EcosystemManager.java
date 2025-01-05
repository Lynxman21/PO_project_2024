package agh.ics.oop.model.util;

import agh.ics.oop.model.*;

import java.util.List;
import java.util.Map;

public class EcosystemManager {
    private EarthMap map;
    private Map<Vector2d, Plant> plantList;

    public EcosystemManager(EarthMap map) {
        this.map = map;
        this.plantList = map.getPlants();
    }

    public void isAnimalAlive(Animal animal) {
        if (animal.getEnergy() == 0) {
            map.removeAnimal(animal.getPosition());
        }
    }

    public void plantConsume(Animal animal, Plant plant) {
        Vector2d position = animal.getPosition();
        animal.incrementEnergy(plantList.get(position).getEnergyValue());

        if (plant.isLarge()) {
            for (Vector2d field : plant.getArea()) {
                map.removePlant(field);
            }
        } else {
            map.removePlant(position);
        }

    }
}
