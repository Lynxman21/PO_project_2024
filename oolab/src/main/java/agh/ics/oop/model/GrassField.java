package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static java.lang.Math.sqrt;

public class GrassField extends AbstractWorldMap{
    private final int grassFurther;
    private final HashMap<Vector2d,Grass> grasses;

    public GrassField(int grassPoints) {
        super(new HashMap<>());
        this.grasses = new HashMap<>();
        this.grassFurther = (int) sqrt(grassPoints*10);
        Random random = new Random();
        Vector2d vect;

        while (grasses.size() != grassPoints) {
            int x = random.nextInt(grassFurther);
            int y = random.nextInt(grassFurther);

            vect = new Vector2d(x,y);
            if (!grasses.containsKey(vect)) {
                grasses.put(vect,new Grass(vect));
            }
        }
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return super.isOccupied(position) || grasses.containsKey(position);
    }

    @Override
    public Boundary getCurrentBounds() {
        Vector2d rightUp = new Vector2d(0,0);
        Vector2d leftBottom = new Vector2d(grassFurther,grassFurther);
        for (Vector2d key:grasses.keySet()) {
            rightUp = rightUp.upperRight(key);
            leftBottom = leftBottom.lowerLeft(key);
        }

        for (Vector2d key:animals.keySet()) {
            rightUp = rightUp.upperRight(key);
            leftBottom = leftBottom.lowerLeft(key);
        }

        return new Boundary(leftBottom,rightUp);
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        if (animals.containsKey(position)) return animals.get(position);
        if (grasses.containsKey(position)) return grasses.get(position);
        return null;
    }

    public boolean canMoveTo(Vector2d position) {
        return !animals.containsKey(position);
    }

    @Override
    public List<WorldElement> getElements() {
        List<WorldElement> elements = super.getElements();
        elements.addAll(grasses.values());
        return elements;
    }
}
