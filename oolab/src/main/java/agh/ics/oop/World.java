package agh.ics.oop;

import javafx.application.Application;

public class World {
    public static void main(String[] args) {
        System.out.println("System rozpocza dzialanie");
        Application.launch(SimulationApp.class, args);
        System.out.println("System zakonczyl dzialanie");
    }
}