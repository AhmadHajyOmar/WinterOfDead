package de.unisaarland.cs.se.sopra.abilities;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.crossroads.CrossroadType;
import de.unisaarland.cs.se.sopra.crossroads.Crossroads;
import de.unisaarland.cs.se.sopra.model.Location;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;
import de.unisaarland.cs.se.sopra.model.Survivor;

import java.util.Optional;

public class CleanWasteVisitor extends PassiveAbilityVisitor {

    private static final int STANDARD_TRASH = 3;

    public CleanWasteVisitor(final Model model, final ConnectionWrapper connection,
                             final Player player, final Survivor survivor) {
        super(model, connection, player, survivor);
    }

    @Override
    public void executeStandardAction() {
        final Optional<Integer> die = getPlayer().getHighestDie();
        if (die.isPresent()) {
            if (cleanWaste(STANDARD_TRASH)) {
                getPlayer().removeLowestDie();
                triggerWastChanged();
            }
        } else {
            getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                    "No die left!");
        }
    }

    @Override
    public void visit(final TrashAbility trashAbility) {
        if (cleanWaste(trashAbility.getAmount())) {
            triggerWastChanged();
        }
    }

    @Override
    public void visit(final SwabAbility swabAbility) {
        if (swabAbility.hasUsesLeft()) {
            if (cleanWaste(swabAbility.getAmount())) {
                swabAbility.used();
                //changed
                triggerWastChanged();

            }
        } else {
            swabAbility.getParent().accept(this);
        }
    }

    /**
     * @param amount Amount of waste to clean.
     * @return Whether cleaning the waste succeeded.
     */
    private boolean cleanWaste(final int amount) {
        final Optional<Integer> die = getPlayer().getHighestDie();
        // Here we need to check whether we have a die left, too! This was broken :(
        if (die.isEmpty()) {
            getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                    "No die left!");
            return false;
        }

        final Location location = getSurvivor().getLocation();
        if (location.equals(getModel().getColony()) && getModel().getTrashPile() != 0) {

            final int removedTrash = getModel().removeTrash(amount);
            getConnection().sendWasteChanged(-removedTrash);

            return true;
        }
        final int commId = getModel().getCommId(getPlayer().getId());
        getConnection().sendCommandFailed(commId, "Cannot remove cards from trash pile");
        return false;
    }

    private void triggerWastChanged() {
        final Crossroads crossroad = getPlayer().getCrossroad();
        //final Crossroads crossroad = getModel().getCurrentPlayer().getCrossroad();

        if (crossroad.getCrossroadType() == CrossroadType.WASTECHANGED) {
            crossroad.trigger(getSurvivor(), getConsequenceVisitor(), getModel());
        }
    }



}
