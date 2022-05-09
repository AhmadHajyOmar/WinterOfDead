package de.unisaarland.cs.se.sopra.model;

import de.unisaarland.cs.se.sopra.RegistrationState;
import de.unisaarland.cs.se.sopra.State;
import de.unisaarland.cs.se.sopra.crossroads.Consequence;
import de.unisaarland.cs.se.sopra.crossroads.Crossroads;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.HashMap;
import java.util.HashSet;

public class Model {

    private final Colony colony;
    private final List<Location> locations;
    private final Map<Integer, Player> players;
    private final RandomManager randomManager;
    private final List<Survivor> allSurvivors;
    private int chooseFirstConsequence;
    private int chooseSecondConsequence;
    private final Goal goal;
    private final Path configPath;
    private boolean inTheMidleOfTheGame;
    private Consequence chosenConsequence;
    private Consequence consequence1;
    private Consequence consequence2;

    private final int maxPlayers;
    private State currentState;
    private int trashPile;
    private int currentPlayer;
    private List<Survivor> availableSurvivors;
    private Crisis currentCrisis;
    private List<Crisis> crises;
    private List<Crossroads> crossroads;
    private int moral;
    private int rounds;

    public Model(
            final Colony colony,
            final List<Location> locations,
            final RandomManager randomManager,
            final Collection<Survivor> availableSurvivors,
            final Collection<Crisis> crises,
            final Collection<Crossroads> crossroads,
            final Goal goal,
            final Path configPath,
            final int maxPlayers,
            final int moral,
            final int rounds) {
        this.colony = colony;
        this.locations = locations;
        this.players = new HashMap<>();
        this.randomManager = randomManager;
        this.availableSurvivors = new LinkedList<>(availableSurvivors);
        this.allSurvivors = new LinkedList<>(availableSurvivors);
        this.crises = new LinkedList<>(crises);
        this.crossroads = new LinkedList<>(crossroads);
        this.goal = goal;
        this.configPath = configPath;
        this.maxPlayers = maxPlayers;
        this.currentState = RegistrationState.getInstance();
        this.moral = moral;
        this.rounds = rounds;
        this.chooseFirstConsequence = 0;
        this.chooseSecondConsequence = 0;
        this.inTheMidleOfTheGame = false;
    }

    public Path getConfigPath() {
        return this.configPath;
    }

