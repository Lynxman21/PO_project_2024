package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;

import java.util.*;

public class EquatorialForest extends AbstractWorldMap {

    protected final int width;
    protected final int height;
    protected Map<Vector2d, Plant> plants = new HashMap<>();
    protected final Random random = new Random(); // static?
    protected final Set<Vector2d> preferredFields = new HashSet<>();
    protected final Set<Vector2d> nonPreferredFields = new HashSet<>();

    public EquatorialForest(int width, int height) {
        super(new HashMap<>(), width, height);
        this.width = width;
        this.height = height;
        initializeFields();
    }

    public synchronized void removePlant(Vector2d position) {
        plants.remove(position); // Synchronizowany dostęp do usuwania
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

    public synchronized void addPlant(Vector2d position, Plant plant) {
        plants.put(position, plant); // Synchronizowany dostęp do modyfikacji
    }

    public synchronized Map<Vector2d, Plant> getPlants() {
        return new HashMap<>(plants); // Zwróć kopię mapy, aby uniknąć modyfikacji podczas iteracji
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        // Możemy się poruszyć, jeśli pozycja nie jest zajęta przez zwierzęta lub rośliny
        return !animals.containsKey(position);
    }


    @Override
    public void growPlants() {
        growPlants(0, 10); // Domyślne wywołanie z initialCount = 0 - generowanie losowych drzew w czasie symulacji
    }

    public void growPlants(int initialCount, int energy) {
        // Usuń rośliny, które są zajęte przez zwierzęta
        plants.entrySet().removeIf(entry -> animals.containsKey(entry.getKey()));

        int plantsToGrow = initialCount > 0 ? initialCount : random.nextInt(10) + 1;

        for (int i = 0; i < plantsToGrow; i++) {
            boolean preferPreferred = random.nextDouble() < 0.8; // 80% szans na preferowane pola
            Set<Vector2d> targetFields = preferPreferred ? preferredFields : nonPreferredFields;

            // Filtruj dostępne pola
            List<Vector2d> availableFields = targetFields.stream()
                    .filter(field -> !plants.containsKey(field) && !animals.containsKey(field)) // Pole nie może być zajęte
                    .toList();

            if (!availableFields.isEmpty()) {
                Vector2d position = availableFields.get(random.nextInt(availableFields.size())); // Wybór losowego pola
                boolean isLarge = preferPreferred && random.nextDouble() < 0.2; // 20% szans na duże drzewo

                if (isLarge) {
                    // Duże drzewo - sprawdzenie dostępności wszystkich pól 2x2
                    List<Vector2d> area = new Plant(position, true, energy).getArea();
                    boolean canPlaceLargeTree = area.stream()
                            .allMatch(field -> field.getX() >= 0 && field.getY() >= 0 &&
                                    field.getX() < width && field.getY() < height &&
                                    !plants.containsKey(field) && !animals.containsKey(field));

                    if (canPlaceLargeTree) {
                        Plant largePlant = new Plant(position, true, energy);
                        for (Vector2d field : area) {
                            plants.put(field, largePlant); // Zajmij wszystkie pola dla dużego drzewa
                        }
                        System.out.println("Large plant placed at: " + position);
                    } else {
                        System.out.println("Cannot place large plant at: " + position);
                    }
                } else {
                    // Małe drzewo - zajmuje jedno pole
                    plants.put(position, new Plant(position, false, energy));
                }
            } else {
                System.out.println("No free fields available for plants.");
                break;
            }
        }
    }

    public int getFreeFieldsCount() {
        int totalFields = width * height; // Całkowita liczba pól na mapie
        int animalsCount = animals.values().stream().mapToInt(List::size).sum(); // Liczba zwierząt na mapie
        return totalFields - animalsCount; // Wolne pola = całkowite pola - liczba zwierząt
    }

    @Override
    public void move(Animal animal, MoveDirection direction) { // pusto tu
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
        return new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1)); // dwa nowe obiekty co wywołanie?
    }
}