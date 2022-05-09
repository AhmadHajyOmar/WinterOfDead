package de.unisaarland.cs.se.sopra.crossroads;

import java.util.Optional;

public class SpawnZombies extends Consequence {

    private final int numberOfZombies;
    private final Optional<Integer> locationId;

    public SpawnZombies(final int numOfZombies, final Optional<Integer> locationID) {
        this.numberOfZombies = numOfZombies;
        this.locationId = locationID;
    }

    @Override
    public void accept(final ConsequenceVisitor visitor) {
        visitor.visit(this);
    }

    public int getNumberOfZombies() {
        return numberOfZombies;
    }

    public Optional<Integer> getLocationId() {
        return locationId;
    }
}
