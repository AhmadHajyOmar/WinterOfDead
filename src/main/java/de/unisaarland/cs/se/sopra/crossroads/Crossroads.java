package de.unisaarland.cs.se.sopra.crossroads;


import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Survivor;

public abstract class Crossroads {

    private final int id;
    private final Consequence cone;
    private final CrossroadType crossroadType;
    private boolean usecrossroad;

    public Crossroads(final int id, final Consequence consequence,
                      final CrossroadType crossroadType) {
        this.id = id;
        this.cone = consequence;
        this.crossroadType = crossroadType;
        this.usecrossroad = false;

    }

    public abstract void accept(ConsequenceVisitor visitor);

    public abstract void trigger(Survivor survivor, ConsequenceVisitor consequenceVisitor,
                                 Model model);

    public Consequence getConsequence() {
        return this.cone;
    }


    public int getID() {
        return this.id;
    }

    public CrossroadType getCrossroadType() {
        return crossroadType;
    }


    public void crossroadUsed() {
        this.usecrossroad = true;
    }

    public boolean isUsecrossroad() {
        return this.usecrossroad;
    }



    @Override
    public int hashCode() {

        return this.getID();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Crossroads) {
            return ((Crossroads) o).getID() == this.getID();
        }
        return false;
    }


}



