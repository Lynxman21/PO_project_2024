package agh.ics.oop.model.util;

import agh.ics.oop.model.Vector2d;

import java.util.*;

public class PositionGenerator {
    private final int mapWidth;
    private final int mapHeight;
    private final Random random;

    public PositionGenerator(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.random = new Random();
    }

    public List<Vector2d> generateUniquePositions(int count) {
        Set<Vector2d> positions = new HashSet<>();

        while (positions.size() < count) {
            int x = random.nextInt(mapWidth);
            int y = random.nextInt(mapHeight);

            Vector2d position = new Vector2d(x, y);
            if (!positions.contains(position)) {
                positions.add(position);
            }
        }

        return new ArrayList<>(positions);

    }
}
