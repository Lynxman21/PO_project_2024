package agh.ics.oop.model;

public class EarthMap extends EquatorialForest {

    public EarthMap(int width, int height) {
        super(width, height);
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
            animal.rotate(2); // Obrót o 180 stopni
        } else {
            animal.move(direction, this); // Standardowy ruch
        }

        Vector2d newPosition = animal.getPosition();

        if (!oldPosition.equals(newPosition)) {
            animals.remove(oldPosition);
            animals.put(newPosition, animal);

            // Usuwanie roślin po wejściu zwierzęcia na pole
            Plant plant = plants.get(newPosition);
            if (plant != null) {
                if (plant.isLarge()) {
                    for (Vector2d field : plant.getArea()) {
                        plants.remove(field);
                    }
                } else {
                    plants.remove(newPosition);
                }
            }

            informObservers("Animal moved to: " + newPosition);
        }
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position) || plants.containsKey(position); // Sprawdza zajętość przez zwierzęta i rośliny
    }

}
