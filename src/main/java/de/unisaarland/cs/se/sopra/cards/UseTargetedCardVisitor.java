package de.unisaarland.cs.se.sopra.cards;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.abilities.Ability;
import de.unisaarland.cs.se.sopra.abilities.BlueprintAbility;
import de.unisaarland.cs.se.sopra.abilities.ColtAbility;
import de.unisaarland.cs.se.sopra.abilities.HammerAbility;
import de.unisaarland.cs.se.sopra.abilities.SnowbootsAbility;
import de.unisaarland.cs.se.sopra.abilities.SwabAbility;
import de.unisaarland.cs.se.sopra.crossroads.CrossroadType;
import de.unisaarland.cs.se.sopra.crossroads.Crossroads;
import de.unisaarland.cs.se.sopra.model.Entrance;
import de.unisaarland.cs.se.sopra.model.Location;
import de.unisaarland.cs.se.sopra.model.Model;
import de.unisaarland.cs.se.sopra.model.Player;
import de.unisaarland.cs.se.sopra.model.Survivor;

import java.util.Optional;

public class UseTargetedCardVisitor extends CardVisitor {

    private static final int BARRICADE_AMOUNT = 1;

    private final int target;
    private final Survivor survivor;

    public UseTargetedCardVisitor(final Model model, final ConnectionWrapper connection,
                                  final Player player,
                                  final int commId, final int target, final Survivor survivor) {
        super(model, connection, player, commId);
        this.target = target;
        this.survivor = survivor;
    }

