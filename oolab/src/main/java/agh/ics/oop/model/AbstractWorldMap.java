package agh.ics.oop.model;

import agh.ics.oop.model.exceptions.IncorrectPositionException;
import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap{
    protected Map<Vector2d,List<Animal>> animals = new HashMap<>();
    protected List<MapChangeListener>observers;
    protected MapVisualizer vizulizer;
    protected final int id;
    protected final int width;
    protected final int height;

    public AbstractWorldMap(Map<Vector2d, List<Animal>> animals, int width, int height) {
        this.animals = Collections.synchronizedMap(new HashMap<>());
        this.vizulizer = new MapVisualizer(this);
        this.id = this.hashCode();
        this.observers = new ArrayList<>();
        this.width = width;
        this.height = height;
    }

    @Override
    public void removeAnimal(Vector2d position, Animal animal) {
        List<Animal> animalsAtPosition = animals.get(position);
        if (animalsAtPosition != null) {
            animalsAtPosition.remove(animal);
            if (animalsAtPosition.isEmpty()) {
                animals.remove(position);
            }
            informObservers("Animal removed from: " + position);
        }
    }


    public abstract void growPlants();

    public abstract void growPlants(int initialCount,int energy);

    @Override
    public int getId() {
        return id;
    }

    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    protected void informObservers(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this,message);
        }
    }

    public Map<Vector2d, List<Animal>> getAnimals() {
        return animals; // Zwierzęta są przechowywane w Map<Vector2d, List<Animal>>
    }

    @Override
    public synchronized void place(Animal animal) throws IncorrectPositionException {
        Vector2d position = animal.getPosition();

        animals.computeIfAbsent(position, k -> new ArrayList<>()).add(animal);
        informObservers("Animal placed at: " + position);
    }

    public Map<Vector2d, Plant> getPlants() {
        throw new UnsupportedOperationException("Plants are not supported on this map type.");
    }

    @Override
    public synchronized void move(Animal animal, int direction) {
        Vector2d oldPosition = animal.getPosition();

        if (!animals.containsKey(oldPosition)) {
            System.err.println("Warning: Position " + oldPosition + " not found in animals map. Adding it now.");
            animals.put(oldPosition, new ArrayList<>(List.of(animal)));
        }

        animal.move(direction, this, this.width, this.height);

        Vector2d newPosition = animal.getPosition();

        // Pobierz drzewa z mapy (jeśli są obsługiwane)
        Map<Vector2d, Plant> plants = getPlants();
        if (plants.containsKey(newPosition)) {
            Plant plant = plants.get(newPosition);
            animal.incrementEnergy(plant.getEnergyValue()); // Zwierzę zyskuje energię

            // Usuwanie drzewa
            if (plant.isLarge()) {
                // Jeśli duże drzewo, usuń wszystkie jego części
                for (Vector2d area : plant.getArea()) {
                    plants.remove(area);
                }
            } else {
                // Zwykłe drzewo - usuń tylko jedną część
                plants.remove(newPosition);
            }
            System.out.println("Animal ate a plant at: " + newPosition + ", energy increased to: " + animal.getEnergy());
        }

        if (!oldPosition.equals(newPosition)) {
            List<Animal> oldPositionAnimals = animals.get(oldPosition);
            oldPositionAnimals.remove(animal);
            if (oldPositionAnimals.isEmpty()) {
                animals.remove(oldPosition);
            }

            animals.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(animal);
            informObservers("Animal moved to: " + newPosition);
        }
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return true; // Pozwala na wchodzenie wielu zwierząt na to samo pole
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
}