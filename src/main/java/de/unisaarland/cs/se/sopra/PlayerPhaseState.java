package de.unisaarland.cs.se.sopra;

import de.unisaarland.cs.se.sopra.abilities.WoundingAbilityVisitor;
import de.unisaarland.cs.se.sopra.commands.Command;
import de.unisaarland.cs.se.sopra.commands.KillSurvivorCommand;
import de.unisaarland.cs.se.sopra.commands.LeaveCommand;
import de.unisaarland.cs.se.sopra.crossroads.ConsequenceVisitor;
import de.unisaarland.cs.se.sopra.model.Crisis;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;
import de.unisaarland.cs.se.sopra.model.Survivor;
import java.util.List;
import sopra.comm.TimeoutException;

public final class PlayerPhaseState extends State {

    private static PlayerPhaseState instance;

    private PlayerPhaseState() {
        // Do nothing
    }

    public static PlayerPhaseState getInstance() {
        if (instance == null) {
            instance = new PlayerPhaseState();
        }
        return instance;
    }

    @Override
    public boolean inGame() {
        return true;
    }

    @Override
    public void run(final Model model, final ConnectionWrapper connection) {

        startNextRound(model, connection);
        if (model.getInTheMidleOfTheGame()) {
            triggerTheChosenConsequence(model, connection, model.getCurrentPlayer(),
                    model.getCommId(model.getCurrentPlayer().getId()));
        }
        Player player;
        while (model.getGameState().gameRunning()
                && (player = model.getCurrentPlayer()) != null) {
            if (player.hasLeft()) {
                model.nextPlayer();
                //chnged
                //draw a crossroad, it's ur turn dude :)
                /*if (model.getPlayers().contains(model.getCurrentPlayer())) {
                    //model.drawCrossroads(model.getCurrentPlayer());

                    continue;
                }*/
            }

            player.setOldTrash(model.getTrashPile());

            processFrostbites(player, model, connection);
            while (model.isCurrentPlayer(player.getId()) && model.getGameState().gameRunning()) {
                connection.sendActNow(model.getCommId(player.getId()));
                handleCommand(model, player, connection);
            }
            if (!model.getGameState().gameRunning()) {
                break;
            }
        }

        if (model.getGameState().gameRunning()) {
            model.setGameState(ColonyPhaseState.getInstance());
        }
    }

    private void startNextRound(final Model model, final ConnectionWrapper connection) {

        if (!model.getInTheMidleOfTheGame()) {
            connection.sendNextRound(model.getRounds());
            model.nextCrisis();
            final Crisis crisis = model.getCrisis();
            connection.sendCrisis(crisis.getId());

            //Changed


            final List<Player> sortedPlayers = model.getPlayers();
            for (final Player player : sortedPlayers) {
                if (player.hasLeft()) {
                    continue;
                }
                final int numSurvivors = player.getSurvivors().size();
                for (int i = 0; i <= numSurvivors; i++) {
                    final int die = model.rollActionDie();
                    player.addDie(die);
                    connection.sendDieRolled(player.getId(), die);
                }
            }
            model.setCurrentPlayer(0);
            //draw crossroad for the first player
            model.drawCrossroads(model.getPlayers().get(0));
        }
    }

    private void triggerTheChosenConsequence(final Model model, final ConnectionWrapper connection,
                                             final Player currentPlayer, final int commId) {
        if (model.getInTheMidleOfTheGame()) {
            model.getChosenConsequence().accept(new ConsequenceVisitor(model, connection,
                    currentPlayer, commId));
        }
    }

    private void handleCommand(
            final Model model, final Player player, final ConnectionWrapper connection) {
        final Command command = nextCommand(model, player, connection);
        command.execute(model, connection);
        if (!command.checkPlayer(model.getCommId(player.getId()))) {
            handleCommand(model, player, connection);
        }
    }

    private Command nextCommand(
            final Model model, final Player player, final ConnectionWrapper connection) {
        try {
            return connection.nextCommand();
        } catch (final TimeoutException e) {
            return new LeaveCommand(model.getCommId(player.getId()));
        }
    }

    /**
     * Frostbites turn into more wounds.
     *
     * @param player     The player which survivors get checked.
     * @param model      To possibly kill the survivor.
     * @param connection To inform all players.
     */
    private void processFrostbites(
            final Player player, final Model model, final ConnectionWrapper connection) {
        for (final Survivor s : player.getSurvivors()) {
            for (int i = 0; i < s.getTimesFrostbitten(); i++) {
                s.addWound();
                connection.sendWounded(s.getId());
                if (s.getTotalWounds() >= WoundingAbilityVisitor.MAX_TIMES_WOUNDED) {
                    final KillSurvivorCommand command =
                            new KillSurvivorCommand(model.getCommId(player.getId()), player, s);
                    command.execute(model, connection);
                    if (!model.getGameState().gameRunning()) {
                        return;
                    }
                    break;
                }
            }
        }
    }
}
