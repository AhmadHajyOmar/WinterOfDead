package de.unisaarland.cs.se.sopra.crossroads;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.EndGameState;
import de.unisaarland.cs.se.sopra.VotePhaseState;
import de.unisaarland.cs.se.sopra.commands.KillSurvivorCommand;
import de.unisaarland.cs.se.sopra.model.Entrance;
import de.unisaarland.cs.se.sopra.model.Location;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;
import de.unisaarland.cs.se.sopra.model.Survivor;
import sopra.comm.FoodChange;
import sopra.comm.MoralChange;

import java.util.Optional;


public class ConsequenceVisitor {

    private final Model model;
    private final ConnectionWrapper connection;
    private final Player player;
    private final int commId;

    public ConsequenceVisitor(final Model model, final ConnectionWrapper connection,
                              final Player player, final int commId) {
        this.model = model;
        this.connection = connection;
        this.player = player;
        this.commId = commId;
    }

    public int getCommId() {
        return commId;
    }

    public Player getPlayer() {
        return player;
    }

    public ConnectionWrapper getConnection() {
        return connection;
    }

    public Model getModel() {
        return model;
    }

    public void visit(final ChangeFood consequence) {
        final int amount = consequence.getFoodamount();

        if (amount > 0) {
            model.getColony().addFood(amount);
            connection.sendFoodChanged(amount, FoodChange.CROSSROAD);
        }

        if (amount < 0) {
            model.getColony().addFood(amount);
            final int changed = model.getColony().getFood();

            if (changed >= 0) {
                connection.sendFoodChanged(amount, FoodChange.CROSSROAD);
            } else {
                model.getColony().increaseStarvationToken();
                connection.sendStarvationTokenAdded();
            }


        }

        /*if (amount < 0) {
            final int amountColony = getModel().getColony().getFood();
            if (amountColony == 0) {
                stravation(model.getColony().getFood());
                return;
            }

            if (amountColony == (-amount) || amountColony < (-amount)) {
                model.getColony().removeFood(amountColony);
                stravation(amountColony);
                return;
            } else {
                model.getColony().addFood(amount);
                stravation(amount);

                return;
            }
        }
        model.getColony().addFood(amount);
        connection.sendFoodChanged(amount, FoodChange.CROSSROAD);*/
    }

    public void visit(final ChangeMoral changeMoral) {
        final int amount = changeMoral.getAmount();
        if (amount < 0) {
            final int amountCurrentMoral = getModel().getMoral();

            if (amountCurrentMoral < (-amount) || amountCurrentMoral == (-amount)) {
                model.decreaseMoral(amountCurrentMoral);
                connection.sendMoralChanged(amount, MoralChange.CROSSROAD);
                if (model.getMoral() == 0) {
                    model.setGameState(EndGameState.getInstance());
                    connection.sendGameEnd(false);
                    return;
                }
            }
            if (amountCurrentMoral > (-amount)) {
                model.addMoral(amount);
                connection.sendMoralChanged(amount, MoralChange.CROSSROAD);

                return;
            }

        }
        model.addMoral(amount);
        connection.sendMoralChanged(amount, MoralChange.CROSSROAD);
    }

    public void visit(final SpawnSurvivors spawnSurvivors) {
        final int numberOfSurvivor = spawnSurvivors.getNumberOfSurvivors();
        final Optional<Boolean> checkChildren = spawnSurvivors.getChildren();

        if (!checkChildren.isEmpty()) {
            if (checkChildren.get()) {
                for (int i = 0; i < numberOfSurvivor; i++) {
                    if (model.hasNewSurvivorsLeft()) {
                        final Survivor survivor = model.drawSurvivor();
                        model.getColony().addSurvivor(survivor);
                        getPlayer().addSurvivor(survivor);
                        connection.sendCharacterSpawned(getPlayer().getId(), survivor.getId());
                    }
                }
                for (int i = 0; i < numberOfSurvivor; i++) {
                    model.getColony().addChild();
                    connection.sendChildSpawned();
                }
                return;
            }

        }
        spawnSurvivor(numberOfSurvivor);
    }

    public void visit(final SpawnZombies spawnZombies) {
        final Optional<Integer> locationID = spawnZombies.getLocationId();
        final int numberOfZombies = spawnZombies.getNumberOfZombies();

        if (!locationID.isEmpty()) {
            final Optional<Location> location = model.getLocation(locationID.get());
            spawnZombiesInOnePlace(numberOfZombies, location.get());
            return;
        }
        spawnZombiesEveryWhere(numberOfZombies);
    }

    public void visit(final Choice choice) {
        model.setConsequence1(choice.getConsequence1());
        model.setConsequence2(choice.getConsequence2());
        model.setGameState(VotePhaseState.getInstance());
        model.getGameState().run(model, connection);
    }

    private void spawnSurvivor(final int numberOfSurvivor) {
        for (int i = 0; i < numberOfSurvivor; i++) {
            if (model.hasNewSurvivorsLeft()) {
                final Survivor survivor = model.drawSurvivor();
                model.getColony().addSurvivor(survivor);
                getPlayer().addSurvivor(survivor);
                connection.sendCharacterSpawned(getPlayer().getId(), survivor.getId());
            }
        }
    }





    private void spawnZombiesInOnePlace(final int numberOfZombies, final Location location) {
        int counter = numberOfZombies;
        while (counter > 0) {
            for (final Entrance e : location.getEntrances()) {
                if (counter == 0) {
                    break;
                }
                spawnprocess(e, location);
                counter--;
            }
        }
    }



    private void spawnZombiesEveryWhere(final int numberOfZombies) {
        spawnZombiesInOnePlace(numberOfZombies, getModel().getColony());
        for (final Location location : getModel().getLocations()) {
            spawnZombiesInOnePlace(numberOfZombies, location);
        }
    }



    private void someGonnaBeDead(final Location location) {
        if (location.getNumChildren() > 0) {
            location.killChild();
            connection.sendChildKilled();
            connection.sendMoralChanged(-1, MoralChange.CHARACTER_DIED);
            model.decreaseMoral();
            endGame(model.getMoral());

        } else {
            final Optional<Survivor> optionalSurvivor = location.getSurvivorSmallestStatus();
            if (optionalSurvivor.isPresent()) {
                final Survivor survivor = optionalSurvivor.get();
                final KillSurvivorCommand command = new KillSurvivorCommand(
                        model.getCommId(model.getPlayer(survivor).getId()),
                        model.getPlayer(survivor),
                        survivor);
                command.execute(model, connection);
            }
        }
    }

    private void endGame(final int moral) {
        if (moral <= 0) {
            model.setGameState(EndGameState.getInstance());
            connection.sendGameEnd(false);
        }
    }

    private void spawnprocess(final Entrance entrance, final Location location) {
        if (entrance.getCapacityLeft() > 0) {
            entrance.addZombie();
            connection.sendZombieSpawned(location.getId(), entrance.getId());
        } else if (entrance.getBarricadeCount() > 0) {
            entrance.removeBarricade();
            connection.sendBarricadeDestroyed(location.getId(), entrance.getId());
        } else {
            someGonnaBeDead(location);
        }
    }
    /*
    private void stravation(final int food) {

        final int numOfFood = (int) Math.round((double) (model.getColony().getNumChildren()
                + model.getColony().getNumSurvivors()) / 2);
        if (numOfFood != 0) {
            if (food < numOfFood) {
                model.getColony().increaseStarvationToken();
                connection.sendStarvationTokenAdded();
            } else {
                connection.sendFoodChanged(food, FoodChange.CROSSROAD);
            }
        }
    }

     */

}




