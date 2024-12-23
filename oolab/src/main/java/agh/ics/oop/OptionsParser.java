package agh.ics.oop;

import agh.ics.oop.model.MoveDirection;

import java.util.ArrayList;
import java.util.List;

public class OptionsParser {
    public static List<MoveDirection> parse(List<String> arr) {
        ArrayList<MoveDirection> ans = new ArrayList<>();

        //Wszytkie sytuacje obsłóżone tylko dla return lub przypisania
        for (String val:arr) {
            switch (val){
                case "f" -> ans.add(MoveDirection.FORWARD);
                case "b" -> ans.add(MoveDirection.BACKWARD);
                case "l" -> ans.add(MoveDirection.LEFT);
                case "r" -> ans.add(MoveDirection.RIGHT);
                default -> throw new IllegalArgumentException(val + "is not legal move specification");
            }
        }
        return ans;
    }
}