    /**
     * Gets the player which controls the given survivor's id if existent.
     *
     * @param survivorId The survivor's id.
     * @return The player.
     */
    public Optional<Player> getPlayerForSurvivor(final int survivorId) {
        for (final Player p : players.values()) {
            if (p.hasSurvivor(survivorId)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Player getCurrentPlayer() {
        return getPlayer(currentPlayer);
    }

    public void setCurrentPlayer(final Player player) {
        this.currentPlayer = player.getId();
    }

    public void setCurrentPlayer(final int player) {
        this.currentPlayer = player;
    }

    public void setConsequence1(final Consequence consequence) {
        this.consequence1 = consequence;
    }

    public void setConsequence2(final Consequence consequence) {
        this.consequence2 = consequence;
    }

    public Consequence getConsequence1() {
        return consequence1;
    }

    public Consequence getConsequence2() {
        return consequence2;
    }

    public void setInTheMidleOfTheGame(final boolean check) {
        this.inTheMidleOfTheGame = check;
    }

    public boolean getInTheMidleOfTheGame() {
        return this.inTheMidleOfTheGame;
    }

    public void setChosenConsequence(final Consequence consequence) {
        this.chosenConsequence = consequence;
    }

    public Consequence getChosenConsequence() {
        return this.chosenConsequence;
    }

    /**
     * @return All players sorted by their id.
     */
    public List<Player> getPlayers() {
        final List<Player> players = new ArrayList<>(this.players.values());
        players.sort(Comparator.comparingInt(Player::getId));
        return players;
    }

    public Colony getColony() {
        return colony;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public State getGameState() {
        return currentState;
    }

    public void setGameState(final State gameState) {
        this.currentState = gameState;
    }

    public int getTrashPile() {
        return trashPile;
    }

    //changed
    public void addTrash() {
        this.trashPile++;

    }

    public Survivor getRandomSurvivor(final Player player) {
        for (int i = 0; i < player.getSurvivors().size(); i++) {
            if (player.getSurvivors().get(i) != null) {
                return player.getSurvivors().get(i);
            }
        }
        return null;
    }

    /**
     * @param amount The amount of waste to clean.
     * @return The actually cleaned waste.
     */
    public int removeTrash(final int amount) {

        this.trashPile = this.trashPile - amount;
        if (this.trashPile < 0) {
            final int oldTrash = amount + this.trashPile;
            this.trashPile = 0;

            return oldTrash;
        } else {
            return amount;
        }
    }

    public void drawCrossroads(final Player player) {
        if (player != null) {
            player.updateCrossroad(this.crossroads.remove(0));

        }
    }

    public List<Crossroads> getCrossroads() {
        return this.crossroads;
    }

    public StatusEffect rollInfectionDie() {
        return randomManager.rollInfectionDie();
    }

    /**
     * Returns true if the given character belongs to the player.
     *
     * @param playerId   ID of the player.
     * @param survivorId ID of the character.
     * @return Whether the character belongs to the player.
     */
    public boolean hasSurvivor(final int playerId, final int survivorId) {
        final List<Survivor> survivors = players.get(getCommId(playerId)).getSurvivors();
        for (final Survivor survivor : survivors) {
            if (survivor.getId() == survivorId) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param survivorId ID of the character.
     * @return The character for the given id.
     */
    public Optional<Survivor> getSurvivor(final int survivorId) {
        for (final Survivor survivor : allSurvivors) {
            if (survivor.getId() == survivorId) {
                return Optional.of(survivor);
            }
        }
        return Optional.empty();
    }

    /**
     * @param playerId ID of the player.
     * @return The player for the given id.
     */
    public Player getPlayer(final int playerId) {
        final int commId = getCommId(playerId);
        return players.get(commId);
    }

    /**
     * Gets the player which controls the given survivor if existent.
     *
     * @param survivor The survivor.
     * @return The player.
     */
    public Player getPlayer(final Survivor survivor) {
        for (final Player player : players.values()) {
            final Set<Survivor> survivors = new HashSet<>(player.getSurvivors());
            if (survivors.contains(survivor)) {
                return player;
            }
        }
        throw new IllegalStateException("The survivor doesn't belong to any player");
    }

    /**
     * This function returns the corresponding playerID to a given commID.
     *
     * @param commId CommID of the player.
     * @return PlayerId.
     */
    public int getPlayerID(final int commId) {
        return players.get(commId).getId();
    }

    /**
     * This function returns true if the player with the given playerId is the current player.
     *
     * @param playerId ID of the player.
     * @return Whether the player is the current player.
     */
    public boolean isCurrentPlayer(final int playerId) {
        return currentPlayer == playerId;
    }

    /**
     * @param playerId ID of the player.
     * @return CommId for playerId.
     */
    public int getCommId(final int playerId) {
        for (final var entry : players.entrySet()) {
            final int commId = entry.getKey();
            final Player player = entry.getValue();
            if (player.getId() == playerId) {
                return commId;
            }
        }
        return -1;
    }

    public Crisis getCrisis() {
        return currentCrisis;
    }

    public void shuffleCrises() {
        this.crises = randomManager.shuffleList(crises);
    }

    public void shuffleCrossroads() {
        this.crossroads = randomManager.shuffleList(crossroads);
    }

    public void addVoteForTheFirstConsequence() {
        this.chooseFirstConsequence++;
    }

    public int getChooseFirstConsequence() {
        return this.chooseFirstConsequence;
    }

    public int getChooseSecondConsequence() {
        return this.chooseSecondConsequence;
    }

    public void addVoteForTheSecondConsequence() {
        this.chooseSecondConsequence++;
    }

    public void resetVots() {
        this.chooseFirstConsequence = 0;
        this.chooseSecondConsequence = 0;
    }

    public boolean getVoteResult() {

        return getChooseFirstConsequence() >= getChooseSecondConsequence();
    }

    public void resetVoteForAllPlayers() {
        for (int i = 0; i < this.getPlayers().size(); i++) {
            this.getPlayer(i).setVoted(false);
        }
    }



    public void nextCrisis() {
        this.currentCrisis = crises.remove(0);
    }

    /**
     * This function returns the location for the given id if existent.
     *
     * @param locationId The locationId.
     * @return The location given the locationId.
     */
    public Optional<Location> getLocation(final int locationId) {
        for (final Location location : locations) {
            if (location.getId() == locationId) {
                return Optional.of(location);
            }
        }
        if (locationId == colony.getId()) {
            return Optional.of(colony);
        }
        return Optional.empty();
    }

    /**
     * @param survivor    Survivor to move.
     * @param destination Location to move the survivor to.
     */
    //changed
    public void moveSurvivor(final Survivor survivor, final Location destination) {
        survivor.getLocation().removeSurvivor(survivor);
        destination.addSurvivor(survivor);
        survivor.setLocation(destination);


    }

    public int rollActionDie() {
        return randomManager.rollActionDie();
    }

    public Survivor drawSurvivor() {
        return availableSurvivors.remove(0);
    }

    public boolean hasNewSurvivorsLeft() {
        return !availableSurvivors.isEmpty();
    }

    public void removePlayer(final Player player) {
        player.setLeft();
    }

    public int nextInt(final int upperBound) {
        return randomManager.nextInt(upperBound);
    }

    public void decreaseMoral() {
        moral--;
    }

    public void decreaseMoral(final int moralChange) {
        moral -= moralChange;
    }

    public void decreaseRounds() {
        rounds--;
    }

    public void addPlayer(final int commId, final String playerName) {
        this.players.put(commId, new Player(players.size(), playerName));
    }

    public void nextPlayer() {
        currentPlayer++;
    }



    public void putBackSurvivors(final List<Survivor> survivors) {
        availableSurvivors.addAll(survivors);
    }

    public Survivor popSurvivor() {
        return this.availableSurvivors.remove(0);
    }

    public boolean hasPlayer(final int commId) {
        return players.containsKey(commId);
    }

    public void shuffleSurvivors() {
        availableSurvivors = randomManager.shuffleList(availableSurvivors);
    }

    /**
     * Shuffles all colony and location cards.
     */
    public void shuffleCards() {
        colony.setCards(randomManager.shuffleList(colony.getCards()));
        for (final Location location : locations) {
            location.setCards(randomManager.shuffleList(location.getCards()));
        }
    }

    public void addMoral(final int moralPlus) {
        moral += moralPlus;
    }

    public int getMoral() {
        return moral;
    }

    public Goal getGoal() {
        return goal;
    }

    public int getRounds() {
        return rounds;
    }

    public Player getPlayerByCommId(final int commId) {
        return players.get(commId);
    }
}
