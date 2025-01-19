package agh.ics.oop.model;

import agh.ics.oop.model.util.EcosystemManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EarthMap extends EquatorialForest {
    private final EcosystemManager manager;
    private final int minEnergy;

    public EarthMap(int width, int height, int minEnergy) {
        super(width, height);
        this.minEnergy = minEnergy;
        manager = new EcosystemManager(this);
    }


    public Map<Vector2d, Plant> getPlants() {
        return plants;
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

        // Wrapowanie w poziomie (lewo-prawo)
        if (newPosition.getX() >= width) {
            newPosition = new Vector2d(0, newPosition.getY()); // Przejście na lewą krawędź
        } else if (newPosition.getX() < 0) {
            newPosition = new Vector2d(width - 1, newPosition.getY()); // Przejście na prawą krawędź
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

            // Aktualizuj pozycję zwierzęcia
            animal.setPosition(newPosition);

            // Sprawdź, czy na nowej pozycji jest roślina
            Plant plant = plants.get(newPosition);
            if (plant != null) {
                manager.plantConsume(animal, plant); // Zwierzę konsumuje roślinę
            }
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

    public Animal reproduce(Animal parent1, Animal parent2) {
        int childEnergy = 2*minEnergy;

        parent1.incrementEnergy(-minEnergy);
        parent2.incrementEnergy(-minEnergy);

        ArrayList<MoveDirection> childGenotype = createChildGenotype(parent1, parent2);

        Animal child = new Animal(parent1.getPosition(),childEnergy);
        child.setMoves(childGenotype);
        return child;
    }

    public ArrayList<MoveDirection> createChildGenotype(Animal parent1, Animal parent2) {
        int totalEnergy = 2*minEnergy;
        double ratioParent1 = (double) parent1.getEnergy() / totalEnergy;
        double ratioParent2 = (double) parent2.getEnergy() / totalEnergy;

        Random random = new Random();
        boolean takeRightFromStronger = random.nextBoolean();

        int breakpoint1 = (int) (parent1.getMoves().size() * ratioParent1);
        int breakpoint2 = (int) (parent2.getMoves().size() * ratioParent2);

        ArrayList<MoveDirection> childGenotype = new ArrayList<>();

        if (takeRightFromStronger) {
            childGenotype.addAll(parent1.getMoves().subList(0, breakpoint1));
            childGenotype.addAll(parent2.getMoves().subList(breakpoint2, parent2.getMoves().size()));
        } else {
            childGenotype.addAll(parent2.getMoves().subList(0, breakpoint2));
            childGenotype.addAll(parent1.getMoves().subList(breakpoint1, parent1.getMoves().size()));
        }

        mutateGenotype(childGenotype);

        return childGenotype;
    }

    public static void mutateGenotype(ArrayList<MoveDirection> genotype) {
        ArrayList<MoveDirection> m = new ArrayList<>(List.of(MoveDirection.FORWARD,MoveDirection.RIGHT,MoveDirection.BACKWARD,MoveDirection.LEFT));
        Random random = new Random();
        int numberOfMutations = random.nextInt(genotype.size());

        for (int i = 0; i < numberOfMutations; i++) {
            int mutationIndex = random.nextInt(genotype.size());
            genotype.set(mutationIndex, m.get(random.nextInt(4)));
        }
    }
}