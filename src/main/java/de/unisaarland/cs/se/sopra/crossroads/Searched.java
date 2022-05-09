package de.unisaarland.cs.se.sopra.crossroads;

import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Survivor;

import java.util.Optional;

public class Searched extends Crossroads {
    private final Optional<Integer> locationID;

    public Searched(final int id, final Consequence consequence,
                    final Optional<Integer> locationID) {
        super(id, consequence, CrossroadType.SEARCHED);
        this.locationID = locationID;
    }

    @Override
    public void accept(final ConsequenceVisitor visitor) {
        this.getConsequence().accept(visitor);
    }

    @Override
    public void trigger(final Survivor survivor, final ConsequenceVisitor consequenceVisitor,
                        final Model model) {
        if (model.getGameState().inGame()) {

            if (!this.isUsecrossroad()) {

                if (!this.getLocationID().isEmpty()) {
                    final int locationId = survivor.getLocation().getId();

                    if (this.getLocationID().get() == locationId) {
                        consequenceVisitor.getConnection().sendCrossroad(this.getID());
                        this.accept(consequenceVisitor);
                        this.crossroadUsed();

                    }
                } else {
                    consequenceVisitor.getConnection().sendCrossroad(this.getID());
                    this.accept(consequenceVisitor);
                    this.crossroadUsed();

                }

            }
        }

    }

    public Optional<Integer> getLocationID() {
        return this.locationID;
    }
}
