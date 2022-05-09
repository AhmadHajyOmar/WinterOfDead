package de.unisaarland.cs.se.sopra;

import de.unisaarland.cs.se.sopra.commands.Command;
import de.unisaarland.cs.se.sopra.commands.LeaveCommand;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;
import sopra.comm.TimeoutException;


public final class  VotePhaseState extends State {

    private static VotePhaseState instance;


    private VotePhaseState() {


    }

    public static VotePhaseState getInstance() {
        if (instance == null) {
            instance = new VotePhaseState();
        }
        return instance;
    }

    @Override
    public boolean canVote() {
        return true;
    }


    @Override
    public void run(final Model model, final ConnectionWrapper connection) {
        final Player player = model.getCurrentPlayer();
        vote(model, connection);
        model.setCurrentPlayer(player.getId());

        model.setInTheMidleOfTheGame(true);
        if (model.getVoteResult()) {
            model.resetVoteForAllPlayers();
            model.setChosenConsequence(model.getConsequence1());
            connection.sendVoteResult(model.getVoteResult());
            model.setGameState(PlayerPhaseState.getInstance());
            model.resetVots();
            model.getGameState().run(model, connection);
            return;
        }
        model.resetVoteForAllPlayers();
        model.setChosenConsequence(model.getConsequence2());
        connection.sendVoteResult(model.getVoteResult());
        model.setGameState(PlayerPhaseState.getInstance());
        model.resetVots();
        model.getGameState().run(model, connection);

    }

    private void vote(final Model model, final ConnectionWrapper connection) {
        for (int i = 0; i < model.getPlayers().size(); i++) {
            final Player player = model.getPlayer(i);
            if (!player.hasLeft()) {
                while (!(player.getVoted())) {
                    model.setCurrentPlayer(player.getId());
                    connection.sendVoteNow(model.getCommId(player.getId()));
                    handleCommand(model, player, connection);

                }

            }
        }
    }


    private void handleCommand(
            final Model model, final Player player, final ConnectionWrapper connection) {
        final Command command = nextCommand(model, player, connection);
        command.execute(model, connection);
    }

    private Command nextCommand(
            final Model model, final Player player, final ConnectionWrapper connection) {
        try {
            return connection.nextCommand();
        } catch (final TimeoutException e) {
            return new LeaveCommand(model.getCommId(player.getId()));
        }
    }




}



