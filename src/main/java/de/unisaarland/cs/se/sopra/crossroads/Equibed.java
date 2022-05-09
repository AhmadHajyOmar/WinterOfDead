package de.unisaarland.cs.se.sopra.crossroads;

import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Survivor;

public class Equibed extends Crossroads {

    public Equibed(final int id, final Consequence consequence) {
        super(id, consequence, CrossroadType.EQUIPED);
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
                consequenceVisitor.getConnection().sendCrossroad(this.getID());
                this.accept(consequenceVisitor);
                this.crossroadUsed();

            }
        }

    }
}
