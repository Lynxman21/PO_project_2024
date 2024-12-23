package agh.ics.oop;

import agh.ics.oop.model.*;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class World {
    public static void main(String[] args) {
        System.out.println("System rozpocza dzialanie");
//        try {
//            List<Vector2d> positions = List.of(new Vector2d(2, 9));
//            List simulations = new ArrayList<>();
//            for (int i=0;i<500;i++) {
//                List<MoveDirection> directions = OptionsParser.parse(Arrays.asList(args));
//                AbstractWorldMap map1 = new GrassField(10);
//                AbstractWorldMap map2 = new RectangularMap(10,10);
//                map1.addObserver(new ConsoleMapDisplay());
//                map2.addObserver(new ConsoleMapDisplay());
//                simulations.add(new Simulation(positions, directions, map1));
//                simulations.add(new Simulation(positions, directions, map2));
//            }
//            SimulationEngine engine = new SimulationEngine(simulations);
////          engine.runSync();
////          engine.runAsync();
//            engine.runAsyncInThreadPool();
//        } catch (IllegalArgumentException e) {
//            System.out.println("Error: " + e.getMessage());
//        }
        Application.launch(SimulationApp.class, args);
        System.out.println("System zakonczyl dzialanie");
    }
    public static void run(List<MoveDirection> arr) {
        for (MoveDirection move:arr) {
            switch (move) {
                case LEFT -> System.out.println("Zwierzak skreca w lewo");
                case RIGHT -> System.out.println("Zwierzak skreca w prawo");
                case BACKWARD -> System.out.println("Zwierzak idzie do tylu");
                case FORWARD -> System.out.println("Zwierzak idzie do przodu");
            }
        }
    }
}