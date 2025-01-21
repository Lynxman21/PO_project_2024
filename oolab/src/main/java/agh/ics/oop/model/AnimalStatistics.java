package agh.ics.oop.model;

import java.util.List;

public class AnimalStatistics {
    private int energy;
    private int lifeLen;
    private int childrenCount;
    private List<MoveDirection> moves; // Genotyp

    public AnimalStatistics(int energy, List<MoveDirection> moves) {
        this.energy = energy;
        this.lifeLen = 0;
        this.childrenCount = 0;
        this.moves = moves;
    }

    public int getEnergy() {
        return energy;
    }

    public void incrementEnergy(int increment) {
        this.energy += increment;
    }

    public int getLifeLen() {
        return lifeLen;
    }

    public void incrementLifeLen() {
        this.lifeLen++;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void incrementChildrenCount() {
        this.childrenCount++;
    }

    public List<MoveDirection> getMoves() {
        return moves;
    }

    public void setMoves(List<MoveDirection> moves) {
        this.moves = moves;
    }

    @Override
    public String toString() {
        return "Energy: " + energy + ", Life Length: " + lifeLen + ", Children: " + childrenCount + ", Moves: " + moves;
    }

    public void updateLifeLen(int lifeLen) {
        this.lifeLen = lifeLen;
    }
}
