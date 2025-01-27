// Klasa reprezentująca roślinę
package agh.ics.oop.model;

import java.util.List;

public class Plant implements WorldElement {
    private final Vector2d position;
    private final boolean isLarge;
    private final int energy;
    private final boolean isMainPart; // Określa, czy to główna część dużego drzewa

    public Plant(Vector2d position, boolean isLarge, int energy) {
        this.position = position;
        this.isLarge = isLarge;
        this.energy = isLarge ? energy + 20 : energy;
        this.isMainPart = !isLarge || position.equals(calculateMainPosition(position));
    }

    public boolean isLarge() {
        return isLarge;
    }

    public List<Vector2d> getArea() {
        if (!isLarge) {
            return List.of(position);
        }
        // Zwraca wszystkie pola 2x2, które zajmuje duże drzewo
        return List.of(
                position,
                position.add(new Vector2d(1, 0)),
                position.add(new Vector2d(0, 1)),
                position.add(new Vector2d(1, 1))
        );
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return isLarge ? "L" : "P"; // Duże drzewa oznaczane jako "L", zwykłe jako "P"
    }

    public int getEnergyValue() {
        return energy;
    }

    // Metoda obliczająca główną pozycję dużego drzewa
    private Vector2d calculateMainPosition(Vector2d position) {
        return position.add(new Vector2d(-1, -1));
    }

    public boolean isMainPart() {
        return isMainPart;
    }
}

