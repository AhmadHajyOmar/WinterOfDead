package de.unisaarland.cs.se.sopra.crossroads;

import java.util.Optional;

public class SpawnSurvivors extends Consequence {

    private final int numberOfSurvivors;
    private final Optional<Boolean> children;

    public SpawnSurvivors(final int numOfSurvivors, final Optional<Boolean> child) {
        this.numberOfSurvivors = numOfSurvivors;
        this.children = child;
    }

    @Override
    public void accept(final ConsequenceVisitor visitor) {
        visitor.visit(this);
    }


    public int getNumberOfSurvivors() {
        return this.numberOfSurvivors;
    }

    public Optional<Boolean> getChildren() {
        return this.children;
    }
}