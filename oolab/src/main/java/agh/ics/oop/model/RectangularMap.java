//package agh.ics.oop.model;
//
//import agh.ics.oop.model.util.Boundary;
//import agh.ics.oop.model.util.MapVisualizer;
//
//import java.util.*;
//
//public class RectangularMap extends AbstractWorldMap{
//    protected static final Vector2d LEFT_DOWN = new Vector2d(0,0);
//
//    //bo każda mapa może mieć różne granice
//    private final int height;
//    private final int width;
//    private final Vector2d leftDown;
//    private final Vector2d rightUp;
//
//    public RectangularMap(int width, int height) {
//        super(new HashMap<>());
//        this.width = width;
//        this.height = height;
//        this.leftDown = new Vector2d(0,0);
//        this.rightUp = new Vector2d(this.width-1,this.height-1);
//    }
//
//    public boolean canMoveTo(Vector2d position) {
//        return position.follows(LEFT_DOWN) && !isOccupied(position) && position.precedes(rightUp);
//    }
//
//    @Override
//    public Boundary getCurrentBounds() {
//        return new Boundary(leftDown,rightUp);
//    }
//
//    @Override
//    public Animal objectAt(Vector2d position) {
//        return animals.get(position);
//    }
//}
