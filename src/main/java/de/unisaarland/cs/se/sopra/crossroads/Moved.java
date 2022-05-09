package de.unisaarland.cs.se.sopra.crossroads;

import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Survivor;

import java.util.Optional;

public class Moved extends Crossroads {
    private final Optional<Integer> locationID;

    public Moved(final int id, final Consequence consequence, final Optional<Integer> locationID) {
        super(id, consequence, CrossroadType.MOVED);
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

            //check if the card not yet used
            if (!this.isUsecrossroad()) {
                if (!this.getLocationID().isEmpty()) {
                    if (this.getLocationID().get() == survivor.getLocation().getId()) {
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
