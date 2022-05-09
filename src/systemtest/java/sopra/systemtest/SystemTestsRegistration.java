package sopra.systemtest;

import sopra.systemtest.api.SystemTestManager;
import sopra.systemtest.parsertest.ChoiceTest;
import sopra.systemtest.parsertest.CleanWasteTrigger;
import sopra.systemtest.parsertest.CrossroadCausesHunger;
import sopra.systemtest.parsertest.EquipedButDead;
import sopra.systemtest.parsertest.GrandpaCanNotVote;
import sopra.systemtest.parsertest.InvaliedChoiceOnlyFirstConsequence;
import sopra.systemtest.parsertest.InvaliedChoiceSecondConsequence;
import sopra.systemtest.parsertest.InvaliedCrossroadId;
import sopra.systemtest.parsertest.InvaliedLocationId;
import sopra.systemtest.parsertest.InvaliedSpawnSurvivor;
import sopra.systemtest.parsertest.InvaliedSpawnZombie;
import sopra.systemtest.parsertest.InvaliedSpawnZombieWithoutLocation;
import sopra.systemtest.parsertest.InvaliedWasteAmount;
import sopra.systemtest.parsertest.InvaliedZombieLocation;
import sopra.systemtest.parsertest.MoveTest;
import sopra.systemtest.parsertest.MovedTriggerInSecondMoveAction;
import sopra.systemtest.parsertest.NoSearchNoSearched;
import sopra.systemtest.parsertest.NoSearchedIfRandomEncounter;
import sopra.systemtest.parsertest.SearchedUsingBlueprintButLost;
import sopra.systemtest.parsertest.SearchedWithBlueprint;
import sopra.systemtest.parsertest.SearchedWithSearchAbility;
import sopra.systemtest.parsertest.TriggerBarricadeAbility;
import sopra.systemtest.parsertest.TriggerBarricadedAbility;
import sopra.systemtest.parsertest.TriggerLockCard;
import sopra.systemtest.parsertest.TriggerMovedFuelCard;
import sopra.systemtest.parsertest.VoteInTwoRounds;
import sopra.systemtest.parsertest.VoteInTwoTurns;
import sopra.systemtest.parsertest.WasteChangedAbsolut;
import sopra.systemtest.parsertest.WasteChangedAbsolut2;
import sopra.systemtest.parsertest.WasteChangedIsZero;
import sopra.systemtest.parsertest.ZombiesSpawn;

/**
 * @author Lauritz Timm (s9latimm@stud.uni-saarland.de)
 * @version 1.0.0
 */
final class SystemTestsRegistration {

    private SystemTestsRegistration() {
        // empty
    }

    static void registerDailyTests(final SystemTestManager manager) {
        manager.registerTest(new InvaliedCrossroadId());
        manager.registerTest(new InvaliedLocationId());
        manager.registerTest(new InvaliedWasteAmount());
        manager.registerTest(new InvaliedZombieLocation());
        manager.registerTest(new InvaliedSpawnSurvivor());
        manager.registerTest(new InvaliedSpawnZombie());
        manager.registerTest(new InvaliedSpawnZombieWithoutLocation());
        manager.registerTest(new InvaliedChoiceOnlyFirstConsequence());
        manager.registerTest(new InvaliedChoiceSecondConsequence());
        manager.registerTest(new NoSearchNoSearched());
        manager.registerTest(new TriggerMovedFuelCard());
        manager.registerTest(new TriggerBarricadedAbility());
        manager.registerTest(new TriggerLockCard());
        manager.registerTest(new MovedTriggerInSecondMoveAction());
        manager.registerTest(new MoveTest());
        manager.registerTest(new ChoiceTest());
        manager.registerTest(new ZombiesSpawn());
        manager.registerTest(new TriggerBarricadeAbility());
        manager.registerTest(new NoSearchedIfRandomEncounter());
        manager.registerTest(new ChoiceTest());
        manager.registerTest(new GrandpaCanNotVote());
        manager.registerTest(new VoteInTwoRounds());
        manager.registerTest(new WasteChangedAbsolut());
        manager.registerTest(new WasteChangedAbsolut2());
        manager.registerTest(new WasteChangedIsZero());
        manager.registerTest(new CrossroadCausesHunger());
        manager.registerTest(new SearchedWithBlueprint());
        manager.registerTest(new SearchedWithSearchAbility());
        manager.registerTest(new EquipedButDead());
        manager.registerTest(new SearchedUsingBlueprintButLost());
        manager.registerTest(new VoteInTwoTurns());
        manager.registerTest(new CleanWasteTrigger());






    }

