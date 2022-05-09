package de.unisaarland.cs.se.sopra.abilities;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.cards.Card;
import de.unisaarland.cs.se.sopra.crossroads.CrossroadType;
import de.unisaarland.cs.se.sopra.crossroads.Crossroads;
import de.unisaarland.cs.se.sopra.model.Location;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;
import de.unisaarland.cs.se.sopra.model.Survivor;

import java.util.Optional;

public class SearchVisitor extends PassiveAbilityVisitor {

    private static final int STANDARD_DRAW = 1;
    private static final int MAX_CHILD_SPAWN = 4;

    public SearchVisitor(final Model model, final ConnectionWrapper connection, final Player player,
                         final Survivor survivor) {
        super(model, connection, player, survivor);
    }

    @Override
    public void executeStandardAction() {
        if (drawCards(STANDARD_DRAW)) {
            triggerSearched();
        }

    }

    @Override
    public void visit(final SearchAbility searchAbility) {
        final Location location = getSurvivor().getLocation();
        if (searchAbility.hasUsesLeft() && location.getId() == searchAbility.getLocation()) {
            if (drawCards(searchAbility.getNumCards() + STANDARD_DRAW)) {
                searchAbility.used();
                triggerSearched();

            }
        } else {
            executeStandardAction();



        }
    }

    @Override
    public void visit(final BlueprintAbility searchAbility) {
        final Location location = getSurvivor().getLocation();
        if (searchAbility.hasUsesLeft() && location.getId() == searchAbility.getLocation()) {
            if (drawCards(searchAbility.getNumCards() + STANDARD_DRAW)) {
                searchAbility.used();
                triggerSearched();
            }
        } else {
            searchAbility.getParent().accept(this);
            triggerSearched();
        }
    }

    /**
     * This function draws cards, starts a random encounter if needed
     * and removes a fitting die from the player.
     *
     * @param numCards Amount of cards to draw.
     * @return Whether the drawing process succeeded.
     */
    private boolean drawCards(final int numCards) {
        final int dieValue = getSurvivor().getSearch();
        final Optional<Integer> highestDie = getPlayer().getHighestDie();
        if (highestDie.isEmpty()) {
            getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                    "No die left!");
            return false;
        } else if (highestDie.get() < dieValue) {
            getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                    "Die to small!");
            return false;
        }
        final Location location = getSurvivor().getLocation();
        if (randomEncounter(location)) {
            getPlayer().removeDieLowerThan(dieValue);
            return false;
        }
        Optional<Card> card = location.drawCard();
        if (card.isPresent()) {
            getPlayer().addCard(card.get());
            //changed
            getConnection().sendSearched(getSurvivor().getId(), location.getId());
            getConnection().sendCardDrawn(getPlayer().getId(), card.get().getId());

        } else {
            getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                    "No cards left at location!");
            return false;
        }
        for (int i = 1; i < numCards; i++) {
            card = location.drawCard();
            if (card.isPresent()) {
                getPlayer().addCard(card.get());
                getConnection().sendCardDrawn(getPlayer().getId(), card.get().getId());
            } else {
                break;
            }
        }
        getPlayer().removeDieLowerThan(dieValue);
        return true;
    }

    /**
     * @param location Where the random encounter might happen.
     * @return True if a random encounter happened.
     */
    private boolean randomEncounter(final Location location) {
        if (location.hasCardsLeft() && getModel().hasNewSurvivorsLeft()) {
            final int upperBound = location.getInitialStackSize();
            final int randomValue = getModel().nextInt(upperBound);
            if (randomValue == 0) {
                //changed

                getConnection().sendSearched(getSurvivor().getId(), location.getId());
                final Survivor newSurvivor = getModel().drawSurvivor();
                getPlayer().addSurvivor(newSurvivor);
                newSurvivor.setLocation(getModel().getColony());
                getModel().getColony().addSurvivor(newSurvivor);
                getConnection().sendCharacterSpawned(
                        getPlayer().getId(), newSurvivor.getId());
                final int children = getModel().nextInt(MAX_CHILD_SPAWN);
                for (int i = 0; i < children; i++) {
                    getModel().getColony().addChild();
                    getConnection().sendChildSpawned();
                }
                return true;
            }
        }
        return false;
    }

    //Change new Method

    private void triggerSearched() {
        final Crossroads crossroad = getPlayer().getCrossroad();
        //final Crossroads crossroad = getModel().getCurrentPlayer().getCrossroad();
        if (crossroad.getCrossroadType() == CrossroadType.SEARCHED) {
            crossroad.trigger(getSurvivor(), getConsequenceVisitor(), getModel());
        }
    }




}
