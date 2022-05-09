package sopra.systemtest.parsertest;

import sopra.comm.FoodChange;
import sopra.comm.MoralChange;
import sopra.comm.TimeoutException;
import sopra.systemtest.api.Utils;

import java.util.Set;

public class VoteInTwoTurns extends HugeSystemTest {

    public VoteInTwoTurns() {
        super(VoteInTwoTurns.class, false);
    }



    @Override
    protected String createConfig() {
        return Utils.loadResource(InvaliedCrossroadId.class,
                "GameOffTriggerOf.json");
    }

    @Override
    protected long createSeed() {
        return 60;
    }

    @Override
    protected Set<Integer> createSockets() {
        return Set.of(1, 2);
    }

    @Override
    protected void run() throws TimeoutException, AssertionError {
        //Initialise game
        registerTwoPlayers();

        roundAndDice();
        //player phase
        this.assertActNow(1);
        this.sendUseCard(1, 1003);
        this.assertCardUsed(1, 1003);
        this.assertCardUsed(2, 1003);
        this.assertWasteChanged(1, 1);
        this.assertWasteChanged(2, 1);
        this.assertFoodChanged(1, 1, FoodChange.FOOD_ADDED);
        this.assertFoodChanged(2, 1, FoodChange.FOOD_ADDED);
        this.assertCrossroad(1, 2);
        this.assertCrossroad(2, 2);
        this.assertVoteNow(1);
        this.sendVote(1, true);
        this.assertVoteNow(2);
        this.sendVote(2, false);
        this.assertVoteResult(1, true);
        this.assertVoteResult(2, true);
        this.assertMoralChanged(1, 3, MoralChange.CROSSROAD);
        this.assertMoralChanged(2, 3, MoralChange.CROSSROAD);
        this.sendEndTurn(1);
        this.assertEvent(1);


        this.assertActNow(2);
        this.sendUseCard(2, 1000);
        this.assertCardUsed(1, 1000);
        this.assertCardUsed(2, 1000);
        this.assertWasteChanged(1, 1);
        this.assertWasteChanged(2, 1);
        this.assertFoodChanged(1, 1, FoodChange.FOOD_ADDED);
        this.assertFoodChanged(2, 1, FoodChange.FOOD_ADDED);
        this.assertCrossroad(1, 1);
        this.assertCrossroad(2, 1);
        this.assertVoteNow(1);
        this.sendVote(1, false);
        this.assertVoteNow(2);
        this.sendVote(2, false);
        this.assertVoteResult(1, false);
        this.assertVoteResult(2, false);
        this.assertFoodChanged(1, 1, FoodChange.CROSSROAD);
        this.assertFoodChanged(2, 1, FoodChange.CROSSROAD);

        this.assertActNow(2);
        this.sendLeave(2);
        this.assertLeft(2, 1);






    }
}