    static void registerSecretTests(final SystemTestManager manager) {
        manager.registerTest(new InvaliedCrossroadId());
        manager.registerTest(new InvaliedLocationId());
        manager.registerTest(new InvaliedWasteAmount());
        manager.registerTest(new InvaliedZombieLocation());
        manager.registerTest(new InvaliedSpawnSurvivor());
        manager.registerTest(new InvaliedSpawnZombie());
        manager.registerTest(new InvaliedSpawnZombieWithoutLocation());
        manager.registerTest(new InvaliedChoiceOnlyFirstConsequence());
        manager.registerTest(new InvaliedChoiceSecondConsequence());
        manager.registerTest(new NoSearchNoSearched());
        manager.registerTest(new TriggerMovedFuelCard());
        manager.registerTest(new TriggerBarricadedAbility());
        manager.registerTest(new TriggerLockCard());
        manager.registerTest(new MovedTriggerInSecondMoveAction());
        manager.registerTest(new MoveTest());
        manager.registerTest(new ChoiceTest());
        manager.registerTest(new ZombiesSpawn());
        manager.registerTest(new TriggerBarricadeAbility());
        manager.registerTest(new NoSearchedIfRandomEncounter());
        manager.registerTest(new ChoiceTest());
        manager.registerTest(new GrandpaCanNotVote());
        manager.registerTest(new VoteInTwoRounds());
        manager.registerTest(new WasteChangedAbsolut());
        manager.registerTest(new WasteChangedAbsolut2());
        manager.registerTest(new WasteChangedIsZero());
        manager.registerTest(new CrossroadCausesHunger());
        manager.registerTest(new SearchedWithBlueprint());
        manager.registerTest(new SearchedWithSearchAbility());
        manager.registerTest(new EquipedButDead());
        manager.registerTest(new SearchedUsingBlueprintButLost());
        manager.registerTest(new VoteInTwoTurns());
        manager.registerTest(new CleanWasteTrigger());





    }

    static void registerSystemTests(final SystemTestManager manager) {
        //manager.registerTest(new RegistrationTest());
        manager.registerTest(new InvaliedCrossroadId());
        manager.registerTest(new InvaliedLocationId());
        manager.registerTest(new InvaliedWasteAmount());
        manager.registerTest(new InvaliedZombieLocation());
        manager.registerTest(new InvaliedSpawnSurvivor());
        manager.registerTest(new InvaliedSpawnZombie());
        manager.registerTest(new InvaliedSpawnZombieWithoutLocation());
        manager.registerTest(new InvaliedChoiceOnlyFirstConsequence());
        manager.registerTest(new InvaliedChoiceSecondConsequence());
        manager.registerTest(new NoSearchNoSearched());
        manager.registerTest(new TriggerMovedFuelCard());
        manager.registerTest(new TriggerBarricadedAbility());
        manager.registerTest(new TriggerLockCard());
        manager.registerTest(new MovedTriggerInSecondMoveAction());
        manager.registerTest(new MoveTest());
        manager.registerTest(new ChoiceTest());
        manager.registerTest(new ZombiesSpawn());
        manager.registerTest(new TriggerBarricadeAbility());
        manager.registerTest(new NoSearchedIfRandomEncounter());
        manager.registerTest(new ChoiceTest());
        manager.registerTest(new GrandpaCanNotVote());
        manager.registerTest(new VoteInTwoRounds());
        manager.registerTest(new WasteChangedAbsolut());
        manager.registerTest(new WasteChangedAbsolut2());
        manager.registerTest(new WasteChangedIsZero());
        manager.registerTest(new CrossroadCausesHunger());
        manager.registerTest(new SearchedWithBlueprint());
        manager.registerTest(new SearchedWithSearchAbility());
        manager.registerTest(new EquipedButDead());
        manager.registerTest(new SearchedUsingBlueprintButLost());
        manager.registerTest(new VoteInTwoTurns());
        manager.registerTest(new CleanWasteTrigger());





    }
}
