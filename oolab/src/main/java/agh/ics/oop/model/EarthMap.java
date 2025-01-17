package agh.ics.oop.model;

import agh.ics.oop.model.util.EcosystemManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class EarthMap extends EquatorialForest {
    private final EcosystemManager manager;

    public EarthMap(int width, int height) {
        super(width, height);
        manager = new EcosystemManager(this);
    }

    public Map<Vector2d, Plant> getPlants() {
        return plants;
    }

    @Override
    public void move(Animal animal, MoveDirection direction) {
        Vector2d oldPosition = animal.getPosition();
//        growPlants(); TU SIĘ DODAJĄ NOWE ROŚLINY PRZY RUCHU

        // Wrapowanie dla krawędzi wschodniej i zachodniej
        if (direction == MoveDirection.FORWARD) {
            if (animal.getDirection() == MapDirection.EAST && oldPosition.getX() == width - 1) {
                animal.setPosition(new Vector2d(0, oldPosition.getY())); // Przejście na lewą krawędź
            } else if (animal.getDirection() == MapDirection.WEST && oldPosition.getX() == 0) {
                animal.setPosition(new Vector2d(width - 1, oldPosition.getY())); // Przejście na prawą krawędź
            }
        }

        // Obrót dla północy i południa
        if ((animal.getDirection() == MapDirection.NORTH && direction == MoveDirection.FORWARD && oldPosition.getY() == height - 1) ||
                (animal.getDirection() == MapDirection.SOUTH && direction == MoveDirection.FORWARD && oldPosition.getY() == 0)) {
            animal.rotate(4); // Obrót o 180 stopni
        } else {
            animal.move(direction, this); // Standardowy ruch
        }

        Vector2d newPosition = animal.getPosition();

        if (!oldPosition.equals(newPosition)) {
            if (animals.get(oldPosition).size() != 1) {
                for (int i=0;i<animals.get(oldPosition).size();i++) {
                    if (animals.get(oldPosition).get(i).equals(animal)) {
                        animals.get(oldPosition).remove(i);
                    }
                }
            }
            else {
                animals.get(oldPosition).remove(0);
                animals.remove(oldPosition);
            }

            if (animals.containsKey(newPosition)) {
                animals.get(newPosition).add(animal);
            }
            else {
                animals.put(newPosition,new ArrayList<Animal>());
                animals.get(newPosition).add(animal);
            }

            // Usuwanie roślin po wejściu zwierzęcia na pole
            Plant plant = plants.get(newPosition);
            if (plant != null) {
                System.out.println("Przed");
                System.out.println(animal.getEnergy());
                manager.plantConsume(animal,plant);
                System.out.println("Po");
                System.out.println(animal.getEnergy());
            }

            animal.incrementEnergy(-1);
//            manager.isAnimalAlive(animal);
            if (animal.getEnergy()<=0) {
                this.removeAnimal(animal.getPosition());
            }
            informObservers("Animal moved to: " + newPosition);
        }
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position) || plants.containsKey(position); // Sprawdza zajętość przez zwierzęta i rośliny
    }

}