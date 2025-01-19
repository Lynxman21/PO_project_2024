package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animal implements WorldElement {
    private static final Vector2d LEFT_BOTTOM_CORNER = new Vector2d(0,0);
    private static final Vector2d RIGHT_UP_CORNER = new Vector2d(4,4);

    private MapDirection direction;
    private Vector2d position;
    private int energy;
    private List<MoveDirection> moves;


    public Animal(Vector2d position,int energy) {
        this.direction = MapDirection.NORTH;
        this.position = position;
        this.energy = energy;
        this.moves = new ArrayList<>();
    }

    public void setMoves(List<MoveDirection> moves) {
        this.moves = moves;
    }

    public int getEnergy() {
        return energy;
    }

    public void incrementEnergy(int increment) {
        this.energy += increment;
    }

    public List<MoveDirection> getMoves() {
        return moves;
    }

    public Animal() {
        this(new Vector2d(2,2),10);
    }

    @Override
    public String toString() {
        return this.direction.toString();
    }
    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    public MapDirection getDirection() {
        return direction;
    }

    public Vector2d getPosition() {
        return position;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public void rotate(int count) {
        for (int index=0;index<count;index++) {
            direction = direction.next();
        }
    }

    //za pomocą tych funkcji co były np na lewo czy na prawo
    public void move(MoveDirection direction, MoveValidator validator) {
        Vector2d newPosition = this.position; // Domyślna nowa pozycja

        switch (direction) {
            case RIGHT -> this.direction = this.direction.next(); // Obrót w prawo
            case LEFT -> this.direction = this.direction.previous(); // Obrót w lewo
            case FORWARD -> {
                Vector2d forwardVector = this.direction.toUnitVector();
                newPosition = this.position.add(forwardVector);
            }
            case BACKWARD -> {
                Vector2d backwardVector = this.direction.toUnitVector().opposite();
                newPosition = this.position.add(backwardVector);
            }
        }

        // Sprawdź, czy nowa pozycja jest dozwolona przez mapę
        if (validator.canMoveTo(newPosition)) {
            this.position = newPosition; // Aktualizacja pozycji
        }
    }


}