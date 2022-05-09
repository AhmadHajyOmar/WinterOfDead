package de.unisaarland.cs.se.sopra.crossroads;

public abstract class Consequence {
    public abstract void accept(ConsequenceVisitor visitor);
}
