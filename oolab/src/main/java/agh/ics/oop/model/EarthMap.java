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
                animal.rotate(7); // Obrót w lewo
                newPosition = oldPosition.add(animal.getDirection().toUnitVector());
            }
            case RIGHT -> {
                animal.rotate(1); // Obrót w prawo
                newPosition = oldPosition.add(animal.getDirection().toUnitVector());
            }
        }

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

        informObservers("");
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

        int size1 = moves1.size();
        int size2 = moves2.size();

        int breakpoint1 = (int) (size1 * ratioParent1);
        int breakpoint2 = size2 - breakpoint1;

        List<MoveDirection> childGenotype = new ArrayList<>();
        Random random = new Random();

        // Losuj stronę podziału: true -> moves1 z lewej, false -> moves2 z lewej
        boolean moves1Left = random.nextBoolean();

        if (moves1Left) {
            // Lewa strona od moves1, prawa od moves2
            childGenotype.addAll(moves1.subList(0, breakpoint1));
            childGenotype.addAll(moves2.subList(size2 - breakpoint2, size2));
        } else {
            // Lewa strona od moves2, prawa od moves1
            childGenotype.addAll(moves2.subList(0, breakpoint2));
            childGenotype.addAll(moves1.subList(size1 - breakpoint1, size1));
        }

        return childGenotype;
    }

    private void mutateGenotype(List<MoveDirection> genotype, int minMutate, int maxMutate) {
        if (genotype.isEmpty()) {
            System.err.println("Error: Genotype is empty. No mutations applied.");
            return;
        }

        Random random = new Random();
        int mutations = random.nextInt( (maxMutate-minMutate)+ 1)+minMutate; // Liczba mutacji (od 0 do rozmiaru genotypu)

        for (int i = 0; i < mutations; i++) {
            boolean type = random.nextBoolean();
            if (type) {
                int mutationIndex = random.nextInt(genotype.size());
                genotype.set(mutationIndex, MoveDirection.values()[random.nextInt(MoveDirection.values().length)]);
            }
            else {
                int firstIndex = random.nextInt(genotype.size());
                int secondIndex = random.nextInt(genotype.size());

                while (firstIndex==secondIndex) {
                    secondIndex = random.nextInt(genotype.size());
                }

                MoveDirection temp = genotype.get(firstIndex);
                genotype.set(firstIndex,genotype.get(secondIndex));
                genotype.set(secondIndex,temp);
            }
        }
    }

    public Animal reproduce(Animal parent1, Animal parent2, Statistics statistics, int minMutate, int maxMutate, int genLen) {
        int totalEnergy = parent1.getEnergy() + parent2.getEnergy();
        double ratioParent1 = (double) parent1.getEnergy() / totalEnergy;

        // Stwórz genotyp dziecka
        List<MoveDirection> childGenotype = createChildGenotype(parent1, parent2, ratioParent1);
        mutateGenotype(childGenotype,minMutate,maxMutate);

        // Rodzice tracą energię na rzecz dziecka
        parent1.incrementEnergy(-minEnergy);
        parent2.incrementEnergy(-minEnergy);

        // Stwórz dziecko
        Animal child = new Animal(parent1.getPosition(), 2 * minEnergy, genLen);
        child.setMoves(childGenotype);
        child.setDirection(MapDirection.values()[new Random().nextInt(MapDirection.values().length)]);

        // Rodzice zwiększają licznik dzieci
        parent1.incrementChildrenCount();
        parent2.incrementChildrenCount();
        statistics.newAverageChildrenCount(parent1);
        statistics.newAverageChildrenCount(parent2);
        return child;
    }
}