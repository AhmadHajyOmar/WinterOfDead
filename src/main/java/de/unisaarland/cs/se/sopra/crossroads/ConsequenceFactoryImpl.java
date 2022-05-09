package de.unisaarland.cs.se.sopra.crossroads;

import de.unisaarland.cs.se.sopra.config.ConsequenceFactory;
import de.unisaarland.cs.se.sopra.config.JSONParser;
import de.unisaarland.cs.se.sopra.config.ParamMap;
import org.json.JSONObject;
import java.util.Optional;

public class ConsequenceFactoryImpl implements ConsequenceFactory<Consequence> {

    @Override
    public Consequence createConsequence(final String name, final ParamMap params) {
        return switch (name) {
            case "changeFood" -> {
                final int amount = params.getInt("amount");
                yield createChangeFood(amount);
            }
            case "changeMoral" -> {
                final int amount = params.getInt("amount");

                yield createChangeMoral(amount);
            }
            case "spawnZombies" -> {
                final int numZombies = params.getInt("amount");
                Optional<Integer> locationId;
                if (params.hasLocation("locationId")) {
                    locationId = Optional.of(params.getInt("locationId"));
                } else {
                    locationId = Optional.empty();
                }

                yield createSpawnZombies(numZombies, locationId);
            }
            case "choice" -> {


                final JSONObject consequenceA = params.getJSONObject("consequence1");
                final String consequenceTypeA = consequenceA.keys().next();
                final JSONObject properties = consequenceA.getJSONObject(consequenceTypeA);
                final ParamMap param1 = new JSONParser.JSONParaMap(properties);
                final Consequence consequence1 = createConsequence(consequenceTypeA, param1);


                final JSONObject consequenceB = params.getJSONObject("consequence2");
                final String consequenceTypeB = consequenceB.keys().next();
                final JSONObject properties2 = consequenceB.getJSONObject(consequenceTypeB);
                final ParamMap param2 = new JSONParser.JSONParaMap(properties2);
                final Consequence consequence2 = createConsequence(consequenceTypeB, param2);

                yield craeteChoice(consequence1, consequence2);
            }
            case "spawnSurvivors" -> {


                Optional<Boolean> children;
                if (params.hasLocation("children")) {
                    children = Optional.of(params.getBoolean("children"));
                } else {
                    children = Optional.empty();
                }
                final int numberOfSurvivors = params.getInt("amount");
                yield createSpawnSurvivors(numberOfSurvivors, children);
            }

            default -> throw new IllegalArgumentException("Unknown Consequence. (%s)"
                    .formatted(name));
        };
    }


    public Consequence createChangeFood(final int amount) {
        return new ChangeFood(amount);
    }

    private Consequence createChangeMoral(final int amount) {
        return new ChangeMoral(amount);
    }

    private Consequence createSpawnZombies(final int numZombies,
                                           final Optional<Integer> locationId) {
        return new SpawnZombies(numZombies, locationId);
    }

    private Consequence craeteChoice(final Consequence consequence1,
                                     final Consequence consequence2) {
        return new Choice(consequence1, consequence2);
    }

    private Consequence createSpawnSurvivors(final int numberOfSurvivors,
                                             final Optional<Boolean> children) {
        return new SpawnSurvivors(numberOfSurvivors, children);
    }




}
