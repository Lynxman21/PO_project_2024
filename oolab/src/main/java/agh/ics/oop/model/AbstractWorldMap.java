package agh.ics.oop.model;

import agh.ics.oop.model.exceptions.IncorrectPositionException;
import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractWorldMap implements WorldMap{
    protected Map<Vector2d,Animal> animals = new HashMap<>();
    protected List<MapChangeListener>observers;
    protected MapVisualizer vizulizer;
    protected final int id;

    public AbstractWorldMap(Map<Vector2d, Animal> animals) {
        this.animals = animals;
        this.vizulizer = new MapVisualizer(this);
        this.id = this.hashCode();
        this.observers = new ArrayList<>();
    }

    public abstract void growPlants();

    public abstract void growPlants(int initialCount);

    @Override
    public int getId() {
        return id;
    }

    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    public void removeObserver(MapChangeListener observer) {
        observers.remove(observer);
    }

    protected void informObservers(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this,message);
        }
    }

    @Override
    public void place(Animal animal) throws IncorrectPositionException{
        Vector2d vect = animal.getPosition();
        if (this.canMoveTo(vect)) {
            animals.put(vect, animal);
            informObservers("Animal placed at: " + animal.getPosition());
        }
        else {
            throw new IncorrectPositionException(vect);
        }
    }

    @Override
    public void move(Animal animal, MoveDirection direction) {
        Vector2d oldPosition = animal.getPosition();
        animal.move(direction, this);
        if (!oldPosition.equals(animal.getPosition())) {
            animals.remove(oldPosition);
            animals.put(animal.getPosition(),animal);
            informObservers("Animal moved to: " + animal.getPosition());
        }
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position);
    }

    public String toString() {
        Boundary boundary = this.getCurrentBounds();
        return vizulizer.draw(boundary.lowerLeft(),boundary.upperRight());
    }

    public abstract Boundary getCurrentBounds();

    public abstract WorldElement objectAt(Vector2d position);

    public List<WorldElement> getElements() {
        return new ArrayList<>(animals.values());
    }
}
