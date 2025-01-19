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
    public void move(int direction, MoveValidator validator, int mapWidth, int mapHeight) {
        // Obrót zwierzaka
        for (int i = 0; i < direction; i++) {
            this.direction = this.direction.next();
        }

        // Oblicz nową pozycję
        Vector2d newPosition = this.position.add(this.direction.toUnitVector());

        // Wrapowanie w poziomie
        if (newPosition.getX() >= mapWidth) {
            newPosition = new Vector2d(0, newPosition.getY());
        } else if (newPosition.getX() < 0) {
            newPosition = new Vector2d(mapWidth - 1, newPosition.getY());
        }

        // Odbijanie w pionie
        if (newPosition.getY() >= mapHeight || newPosition.getY() < 0) {
            this.direction = this.direction.opposite(); // Obrót o 180°
            newPosition = this.position; // Zwierzak zostaje w miejscu
        }

        // Sprawdź, czy nowa pozycja jest dostępna
        if (validator.canMoveTo(newPosition)) {
            this.position = newPosition;
        }

        // Zmniejszenie energii
        this.energy--;

        // Informacja debugowa
        System.out.println("Animal moved to " + this.position + ", remaining energy: " + this.energy);
    }





}