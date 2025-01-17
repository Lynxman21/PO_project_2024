// Klasa reprezentująca roślinę
package agh.ics.oop.model;

import java.util.List;
import java.util.Random;

public class Plant implements WorldElement {
    private final Vector2d position;
    private final boolean isLarge;
    private final int energyValue;

    public int getEnergyValue() {
        return energyValue;
    }

    public Plant(Vector2d position, boolean isLarge,int energyValue) {
        this.position = position;
        this.isLarge = isLarge;
        this.energyValue = isLarge ? energyValue+20 : energyValue;
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
}