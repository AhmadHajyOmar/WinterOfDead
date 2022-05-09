package de.unisaarland.cs.se.sopra.commands;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;

public class EndTurnCommand extends InGameCommand {

    public EndTurnCommand(final int commId) {
        super(commId);
    }

    @Override
    public void run(final Model model, final ConnectionWrapper connection) {

        final Player player = model.getCurrentPlayer();
        player.tick();
        model.nextPlayer();
        //chnged
        //draw a crossroad, it's ur turn dude :)
        if (model.getPlayers().contains(model.getCurrentPlayer())) {
            final Player firstPlayer = model.getPlayers().get(0);
            if (!(model.getCurrentPlayer().equals(firstPlayer))) {
                model.drawCrossroads(model.getCurrentPlayer());
            }
        }
    }

}
