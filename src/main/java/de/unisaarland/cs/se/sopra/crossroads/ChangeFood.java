package de.unisaarland.cs.se.sopra.crossroads;

public class ChangeFood extends Consequence {

    private final int foodamount;

    public ChangeFood(final int food) {
        this.foodamount = food;
    }

    @Override
    public void accept(final ConsequenceVisitor visitor) {
        visitor.visit(this);
    }

    public int getFoodamount() {
        return this.foodamount;
    }
}
