package de.unisaarland.cs.se.sopra.crossroads;

public class ChangeMoral extends Consequence {

    private final int amount;

    public ChangeMoral(final int amount) {
        this.amount = amount;
    }

    @Override
    public void accept(final ConsequenceVisitor visitor) {
        visitor.visit(this);
    }

    public int getAmount() {
        return this.amount;
    }
}
