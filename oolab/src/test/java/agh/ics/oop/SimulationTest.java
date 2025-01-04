//package agh.ics.oop;
//
//import agh.ics.oop.model.MapDirection;
//import agh.ics.oop.model.MoveDirection;
//import agh.ics.oop.model.RectangularMap;
//import agh.ics.oop.model.Vector2d;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class SimulationTest {
//    Vector2d vector = new Vector2d(1,2);
//
//    @Test
//    public void twoAnimalMovingCheckDirectionAndPosition() {
//        List<Vector2d> startPositions = new ArrayList<>(Arrays.asList(vector, new Vector2d(3,2)));
//        List<MoveDirection> directions = new ArrayList<>(Arrays.asList(MoveDirection.FORWARD,MoveDirection.FORWARD,MoveDirection.LEFT,MoveDirection.RIGHT,
//                MoveDirection.FORWARD,MoveDirection.BACKWARD));
//        RectangularMap map = new RectangularMap(5,5);
//
//        Simulation simulation = new Simulation(startPositions,directions,map);
//        simulation.run();
//
//        List<Vector2d> expectedPosition = new ArrayList<>(Arrays.asList(new Vector2d(0,3),new Vector2d(2,3)));
//        List<MapDirection> expectedDirections = new ArrayList<>(Arrays.asList(MapDirection.WEST,MapDirection.EAST));
//
//        for (int index=0;index<2;index++) {
//            assertTrue(simulation.isAtGoodPossisioned(index,expectedPosition.get(index)));
//            assertTrue(simulation.isGoodOriented(index,expectedDirections.get(index)));
//        }
//    }
//
//    @Test
//    public void oneAnimalWithStringList() {
//        List<String> input = new ArrayList<>(List.of("f", "f", "l","b"));
//        List<MoveDirection> directions = OptionsParser.parse(input);
//        List<Vector2d> startPositions = new ArrayList<>(Arrays.asList(vector));
//        RectangularMap map = new RectangularMap(5,5);
//
//        Simulation simulation = new Simulation(startPositions, directions,map);
//        simulation.run();
//
//        assertTrue(simulation.isAtGoodPossisioned(0,new Vector2d(2, 4)));
//        assertTrue(simulation.isGoodOriented(0,MapDirection.WEST));
//    }
//
//    @Test
//    public void outOfBorder() {
//        List<String> input = new ArrayList<>(List.of("r","f","l","f","f"));
//        List<MoveDirection> directions = OptionsParser.parse(input);
//        List<Vector2d> startPositions = new ArrayList<>(Arrays.asList(new Vector2d(4,3)));
//        RectangularMap map = new RectangularMap(5,5);
//
//        Simulation simulation = new Simulation(startPositions, directions,map);
//        simulation.run();
//
//        assertTrue(simulation.isAtGoodPossisioned(0,new Vector2d(4, 4)));
//    }
//}