package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;

import java.util.*;

public class EquatorialForest extends AbstractWorldMap {

    protected final int width;
    protected final int height;
    protected Map<Vector2d, Plant> plants = new HashMap<>();
    protected final Random random = new Random();
    protected final Set<Vector2d> preferredFields = new HashSet<>();
    protected final Set<Vector2d> nonPreferredFields = new HashSet<>();

    public EquatorialForest(int width, int height) {
        super(new HashMap<>(), width, height);
        this.width = width;
        this.height = height;
        initializeFields();
    }

    public void removePlant(Vector2d position) {
        this.plants.remove(position);
    }

    private void initializeFields() {
        int equatorStart = height / 3;
        int equatorEnd = (2 * height) / 3;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Vector2d position = new Vector2d(x, y);
                if (y >= equatorStart && y <= equatorEnd) {
                    preferredFields.add(position);
                } else {
                    nonPreferredFields.add(position);
                }
            }
        }
    }

    public Map<Vector2d, Plant> getPlants() {
        return plants; // Rośliny są przechowywane w Map<Vector2d, Plant>
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        // Możemy się poruszyć, jeśli pozycja nie jest zajęta przez zwierzęta lub rośliny
        return !animals.containsKey(position);
    }

    @Override
    public void growPlants() {
        growPlants(0,10); // Domyślne wywołanie z `initialCount = 0` - potrzebne do generowanie drzew na starcie i potem losowo
    }

    public void growPlants(int initialCount, int energy) {
        plants.entrySet().removeIf(entry -> animals.containsKey(entry.getKey())); // Usuń zajęte rośliny

        int plantsToGrow = initialCount > 0 ? initialCount : random.nextInt(10) + 1;

        for (int i = 0; i < plantsToGrow; i++) {
            boolean preferPreferred = random.nextDouble() < 0.8; // Preferowane pola
            Set<Vector2d> targetFields = preferPreferred ? preferredFields : nonPreferredFields;

            // Filtruj dostępne pola
            List<Vector2d> availableFields = targetFields.stream()
                    .filter(field -> !plants.containsKey(field)) // Pole nie może być zajęte przez inną roślinę
                    .toList();

            if (!availableFields.isEmpty()) {
                Vector2d position = availableFields.get(random.nextInt(availableFields.size())); // Losowe pole
                boolean isLarge = preferPreferred && random.nextDouble() < 0.2; // 20% szans na duże drzewo

                if (isLarge) {
                    List<Vector2d> area = new Plant(position, true, energy).getArea();
                    if (area.stream().allMatch(field -> field.getX() < width && field.getY() < height)) {
                        Plant largePlant = new Plant(position, true, energy);
                        for (Vector2d field : area) {
                            plants.put(field, largePlant);
                        }
                    }
                } else {
                    plants.put(position, new Plant(position, false, energy));
                }
            }
        }
    }


    @Override
    public void move(Animal animal, MoveDirection direction) {

    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position) || plants.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        if (animals.containsKey(position)) return animals.get(position).get(0);
        if (plants.containsKey(position)) return plants.get(position);
        return null;
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
    }
}