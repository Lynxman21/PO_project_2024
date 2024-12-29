package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;

import java.util.HashMap;
import java.util.Vector;

public class EarthMap extends AbstractWorldMap{

    protected static final Vector2d LEFT_DOWN = new Vector2d(0,0);

    //bo każda mapa może mieć różne granice
    private final int height;
    private final int width;
    private final Vector2d leftDown;
    private final Vector2d rightUp;

    public EarthMap(int width, int height) {
        super(new HashMap<>());
        this.width = width;
        this.height = height;
        this.leftDown = new Vector2d(0,0);
        this.rightUp = new Vector2d(this.width-1,this.height-1);
    }

    public boolean canMoveTo(Vector2d position) {
        return position.follows(LEFT_DOWN) && !isOccupied(position) && position.precedes(rightUp);
    }

    @Override
    public void move(Animal animal, MoveDirection direction) {
        Vector2d oldPosition = animal.getPosition();
        if (animal.getDirection() == MapDirection.NORTH && direction == MoveDirection.FORWARD && animal.getPosition().getY()==height-1 && oldPosition.equals(animal.getPosition())) {
            animal.rotate(2);
        }
        else if (animal.getDirection() == MapDirection.SOUTH && direction == MoveDirection.FORWARD && animal.getPosition().getY()==0 && oldPosition.equals(animal.getPosition())) {
            animal.rotate(2);
        }
        else if (oldPosition.upperRight(rightUp).getX() == width && oldPosition.getY() < height && oldPosition.getY() >= 0 && direction.equals(MoveDirection.FORWARD) && animal.getDirection().equals(MapDirection.EAST)) {
            animal.setPosition(new Vector2d(0,oldPosition.getY()));
        }
        else if (oldPosition.lowerLeft(LEFT_DOWN).getX() == 0 && oldPosition.getY() < height && oldPosition.getY() >= 0 && direction.equals(MoveDirection.FORWARD) && animal.getDirection().equals(MapDirection.WEST)) {
            animal.setPosition(new Vector2d(width-1,oldPosition.getY()));
        }
        else {
            animal.move(direction, this);
        }
        if (!oldPosition.equals(animal.getPosition())) {
            animals.remove(oldPosition);
            animals.put(animal.getPosition(),animal);
            informObservers("Animal moved to: " + animal.getPosition());
        }
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(leftDown,rightUp);
    }

    @Override
    public Animal objectAt(Vector2d position) {
        return animals.get(position);
    }
}
