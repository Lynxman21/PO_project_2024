package agh.ics.oop.model;

public class EarthMap extends EquatorialForest {

    public EarthMap(int width, int height) {
        super(width, height);
<<<<<<< Updated upstream
=======
        this.minEnergy = minEnergy;
        manager = new EcosystemManager(this);
    }


    public Map<Vector2d, Plant> getPlants() {
        return plants;
>>>>>>> Stashed changes
    }

    @Override
    public void move(Animal animal, MoveDirection direction) {
        Vector2d oldPosition = animal.getPosition(); // Pozycja przed ruchem
        Vector2d newPosition = oldPosition;          // Nowa pozycja

        // Oblicz nową pozycję na podstawie kierunku
        switch (direction) {
            case FORWARD -> newPosition = oldPosition.add(animal.getDirection().toUnitVector());
            case BACKWARD -> newPosition = oldPosition.add(animal.getDirection().toUnitVector().opposite());
            case LEFT -> {
                animal.rotate(1); // Obrót w lewo
                return; // Nie zmieniamy pozycji
            }
            case RIGHT -> {
                animal.rotate(-1); // Obrót w prawo
                return; // Nie zmieniamy pozycji
            }
        }

<<<<<<< Updated upstream
        // Obrót dla północy i południa
        if ((animal.getDirection() == MapDirection.NORTH && direction == MoveDirection.FORWARD && oldPosition.getY() == height - 1) ||
                (animal.getDirection() == MapDirection.SOUTH && direction == MoveDirection.FORWARD && oldPosition.getY() == 0)) {
            animal.rotate(2); // Obrót o 180 stopni
        } else {
            animal.move(direction, this); // Standardowy ruch
=======
        // Wrapowanie w poziomie (lewo-prawo)
        if (newPosition.getX() >= width) {
            newPosition = new Vector2d(0, newPosition.getY()); // Przejście na lewą krawędź
        } else if (newPosition.getX() < 0) {
            newPosition = new Vector2d(width - 1, newPosition.getY()); // Przejście na prawą krawędź
>>>>>>> Stashed changes
        }

        // Odbijanie w pionie (góra-dół)
        if (newPosition.getY() >= height) {
            animal.rotate(4); // Obrót o 180°
            newPosition = oldPosition; // Zwierzę pozostaje na miejscu
        } else if (newPosition.getY() < 0) {
            animal.rotate(4); // Obrót o 180°
            newPosition = oldPosition; // Zwierzę pozostaje na miejscu
        }

        // Aktualizacja pozycji na mapie
        if (!oldPosition.equals(newPosition)) {
<<<<<<< Updated upstream
            animals.remove(oldPosition);
            animals.put(newPosition, animal);
=======
            // Usuń zwierzę ze starej pozycji
            List<Animal> animalsAtOldPosition = animals.get(oldPosition);
            if (animalsAtOldPosition != null) {
                animalsAtOldPosition.remove(animal);
                if (animalsAtOldPosition.isEmpty()) {
                    animals.remove(oldPosition); // Usuń pozycję, jeśli lista jest pusta
                }
            }

            // Dodaj zwierzę na nową pozycję
            animals.putIfAbsent(newPosition, new ArrayList<>());
            animals.get(newPosition).add(animal);
>>>>>>> Stashed changes

            // Aktualizuj pozycję zwierzęcia
            animal.setPosition(newPosition);

            // Sprawdź, czy na nowej pozycji jest roślina
            Plant plant = plants.get(newPosition);
            if (plant != null) {
<<<<<<< Updated upstream
                if (plant.isLarge()) {
                    for (Vector2d field : plant.getArea()) {
                        plants.remove(field);
                    }
                } else {
                    plants.remove(newPosition);
                }
            }

            informObservers("Animal moved to: " + newPosition);
=======
                manager.plantConsume(animal, plant); // Zwierzę konsumuje roślinę
            }
>>>>>>> Stashed changes
        }

        // Zmniejsz energię zwierzęcia za ruch
        animal.incrementEnergy(-1);

        // Usuń zwierzę, jeśli energia spadnie do zera
        if (animal.getEnergy() <= 0) {
            this.removeAnimal(newPosition, animal);
        }

        // Powiadom obserwatorów o zmianie pozycji
        informObservers("Animal moved to: " + newPosition);
    }









    @Override
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position) || plants.containsKey(position); // Sprawdza zajętość przez zwierzęta i rośliny
    }

}
