package agh.ics.oop;

import agh.ics.oop.model.exceptions.IncorrectPositionException;
import agh.ics.oop.model.*;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {
    private List<Animal> animals;
    private List<MoveDirection> directions;
    private WorldMap map;

    public Simulation(List<Vector2d> startPositions, List<MoveDirection> directions, WorldMap map) {
        this.animals = new ArrayList<>();
        for (Vector2d position:startPositions) {
            if (!map.isOccupied(position)) {
                Animal animal = new Animal(position);
                try {
                    map.place(animal);
                    animals.add(animal);
                } catch (IncorrectPositionException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        this.directions = directions;
        this.map = map;
    }

    public boolean isAtGoodPossisioned(int animalIndex, Vector2d vector) {
        return animals.get(animalIndex).isAt(vector);
    }

    public boolean isGoodOriented(int animalIndex, MapDirection orient) {
        return animals.get(animalIndex).getDirection() == orient;
    }

    public void run() {
        int animalArraySize = animals.size();

        for (int index=0;index<directions.size();index++) {
            map.move(animals.get(index%animalArraySize),directions.get(index));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
