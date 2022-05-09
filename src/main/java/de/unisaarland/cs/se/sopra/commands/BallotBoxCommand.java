package de.unisaarland.cs.se.sopra.commands;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.EndGameState;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;

public abstract class BallotBoxCommand extends Command {


    public BallotBoxCommand(final int commId) {
        super(commId);
    }

    @Override
    protected boolean checkPreCondition(final Model model, final ConnectionWrapper connection) {
        if (model.getGameState().canRegister()) {
            model.setGameState(EndGameState.getInstance());
            connection.sendRegistrationAborted();
            return false;
        }

        if (!model.getGameState().canVote()) {
            connection.sendCommandFailed(getCommId(), "Wrong Phase");
            return false;
        }
        final Player player = model.getPlayerByCommId(super.getCommId());
        if (player.hasLeft()) {
            connection.sendCommandFailed(super.getCommId(), "go out Grandpa :)");
            return false;
        }
        //player check
        if (!model.getCurrentPlayer().equals(model.getPlayerByCommId(super.getCommId()))) {
            connection.sendCommandFailed(super.getCommId(), "you have wait dude :D :)");
            return false;

        }


        return true;
    }


}