    /**
     * This method yields the executing survivor's ability iff its position
     * matches the position of the target survivor.
     *
     * @return The ability.
     */
    private Optional<Ability> getAbility() {
        final Optional<Survivor> optionalSurvivor = getModel().getSurvivor(target);
        if (optionalSurvivor.isPresent()) {
            if (optionalSurvivor.get().getLocation().equals(survivor.getLocation())) {
                final Survivor survivor = optionalSurvivor.get();
                return Optional.of(survivor.getAbility());
            } else {
                getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                        "Survivors not at same location!");
                return Optional.empty();
            }
        } else {
            getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                    "Survivor does not exist!");
            return Optional.empty();
        }
    }

    @Override
    public void visit(final Blueprint card) {
        final Optional<Ability> optionalAbility = getAbility();
        final Optional<Survivor> optionalSurvivor = getModel().getSurvivor(target);
        if (optionalAbility.isPresent() && optionalSurvivor.isPresent()) {
            final Ability ability = optionalAbility.get();
            final Ability newAbility = new BlueprintAbility(ability, card.getLocationId());
            optionalSurvivor.get().setAbility(newAbility);
            getPlayer().removeCard(card);
            getConnection().sendCardUsed(card.getId(), this.survivor.getId(), this.target);
            //triggerSearch();
            triggerEquiped();
        }
    }

    @Override
    public void visit(final Colt card) {
        final Optional<Ability> optionalAbility = getAbility();
        final Optional<Survivor> optionalSurvivor = getModel().getSurvivor(target);
        if (optionalAbility.isPresent() && optionalSurvivor.isPresent()) {
            final Ability ability = optionalAbility.get();
            final Ability newAbility = new ColtAbility(ability);
            optionalSurvivor.get().setAbility(newAbility);
            getPlayer().removeCard(card);
            getConnection().sendCardUsed(card.getId(), this.survivor.getId(), this.target);
            triggerEquiped();
        }
    }

    @Override
    public void visit(final Hammer card) {
        final Optional<Ability> optionalAbility = getAbility();
        final Optional<Survivor> optionalSurvivor = getModel().getSurvivor(target);
        if (optionalAbility.isPresent() && optionalSurvivor.isPresent()) {
            final Ability ability = optionalAbility.get();
            final Ability newAbility = new HammerAbility(ability);
            optionalSurvivor.get().setAbility(newAbility);
            getPlayer().removeCard(card);
            getConnection().sendCardUsed(card.getId(), this.survivor.getId(), this.target);
            triggerEquiped();
        }
    }

    @Override
    public void visit(final Lock card) {
        final Location location = survivor.getLocation();
        final Optional<Entrance> optionalEntrance = location.getEntrance(target);
        if (optionalEntrance.isPresent()) {
            final Entrance entrance = optionalEntrance.get();
            if (entrance.getCapacityLeft() > 0) {
                //changed
                entrance.placeBarricades(BARRICADE_AMOUNT);
                getPlayer().removeCard(card);
                //changed
                getModel().addTrash();
                getConnection().sendCardUsed(card.getId(), survivor.getId(), target);
                getConnection().sendWasteChanged(1);
                triggerBarricaded();
                triggerWasteChanged();
            } else {
                getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                        "No space left.");
            }
        } else {
            getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                    "Cannot use card.");
        }
    }

    @Override
    public void visit(final Medicine card) {
        final Optional<Survivor> optionalSurvivor = getModel().getSurvivor(target);
        if (optionalSurvivor.isPresent() && optionalSurvivor.get().getLocation()
                .equals(survivor.getLocation())) {
            final Survivor patient = optionalSurvivor.get();
            if (patient.isWounded()) {
                if (patient.getTimesFrostbitten() > 0) {
                    patient.healFrostbite();
                } else {
                    patient.heal();
                }
                getPlayer().removeCard(card);
                //changed
                getModel().addTrash();
                getConnection().sendCardUsed(card.getId(), survivor.getId(), target);
                getConnection().sendWasteChanged(1);
                triggerWasteChanged();
            } else {
                getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                        "No wound to heal");
            }
        } else {
            getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                    "Cannot use card.");
        }
    }

    @Override
    public void visit(final Scissors card) {
        final Location location = survivor.getLocation();
        final Optional<Entrance> optionalEntrance = location.getEntrance(target);
        if (optionalEntrance.isPresent()) {
            final Entrance entrance = optionalEntrance.get();
            if (entrance.getZombieCount() > 0) {
                entrance.killZombie();
                getPlayer().removeCard(card);
                //changed
                getModel().addTrash();
                getConnection().sendCardUsed(card.getId(), survivor.getId(), target);
                getConnection().sendWasteChanged(1);
                getConnection().sendZombieKilled(survivor.getId(), location.getId(),
                        entrance.getId());
                triggerWasteChanged();
            } else {
                getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                        "No zombie at location!");
            }
        } else {
            getConnection().sendCommandFailed(getModel().getCommId(getPlayer().getId()),
                    "Cannot use card.");
        }
    }

    @Override
    public void visit(final Snowboots card) {
        final Optional<Ability> optionalAbility = getAbility();
        final Optional<Survivor> optionalSurvivor = getModel().getSurvivor(target);
        if (optionalAbility.isPresent() && optionalSurvivor.isPresent()) {
            final Ability ability = optionalAbility.get();
            final Ability newAbility = new SnowbootsAbility(ability);
            optionalSurvivor.get().setAbility(newAbility);
            getPlayer().removeCard(card);
            getConnection().sendCardUsed(card.getId(), this.survivor.getId(), this.target);
            //triggerMoved();
            triggerEquiped();
        }
    }

    @Override
    public void visit(final Swab card) {
        final Optional<Ability> optionalAbility = getAbility();
        final Optional<Survivor> optionalSurvivor = getModel().getSurvivor(target);
        if (optionalAbility.isPresent() && optionalSurvivor.isPresent()) {
            final Ability ability = optionalAbility.get();
            final Ability newAbility = new SwabAbility(ability, card.getUses(), card.getAmount());
            optionalSurvivor.get().setAbility(newAbility);
            getPlayer().removeCard(card);
            getConnection().sendCardUsed(card.getId(), this.survivor.getId(), this.target);
            //triggerWasteChanged();
            triggerEquiped();
        }
    }

    @Override
    public void visit(final Fuel card) {
        if (survivor.hasMoved()) {
            getConnection().sendCommandFailed(getCommId(), "Cannot move anymore!");
        } else if (!getPlayer().hasSurvivor(survivor.getId())) {
            getConnection().sendCommandFailed(getCommId(),
                    "Player does not control this character!");
        } else {
            final Optional<Location> optDestination = getModel().getLocation(this.target);
            if (optDestination.isPresent()) {
                final Location destination = optDestination.get();
                if (destination.equals(survivor.getLocation())) {
                    getConnection().sendCommandFailed(getCommId(), "You are already here");
                } else if (destination.hasPlacesLeft()) {
                    //changed
                    getModel().moveSurvivor(this.survivor, destination);

                    this.survivor.moved();
                    //changed
                    getModel().addTrash();
                    getConnection().sendCardUsed(card.getId(), this.survivor.getId(), this.target);
                    getConnection().sendWasteChanged(1);
                    triggerMoved();
                    triggerWasteChanged();


                    getPlayer().removeCard(card);

                } else {
                    getConnection().sendCommandFailed(getCommId(), "No places left!");
                }
            } else {
                getConnection().sendCommandFailed(getCommId(), "Invalid Argument! locationId");
            }
        }
    }

    private void triggerMoved() {
        final Crossroads crossroad = getPlayer().getCrossroad();
        //final Crossroads crossroad = getModel().getCurrentPlayer().getCrossroad();

        if (crossroad.getCrossroadType() == CrossroadType.MOVED) {
            crossroad.trigger(this.survivor, getConsequenceVisitor(), getModel());
        }
    }

    private void triggerWasteChanged() {
        final Crossroads crossroad = getPlayer().getCrossroad();
        //final Crossroads crossroad = getModel().getCurrentPlayer().getCrossroad();

        if (crossroad.getCrossroadType() == CrossroadType.WASTECHANGED) {
            crossroad.trigger(this.survivor, getConsequenceVisitor(), getModel());

        }

    }

    private void triggerEquiped() {
        final Crossroads crossroad = getPlayer().getCrossroad();
        //final Crossroads crossroad = getModel().getCurrentPlayer().getCrossroad();

        if (crossroad.getCrossroadType() == CrossroadType.EQUIPED) {
            crossroad.trigger(this.survivor, getConsequenceVisitor(), getModel());

        }

    }

    private void triggerBarricaded() {
        final Crossroads crossroad = getPlayer().getCrossroad();
        //final Crossroads crossroad = getModel().getCurrentPlayer().getCrossroad();
        if (crossroad.getCrossroadType() == CrossroadType.BARRICADED) {
            crossroad.trigger(this.survivor, getConsequenceVisitor(), getModel());
        }
    }

    /*
    private void triggerSearch() {
        //final Crossroads crossroad = getPlayer().getCrossroad();
        final Crossroads crossroad = getModel().getCurrentPlayer().getCrossroad();

        if (crossroad.getCrossroadType() == CrossroadType.SEARCHED) {
            crossroad.trigger(getModel().getRandomSurvivor(getPlayer()), getConsequenceVisitor(),
                    getModel());
        }
    }
    */


}
