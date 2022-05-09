package sopra.systemtest.parsertest;

import sopra.comm.FoodChange;
import sopra.comm.TimeoutException;
import sopra.systemtest.api.SystemTest;
import sopra.systemtest.api.Utils;

import java.util.Set;

public class ChoiceTest extends SystemTest {

    public ChoiceTest() {
        super(ChoiceTest.class, false);
    }


    @Override
    protected String createConfig() {
        return Utils.loadResource(InvaliedCrossroadId.class,
                "choice.json");
    }

    @Override
    protected long createSeed() {
        return 60;
    }

    @Override
    protected Set<Integer> createSockets() {
        return Set.of(1);
    }

    @Override
    protected void run() throws TimeoutException, AssertionError {
        // Initialise game
        this.sendRegister(1, "Ahmad");
        this.assertEvent(1); // Config
        this.assertGameStarted(1);
        this.assertPlayer(1, 0, "Ahmad");
        this.assertCharacters(1, 1, 4, 2, 3);
        this.sendSelectCharacters(1, 3, 2);
        this.assertCharacterSpawned(1, 0, 2);
        this.assertCharacterSpawned(1, 0, 3);
        this.assertCardDrawn(1, 0, 1002);
        this.assertCardDrawn(1, 0, 1004);
        this.assertCardDrawn(1, 0, 1003);
        this.assertCardDrawn(1, 0, 1001);
        this.assertCardDrawn(1, 0, 1000);
        // start next round
        this.assertNextRound(1, 2);
        this.assertCrisis(1, 11);
        this.assertDieRolled(1, 0, 1);
        this.assertDieRolled(1, 0, 2);
        this.assertDieRolled(1, 0, 2);
        // player phase
        this.assertActNow(1);
        this.sendUseCard(1, 1000);
        this.assertCardUsed(1, 1000);
        this.assertWasteChanged(1, 1);
        this.assertFoodChanged(1, 1, FoodChange.FOOD_ADDED);
        this.assertCrossroad(1, 1);
        this.assertVoteNow(1);
        this.sendVote(1, false);
        this.assertVoteResult(1, false);
        this.assertFoodChanged(1, 1, FoodChange.CROSSROAD);
        this.assertActNow(1);


        this.sendLeave(1);
        this.assertLeft(1, 0);



    }
}