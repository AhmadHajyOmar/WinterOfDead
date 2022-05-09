package de.unisaarland.cs.se.sopra.config;

public interface ConsequenceFactory<T> {

    T createConsequence(String name, ParamMap params);
}
