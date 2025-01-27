package agh.ics.oop.model;

public class ConsoleMapDisplay implements MapChangeListener { // czy to jest używane?
    private int totalCounter = 0;

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        synchronized (System.out) {
            totalCounter++;
            System.out.println(worldMap.getId());
            System.out.println(message);
            System.out.println(worldMap);
            System.out.println("Total messages: " + totalCounter);
        }
    }
}
