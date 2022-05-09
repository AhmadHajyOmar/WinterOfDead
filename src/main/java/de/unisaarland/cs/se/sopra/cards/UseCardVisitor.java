package de.unisaarland.cs.se.sopra.cards;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.crossroads.CrossroadType;
import de.unisaarland.cs.se.sopra.crossroads.Crossroads;
import de.unisaarland.cs.se.sopra.model.Colony;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;
import sopra.comm.FoodChange;

import java.util.Optional;

public class UseCardVisitor extends CardVisitor {

    public UseCardVisitor(final Model model, final ConnectionWrapper connection,
                          final Player player, final int commId) {
        super(model, connection, player, commId);
    }

    @Override
    public void visit(final Food card) {
        final Colony colony = getModel().getColony();
        colony.addFood(card.getFood());
        //Changed
        getModel().addTrash();
        getPlayer().removeCard(card);
        getConnection().sendCardUsed(card.getId());
        getConnection().sendWasteChanged(1);
        getConnection().sendFoodChanged(card.getFood(), FoodChange.FOOD_ADDED);
        //Changed
        triggerWasteChanged();
    }

    @Override
    public void visit(final Stuff card) {
        final Player player = getPlayer();
        final Optional<Integer> optionalDie = player.getHighestDie();
        if (optionalDie.isPresent()) {
            player.removeLowestDie();
            final int die = getModel().rollActionDie();
            player.addDie(die);
            getPlayer().removeCard(card);
            getModel().addTrash();
            getConnection().sendCardUsed(card.getId());
            getConnection().sendWasteChanged(1);
            getConnection().sendDieRolled(player.getId(), die);
            //Changed
            triggerWasteChanged();
        } else {
            getConnection().sendCommandFailed(getModel().getCommId(player.getId()), "No die left!");
        }
    }

    private void triggerWasteChanged() {
        final Crossroads crossroad = getPlayer().getCrossroad();

        if (crossroad.getCrossroadType() == CrossroadType.WASTECHANGED) {
            crossroad.trigger(getModel().getRandomSurvivor(getPlayer()), getConsequenceVisitor(),
                    getModel());
        }
    }


}
