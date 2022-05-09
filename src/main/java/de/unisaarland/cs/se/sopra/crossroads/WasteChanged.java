package de.unisaarland.cs.se.sopra.crossroads;

import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Survivor;

public class WasteChanged extends Crossroads {

    private final int amount;

    public WasteChanged(final int id, final Consequence consequence, final int amount) {
        super(id, consequence, CrossroadType.WASTECHANGED);
        this.amount = amount;
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

                final int amountchanged = model.getTrashPile();
                check1(consequenceVisitor, model, amountchanged);
                if (model.getCurrentPlayer().getOldTrash() <= this.getAmount()) {
                    if (amountchanged == model.getCurrentPlayer().getOldTrash()
                            + this.getAmount()) {
                        consequenceVisitor.getConnection().sendCrossroad(this.getID());
                        this.accept(consequenceVisitor);
                        this.crossroadUsed();
                        return;
                    }
                } else if (this.getAmount() == 0) {

                    consequenceVisitor.getConnection().sendCrossroad(this.getID());
                    this.accept(consequenceVisitor);
                    this.crossroadUsed();
                    return;


                } else {

                    check3(consequenceVisitor, model, amountchanged);

                }



            }
        }

    }


    public int getAmount() {
        return this.amount;
    }

    private void run(final ConsequenceVisitor consequenceVisitor) {

        consequenceVisitor.getConnection().sendCrossroad(this.getID());
        this.accept(consequenceVisitor);
        this.crossroadUsed();

    }

    private void check1(final ConsequenceVisitor consequenceVisitor, final Model model,
                        final int amount) {
        if (this.getAmount() < 0) {
            if (amount == model.getCurrentPlayer().getOldTrash()
                    + this.getAmount()) {
                run(consequenceVisitor);
            }
        }

    }


    private void check3(final ConsequenceVisitor consequenceVisitor, final Model model,
                        final int amount) {
        final int amoutToDec = model.getCurrentPlayer().getOldTrash()
                - this.getAmount();
        if (amount == model.getCurrentPlayer().getOldTrash() - amoutToDec) {
            consequenceVisitor.getConnection().sendCrossroad(this.getID());
            this.accept(consequenceVisitor);
            this.crossroadUsed();
        }

    }

}
