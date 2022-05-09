package de.unisaarland.cs.se.sopra.crossroads;

import de.unisaarland.cs.se.sopra.config.ConsequenceFactory;
import de.unisaarland.cs.se.sopra.config.CrossroadsFactory;
import de.unisaarland.cs.se.sopra.config.ParamMap;

import java.util.Optional;

public class CrossroadsFactoryImpl implements CrossroadsFactory<Crossroads> {
    private final ConsequenceFactory<Consequence> consequenceFactory;

    public CrossroadsFactoryImpl(final ConsequenceFactory<Consequence> consequenceFactory) {
        this.consequenceFactory = consequenceFactory;
    }


    @Override
    public Crossroads createCrossroads(final int id, final String name, final ParamMap params,
                                       final String consequenceName,
                                       final ParamMap paramConsequence) {

        return switch (name) {
            case "barricaded" -> {
                final int iD = params.getInt("identifier");
                Optional<Integer> locationId;
                if (params.hasLocation("locationId")) {
                    locationId = Optional.of(params.getInt("locationId"));
                } else {
                    locationId = Optional.empty();
                }
                final Consequence consequence = consequenceFactory
                        .createConsequence(consequenceName, paramConsequence);
                yield createBarricaded(iD, consequence, locationId);
            }
            case "moved" -> {
                final int iD = params.getInt("identifier");
                Optional<Integer> locationId;
                if (params.hasLocation("locationId")) {
                    locationId = Optional.of(params.getInt("locationId"));
                } else {
                    locationId = Optional.empty();
                }
                final Consequence consequence = consequenceFactory
                        .createConsequence(consequenceName, paramConsequence);
                yield createMoved(iD, consequence, locationId);
            }
            case "searched" -> {
                final int iD = params.getInt("identifier");
                Optional<Integer> locationId;
                if (params.hasLocation("locationId")) {
                    locationId = Optional.of(params.getInt("locationId"));
                } else {
                    locationId = Optional.empty();
                }
                final Consequence consequence = consequenceFactory
                        .createConsequence(consequenceName, paramConsequence);
                yield createSearched(iD, consequence, locationId);
            }
            case "wasteChanged" -> {
                final int iD = params.getInt("identifier");
                final int amount = params.getInt("amount");
                final Consequence consequence = consequenceFactory
                        .createConsequence(consequenceName, paramConsequence);

                yield createWasteChanged(iD, consequence, amount);
            }
            case "equip" -> {
                final int iD = params.getInt("identifier");
                final Consequence consequence = consequenceFactory
                        .createConsequence(consequenceName, paramConsequence);

                yield createEquiped(iD, consequence);
            }

            default -> throw new IllegalArgumentException("Unknown Crossroad. (%s)"
                    .formatted(name));
        };
    }


    private Crossroads createBarricaded(final int id, final Consequence consequence,
                                        final Optional<Integer> locationId) {
        return new Barricaded(id, consequence, locationId);
    }

    private Crossroads createMoved(final int id, final Consequence consequence,
                                   final Optional<Integer> locationId) {
        return new Moved(id, consequence, locationId);
    }

    private Crossroads createSearched(final int id, final Consequence consequence,
                                      final Optional<Integer> locationId) {
        return new Searched(id, consequence, locationId);
    }

    private Crossroads createWasteChanged(final int id, final Consequence consequence,
                                          final int amount) {
        return new WasteChanged(id, consequence, amount);
    }

    private Crossroads createEquiped(final int id, final Consequence consequence) {
        return new Equibed(id, consequence);
    }


}