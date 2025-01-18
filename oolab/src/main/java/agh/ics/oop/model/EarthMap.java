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
            animal.rotate(4); // Obrót o 180 stopni
        } else {
            animal.move(direction, this); // Standardowy ruch
        }

        Vector2d newPosition = animal.getPosition();

        if (!oldPosition.equals(newPosition)) {
            if (animals.get(oldPosition).size() != 1) {
                for (int i=0;i<animals.get(oldPosition).size();i++) {
                    if (animals.get(oldPosition).get(i).equals(animal)) {
                        animals.get(oldPosition).remove(i);
                    }
                }
            }
            else {
                animals.get(oldPosition).remove(0);
                animals.remove(oldPosition);
            }

            if (animals.containsKey(newPosition)) {
                animals.get(newPosition).add(animal);
            }
            else {
                animals.put(newPosition,new ArrayList<Animal>());
                animals.get(newPosition).add(animal);
            }

            // Usuwanie roślin po wejściu zwierzęcia na pole
            Plant plant = plants.get(newPosition);
            if (plant != null) {
                System.out.println("Przed");
                System.out.println(animal.getEnergy());
                manager.plantConsume(animal,plant);
                System.out.println("Po");
                System.out.println(animal.getEnergy());
            }

            animal.incrementEnergy(-1);
//            manager.isAnimalAlive(animal);
            if (animal.getEnergy()<=0) {
                this.removeAnimal(animal.getPosition(),animal);
            }
            informObservers("Animal moved to: " + newPosition);
        }
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