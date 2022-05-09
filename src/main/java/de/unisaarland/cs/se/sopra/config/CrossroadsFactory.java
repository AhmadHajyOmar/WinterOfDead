package de.unisaarland.cs.se.sopra.config;

public interface CrossroadsFactory<T> {
    T createCrossroads(int id, String name, ParamMap params, String consequenceName,
                       ParamMap paramConsequence);

}
