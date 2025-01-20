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
        this.moves = generateDefaultMoves(); // Dodaj tę linię
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

    public void setDirection(MapDirection direction) {
        this.direction = direction;
    }

    private List<MoveDirection> generateDefaultMoves() {
        Random random = new Random();
        List<MoveDirection> moves = new ArrayList<>();
        int movesCount = random.nextInt(10) + 5; // Długość genotypu od 5 do 15

        for (int i = 0; i < movesCount; i++) {
            moves.add(MoveDirection.values()[random.nextInt(MoveDirection.values().length)]);
        }

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

        // Odbijanie w pionie (góra-dół)
        if (newPosition.getY() >= mapHeight || newPosition.getY() < 0) {
            this.direction = this.direction.opposite(); // Obrót o 180°
            newPosition = this.position.add(this.direction.toUnitVector()); // Oblicz nową pozycję po odbiciu
        }

        // Wrapowanie w poziomie (lewo-prawo)
        if (newPosition.getX() >= mapWidth) {
            newPosition = new Vector2d(0, newPosition.getY()); // Przejście na lewą krawędź
        } else if (newPosition.getX() < 0) {
            newPosition = new Vector2d(mapWidth - 1, newPosition.getY()); // Przejście na prawą krawędź
        }

        // Sprawdź, czy nowa pozycja jest dostępna
        if (validator.canMoveTo(newPosition)) {
            this.position = newPosition;
        }

        // Zmniejszenie energii
        this.energy--;

        // Debugowanie
        System.out.println("Animal moved to " + this.position + ", remaining energy: " + this.energy);
    }






    private int age = 0; // Wiek zwierzęcia
    private int childrenCount = 0; // Liczba dzieci

    public int getAge() {
        return age;
    }

    public void incrementAge() {
        age++;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void incrementChildrenCount() {
        childrenCount++;
    }


}