package de.unisaarland.cs.se.sopra.config;

import org.json.JSONObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Validator<M> implements ModelBuilder<M> {

    public static final int SPAWN_CAPACITY = 3;
    public static final int MAX_DIE = 6;

    private final List<Integer> referencedCards;
    private final List<Integer> socials;
    private final List<Integer> cardIds;
    private final List<Integer> crisesIds;
    private final List<Integer> crossroadsIds;
    private final List<Integer> survivorIds;
    private final Map<Integer, List<Integer>> idsForLocation;
    private final ModelBuilder<M> modelBuilder;
    private final Map<Integer, Integer> entrancesForLocation;
    private final Set<Integer> referencedLocations;

    private Optional<Integer> maxPlayers;
    private Optional<Integer> zombiesLocations;
    private Optional<Integer> zombiesColony;
    private Optional<Integer> childrenInColony;
    private Optional<Path> configPath;
    private Optional<Long> seed;
    private Optional<Integer> moral;
    private Optional<Integer> rounds;

    private Optional<Integer> barricades;
    private Optional<Integer> locationWithZombies;

    private int colonyId;

    public Validator(final ModelBuilder<M> modelBuilder) {
        maxPlayers = Optional.empty();
        zombiesLocations = Optional.empty();
        zombiesColony = Optional.empty();
        childrenInColony = Optional.empty();
        configPath = Optional.empty();
        seed = Optional.empty();
        moral = Optional.empty();
        rounds = Optional.empty();
        barricades = Optional.empty();

        referencedCards = new ArrayList<>();
        socials = new ArrayList<>();
        cardIds = new ArrayList<>();
        crisesIds = new ArrayList<>();
        crossroadsIds = new ArrayList<>();
        survivorIds = new ArrayList<>();
        idsForLocation = new HashMap<>();
        entrancesForLocation = new HashMap<>();
        referencedLocations = new HashSet<>();
        this.modelBuilder = modelBuilder;
    }

    @Override
    public void addColony(final int id, final int entrances, final List<Integer> cardIds) {
        checkBounds(
                1,
                Integer.MAX_VALUE,
                entrances,
                "Invalid number for colony entrances specified: %d".formatted(entrances));
        checkUniqueId(
                id,
                idsForLocation.keySet(),
                "Location id is required to be unique (%s)".formatted(id));
        colonyId = id;
        entrancesForLocation.put(id, entrances);
        idsForLocation.put(id, cardIds);
        referencedCards.addAll(cardIds);
        this.modelBuilder.addColony(id, entrances, cardIds);
    }

    @Override
    public void addLocation(
            final int id,
            final String name,
            final int entrances,
            final List<Integer> cardIds,
            final int survivorSpaces) {
        checkBounds(
                1,
                Integer.MAX_VALUE,
                entrances,
                "Invalid number for location entrances specified: %d".formatted(entrances));
        checkBounds(
                1,
                Integer.MAX_VALUE,
                survivorSpaces,
                "Invalid number for location character spaces specified: %d"
                        .formatted(survivorSpaces));
        checkUniqueId(
                id,
                idsForLocation.keySet(),
                "Location id is required to be unique (%s)".formatted(id));
        entrancesForLocation.put(id, entrances);
        idsForLocation.put(id, cardIds);
        referencedCards.addAll(cardIds);
        this.modelBuilder.addLocation(id, name, entrances, cardIds, survivorSpaces);
    }

    @Override
    public void addSurvivor(
            final int id,
            final String name,
            final int attack,
            final int search,
            final int status,
            final String abilityName,
            final ParamMap abilityParams) {
        if (attack > MAX_DIE) {
            throw new IllegalArgumentException("Attack exceeds maximum die value");
        }
        if (search > MAX_DIE) {
            throw new IllegalArgumentException("Attack exceeds maximum die value");
        }
        checkAbility(abilityName, abilityParams);
        survivorIds.add(id);
        socials.add(status);
        modelBuilder.addSurvivor(id, name, attack, search, status, abilityName, abilityParams);
    }

    private void checkAbility(final String name, final ParamMap params) {
        switch (name) {
            case "kill" -> {
                final int dieValue = params.getInt("dieValue");
                if (dieValue > 6) {
                    throw new IllegalArgumentException(
                            "Kill ability specifies die value greater than 6");
                }
                final int locationId = params.getInt("locationId");
                referencedLocations.add(locationId);
            }
            case "search" -> {
                final int location = params.getInt("location");
                referencedLocations.add(location);
            }
            default -> {
                return;
            }
        }
    }

    private void checkCard(final String name, final ParamMap params) {
        switch (name) {
            case "food" -> {
                final int amount = params.getInt("amount");
                if (amount < 1) {
                    throw new IllegalArgumentException(
                            "Food card specifies illegal amount. (%d)".formatted(amount));
                }
            }
            case "blueprint" -> {
                final int location = params.getInt("location");
                referencedLocations.add(location);
            }
            default -> {
                return;
            }
        }
    }

    private void checkConsequence(final String name, final ParamMap paramMap) {

        switch (name) {
            case "spawnSurvivors" -> {
                final int numberOfSurvivors = paramMap.getInt("amount");

                if (numberOfSurvivors < 1) {
                    throw new IllegalArgumentException(
                            "numberOfSurvivors specifies illegal amount. (%d)"
                                    .formatted(numberOfSurvivors));
                }
            }
            //check if children not equal to true or false ???
            case "spawnZombies" -> {
                if (paramMap.hasLocation("locationId")) {
                    final Optional<Integer> locationId = Optional.of(paramMap.getInt("locationId"));
                    referencedLocations.add(locationId.get());
                }

                final int numberOfZombies = paramMap.getInt("amount");

                if (numberOfZombies < 1) {
                    throw new IllegalArgumentException(
                            "numberOfZombies specifies illegal amount. (%d)"
                                    .formatted(numberOfZombies));
                }

            }
            case "choice" -> {

                if (!paramMap.hasJSONObject("consequence1")) {
                    throw new IllegalArgumentException("wrong choice consequence. (%d)"
                            .formatted("consequence1"));
                }

                if (!paramMap.hasJSONObject("consequence2")) {
                    throw new IllegalArgumentException("wrong choice consequence. (%d)"
                            .formatted("consequence2"));

                }

                final JSONObject consequenceA = paramMap.getJSONObject("consequence1");
                final String consequenceTypeA = consequenceA.keys().next();
                final JSONObject properties = consequenceA.getJSONObject(consequenceTypeA);
                final ParamMap param1 = new JSONParser.JSONParaMap(properties);
                checkConsequence(consequenceTypeA, param1);

                final JSONObject consequenceB = paramMap.getJSONObject("consequence2");
                final String consequenceTypeB = consequenceB.keys().next();
                final JSONObject properties2 = consequenceB.getJSONObject(consequenceTypeB);
                final ParamMap param2 = new JSONParser.JSONParaMap(properties2);
                checkConsequence(consequenceTypeB, param2);





            }
            default -> {
                return;
            }
        }
    }




    private void checkCrossroads(final String name, final ParamMap paramMap) {
        switch (name) {
            case "barricaded", "moved", "searched" -> {
                if (paramMap.hasLocation("locationId")) {
                    final Optional<Integer> locationId = Optional.of(paramMap.getInt("locationId"));
                    referencedLocations.add(locationId.get());
                }
            }
            default -> {
                return;
            }

        }

    }

    private void checkReferencedLocations() {
        for (final int locationId : referencedLocations) {
            if (!idsForLocation.containsKey(locationId)) {
                throw new IllegalArgumentException(
                        "Referencing a non-existent location. (%d)".formatted(locationId));
            }
        }
    }

    @Override
    public void addCrisis(
            final int id, final String type, final int moralChange, final int requiredCards) {
        crisesIds.add(id);
        modelBuilder.addCrisis(id, type, moralChange, requiredCards);
    }

    @Override
    public void addCard(final int id, final String name, final ParamMap params) {
        checkCard(name, params);
        cardIds.add(id);
        modelBuilder.addCard(id, name, params);
    }


    @Override
    public void addCrossroads(final int id, final String name, final ParamMap paramMap,
                              final String consequenceName, final ParamMap consequenceParam) {
        checkCrossroads(name, paramMap);
        checkConsequence(consequenceName, consequenceParam);
        crossroadsIds.add(id);
        modelBuilder.addCrossroads(id, name, paramMap, consequenceName, consequenceParam);

    }


    @Override
    public void addGoal(
            final Optional<Integer> locationWithZombies,
            final Optional<Integer> barricades,
            final Optional<Boolean> survive) {
        int numOfGoalsSpecified = 0;
        this.barricades = barricades;
        this.locationWithZombies = locationWithZombies;
        if (locationWithZombies.isPresent()) {
            numOfGoalsSpecified++;
        }
        if (barricades.isPresent()) {
            numOfGoalsSpecified++;
        }
        if (survive.isPresent()) {
            numOfGoalsSpecified++;
        }
        if (numOfGoalsSpecified != 1) {
            throw new IllegalArgumentException(
                    "Incorrect number of goal conditions specified: %d"
                            .formatted(numOfGoalsSpecified));
        }
        modelBuilder.addGoal(locationWithZombies, barricades, survive);
    }

    @Override
    public void setMaxPlayers(final int maxPlayers) {
        checkBounds(
                1,
                Integer.MAX_VALUE,
                maxPlayers,
                "Invalid number for max players specified: %d".formatted(maxPlayers));
        this.maxPlayers = Optional.of(maxPlayers);
        modelBuilder.setMaxPlayers(maxPlayers);
    }

    @Override
    public void setZombiesLocations(final int zombiesLocations) {
        checkBounds(
                0,
                Integer.MAX_VALUE,
                zombiesLocations,
                "Invalid number for zombies for locations specified: %d"
                        .formatted(zombiesLocations));
        this.zombiesLocations = Optional.of(zombiesLocations);
        modelBuilder.setZombiesLocations(zombiesLocations);
    }

    @Override
    public void setZombiesColony(final int zombiesColony) {
        checkBounds(
                0,
                Integer.MAX_VALUE,
                zombiesColony,
                "Invalid number for zombies in colony specified: %d".formatted(zombiesColony));
        this.zombiesColony = Optional.of(zombiesColony);
        modelBuilder.setZombiesColony(zombiesColony);
    }

    @Override
    public void setChildrenInColony(final int childrenInColony) {
        checkBounds(
                0,
                Integer.MAX_VALUE,
                childrenInColony,
                "Invalid number for children in colony specified: %d".formatted(childrenInColony));
        this.childrenInColony = Optional.of(childrenInColony);
        modelBuilder.setChildrenInColony(childrenInColony);
    }

    @Override
    public void setConfigPath(final Path configPath) {
        this.configPath = Optional.of(configPath);
        modelBuilder.setConfigPath(configPath);
    }

    @Override
    public void setSeed(final long seed) {
        this.seed = Optional.of(seed);
        modelBuilder.setSeed(seed);
    }

    @Override
    public void setMoral(final int moral) {
        checkBounds(
                1,
                Integer.MAX_VALUE,
                moral,
                "Invalid number for moral specified: %d".formatted(moral));
        this.moral = Optional.of(moral);
        modelBuilder.setMoral(moral);
    }

    @Override
    public void setRounds(final int rounds) {
        checkBounds(
                1,
                Integer.MAX_VALUE,
                rounds,
                "Invalid number for number of rounds specified: %d".formatted(rounds));
        this.rounds = Optional.of(rounds);
        modelBuilder.setRounds(rounds);
    }

    @Override
    public M build() {
        checkCardIds();
        checkAllSettersCalled();
        checkEnoughSurvivors();
        checkDuplicateSurvivors();
        checkEnoughCardsForLocations();
        checkEnoughCrisisCards();
        checkEnoughStartCards();
        checkDistinctSocialStatus();
        checkLocationCapacity();
        checkColonyCapacity();
        checkBarricadesGoal();
        checkLocationWithZombiesGoal();
        checkReferencedLocations();
        checkEnoughCrossroads();
        checkCrossroadIds();
        return modelBuilder.build();
    }

    /**
     * Checks that there are not more initial zombies at a location
     * than possible.
     */
    private void checkLocationCapacity() {
        final OptionalInt minEntrancesLocations =
                entrancesForLocation.entrySet().stream()
                        .filter(entry -> entry.getKey() != colonyId)
                        .mapToInt(
                                entry -> {
                                    return entry.getValue();
                                })
                        .min();
        minEntrancesLocations.ifPresent(
                minEntrances -> {
                    if (zombiesLocations.get() > minEntrances * SPAWN_CAPACITY) {
                        throw new IllegalArgumentException("Too many initial zombies for location");
                    }
                });
    }

    /**
     * Checks that there are not more initial zombies at the colony
     * than possible.
     */
    private void checkColonyCapacity() {
        final int entrancesColony = entrancesForLocation.get(colonyId);
        if (zombiesColony.get() > entrancesColony * SPAWN_CAPACITY) {
            throw new IllegalArgumentException("Too many initial zombies for colony");
        }
    }

    /**
     * Checks that the locationWithZombies-goal does not require more
     * locations to be zombie-free than there are locations.
     */
    private void checkLocationWithZombiesGoal() {
        if (locationWithZombies.isEmpty()) {
            return;
        }
        if (locationWithZombies.get() > idsForLocation.size()) {
            throw new IllegalArgumentException(
                    "Unreachable goal. Location with zombies is greater than number of actual"
                            + " locations");
        }
    }

    /**
     * Checks that the barricades-goal does not require more barricades
     * than there are spaces for barricades.
     */
    private void checkBarricadesGoal() {
        if (barricades.isEmpty()) {
            return;
        }
        final int maxBarricadeCapacity =
                entrancesForLocation.entrySet().stream()
                        .mapToInt(
                                entry -> {
                                    return entry.getValue();
                                })
                        .sum()
                        * SPAWN_CAPACITY;
        if (barricades.get() > maxBarricadeCapacity) {
            throw new IllegalArgumentException(
                    "Unreachable goal. Too many initial zombies for colony");
        }
    }

    /**
     * Checks that an id is not inside a given collection.
     *
     * @param id         The id to check.
     * @param collection The collection to check.
     * @param error      The error message to throw if the collection does contain the id.
     */
    private void checkUniqueId(
            final int id, final Collection<Integer> collection, final String error) {
        final Set<Integer> set = new HashSet<>(collection);
        if (set.contains(id)) {
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * Checks that survivor-ids are unique.
     */
    private void checkDuplicateSurvivors() {
        checkDuplicates(survivorIds, "Survivor ids are required to be unique");
    }

    /**
     * Checks that card ids are unique, not referenced in multiple locations
     * and all referenced cards actually exist.
     */
    private void checkCardIds() {
        checkDuplicates(cardIds, "Card ids are required to be unique");
        for (final var entry : idsForLocation.entrySet()) {
            final List<Integer> ids = entry.getValue();
            checkDuplicates(ids, "A card is not allowed to be referenced from multiple contexts");
            checkSubset(cardIds, ids, "Unknown card for location: %d".formatted(entry.getKey()));
        }
        checkDuplicates(
                referencedCards,
                "A card is not allowed to be referenced from multiple contexts cross locations");
    }

    /**
     * Checks that social statuses of survivors are unique.
     */
    private void checkDistinctSocialStatus() {
        checkDuplicates(socials, "No two survivors are allowed to have the same social status");
    }

    /**
     * Checks that there are enough start cards.
     */
    private void checkEnoughStartCards() {
        final int numCards = idsForLocation.get(colonyId).size();
        if (numCards < maxPlayers.get() * 5) {
            throw new IllegalArgumentException("Not enough start cards: %d".formatted(numCards));
        }
    }

    /**
     * Checks that there are enough crisis cards.
     */
    private void checkEnoughCrisisCards() {
        if (crisesIds.size() < rounds.get()) {
            throw new IllegalArgumentException(
                    "Not enough crisis cards for rounds: %d for %d"
                            .formatted(crisesIds.size(), rounds.get()));
        }
    }

    private void checkEnoughCrossroads() {

        if (crossroadsIds.size() < maxPlayers.get() * rounds.get()) {
            throw new IllegalArgumentException(
                    "Not enough crisis cards for rounds: %d for %d"
                            .formatted(crisesIds.size(), rounds.get()));
        }
    }

    private void checkCrossroadIds() {
        checkDuplicates(crossroadsIds, "Card ids are required to be unique");
    }

    /**
     * Checks that there are enough cards per location.
     */
    private void checkEnoughCardsForLocations() {
        for (final var entry : idsForLocation.entrySet()) {
            final int numCards = entry.getValue().size();
            if (numCards < maxPlayers.get() * 5) {
                throw new IllegalArgumentException(
                        "Not enough cards for location: %d in %d"
                                .formatted(numCards, entry.getKey()));
            }
        }
    }

    /**
     * Checks that there enough survivors.
     */
    private void checkEnoughSurvivors() {
        if (survivorIds.size() < maxPlayers.get() * 2 + 2) {
            throw new IllegalArgumentException(
                    "Not enough survivors specified %d".formatted(survivorIds.size()));
        }
    }

    /**
     * Checks that model is ready to build (all objects have been set).
     */
    private void checkAllSettersCalled() {
        if (maxPlayers.isEmpty()) {
            throw new IllegalArgumentException("Max number of players not set");
        }
        if (zombiesLocations.isEmpty()) {
            throw new IllegalArgumentException("Initial number of zombies for locations not set");
        }
        if (zombiesColony.isEmpty()) {
            throw new IllegalArgumentException("Initial number of zombies for colony not set");
        }
        if (childrenInColony.isEmpty()) {
            throw new IllegalArgumentException("Initial number of children for colony not set");
        }
        if (configPath.isEmpty()) {
            throw new IllegalArgumentException("Config path not set");
        }
        if (seed.isEmpty()) {
            throw new IllegalArgumentException("Seed not set");
        }
        if (moral.isEmpty()) {
            throw new IllegalArgumentException("Initial moral not set");
        }
        if (rounds.isEmpty()) {
            throw new IllegalArgumentException("Number of rounds not set");
        }
    }

    /**
     * Checks that a value is inside a given interval.
     *
     * @param lower        The interval's lower bound
     * @param upper        The interval's upper bound.
     * @param value        The value to check.
     * @param errorMessage The error message to throw if the value is not inside the
     *                     interval
     */
    private void checkBounds(
            final int lower, final int upper, final int value, final String errorMessage) {
        if (value < lower || value >= upper) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Checks that a given collection is a subset of another.
     *
     * @param set          The whole set.
     * @param subset       The partial set.
     * @param errorMessage The error message to throw is the partial set is not
     *                     a subset of the whole set.
     * @param <T>          The generic type of the sets.
     */
    private <T> void checkSubset(
            final Collection<T> set, final Collection<T> subset, final String errorMessage) {
        final boolean isSubset = set.containsAll(subset);
        if (!isSubset) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Checks that a collections entries are unique.
     *
     * @param collection   The collection to check.
     * @param errorMessage The error message to throw if the elements are not contain
     *                     duplicates.
     * @param <T>          The generic type of the collection.
     */
    private <T> void checkDuplicates(final Collection<T> collection, final String errorMessage) {
        final List<T> duplicates =
                collection.stream()
                        .collect(Collectors.groupingBy(Function.identity()))
                        .entrySet()
                        .stream()
                        .filter(e -> e.getValue().size() > 1)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("%s (%s)".formatted(errorMessage, duplicates));
        }
    }
}
