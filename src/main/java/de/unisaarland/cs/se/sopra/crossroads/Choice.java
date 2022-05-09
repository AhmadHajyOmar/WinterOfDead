package de.unisaarland.cs.se.sopra.crossroads;

public class Choice extends Consequence {

    private final Consequence consequence1;
    private final Consequence consequence2;

    public Choice(final Consequence consequenceOne, final Consequence consequenceTwo) {
        this.consequence1 = consequenceOne;
        this.consequence2 = consequenceTwo;
    }

    @Override
    public void accept(final ConsequenceVisitor visitor) {
        visitor.visit(this);
    }

    public Consequence getConsequence1() {
        return this.consequence1;
    }

    public Consequence getConsequence2() {
        return this.consequence2;
    }
}
