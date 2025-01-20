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
                animalsAtOldPosition.remove(animal); // Usuń zwierzę z listy
                if (animalsAtOldPosition.isEmpty()) {
                    animals.remove(oldPosition); // Usuń całą pozycję, jeśli lista jest pusta
                }
            }

            // Dodaj zwierzę na nową pozycję
            animals.putIfAbsent(newPosition, new ArrayList<>());
            animals.get(newPosition).add(animal);

            // Zaktualizuj pozycję zwierzęcia
            animal.setPosition(newPosition);
        }

        // Sprawdź, czy na nowej pozycji jest roślina
        Plant plant = plants.get(newPosition);
        if (plant != null) {
            manager.plantConsume(animal, plant); // Zwierzę konsumuje roślinę
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
    public boolean canMoveTo(Vector2d position) {
        return true; // Możliwość wejścia na dowolne pole
    }







    @Override
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position) || plants.containsKey(position); // Sprawdza zajętość przez zwierzęta i rośliny
    }


    private List<MoveDirection> createChildGenotype(Animal parent1, Animal parent2, double ratioParent1) {
        List<MoveDirection> moves1 = parent1.getMoves();
        List<MoveDirection> moves2 = parent2.getMoves();

        // Sprawdzenie, czy genotypy są poprawne
        if (moves1.isEmpty() || moves2.isEmpty()) {
            throw new IllegalStateException("Parent genotypes cannot be empty.");
        }

        int size1 = moves1.size();
        int size2 = moves2.size();

        int breakpoint1 = Math.max(0, Math.min(size1, (int) (size1 * ratioParent1)));
        int breakpoint2 = Math.max(0, Math.min(size2, size2 - breakpoint1));

        List<MoveDirection> childGenotype = new ArrayList<>();
        if (new Random().nextBoolean()) {
            // Lewa strona od rodzica 1, prawa od rodzica 2
            childGenotype.addAll(moves1.subList(0, breakpoint1));
            childGenotype.addAll(moves2.subList(breakpoint2, size2));
        } else {
            // Lewa strona od rodzica 2, prawa od rodzica 1
            childGenotype.addAll(moves2.subList(0, breakpoint2));
            childGenotype.addAll(moves1.subList(breakpoint1, size1));
        }

        return childGenotype;
    }






    private void mutateGenotype(List<MoveDirection> genotype) {
        if (genotype.isEmpty()) {
            System.err.println("Error: Genotype is empty. No mutations applied.");
            return;
        }

        Random random = new Random();
        int mutations = random.nextInt(genotype.size() + 1); // Liczba mutacji (od 0 do rozmiaru genotypu)

        for (int i = 0; i < mutations; i++) {
            int mutationIndex = random.nextInt(genotype.size());
            genotype.set(mutationIndex, MoveDirection.values()[random.nextInt(MoveDirection.values().length)]);
        }
    }







    public Animal reproduce(Animal parent1, Animal parent2) {
        int totalEnergy = parent1.getEnergy() + parent2.getEnergy();
        double ratioParent1 = (double) parent1.getEnergy() / totalEnergy;

        // Stwórz genotyp dziecka
        List<MoveDirection> childGenotype = createChildGenotype(parent1, parent2, ratioParent1);
        mutateGenotype(childGenotype);

        // Rodzice tracą energię na rzecz dziecka
        parent1.incrementEnergy(-minEnergy);
        parent2.incrementEnergy(-minEnergy);

        // Stwórz dziecko
        Animal child = new Animal(parent1.getPosition(), 2 * minEnergy);
        child.setMoves(childGenotype);
        child.setDirection(MapDirection.values()[new Random().nextInt(MapDirection.values().length)]);

        // Rodzice zwiększają licznik dzieci
        parent1.incrementChildrenCount();
        parent2.incrementChildrenCount();

        return child;
    }




}