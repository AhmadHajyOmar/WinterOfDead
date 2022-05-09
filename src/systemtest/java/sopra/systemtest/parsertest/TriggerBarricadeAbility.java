package sopra.systemtest.parsertest;

import sopra.comm.FoodChange;
import sopra.comm.MoralChange;
import sopra.comm.TimeoutException;
import sopra.systemtest.api.SystemTest;
import sopra.systemtest.api.Utils;

import java.util.Set;

public class TriggerBarricadeAbility extends SystemTest {

    public TriggerBarricadeAbility() {
        super(TriggerBarricadeAbility.class, false);
    }


    @Override
    protected String createConfig() {
        return Utils.loadResource(InvaliedCrossroadId.class,
                "BarricadeAbilityTwo.json");
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
        this.assertCharacters(1, 1, 3, 2, 4);
        this.sendSelectCharacters(1, 3, 2);
        this.assertCharacterSpawned(1, 0, 2);
        this.assertCharacterSpawned(1, 0, 3);
        this.assertCardDrawn(1, 0, 1002);
        this.assertCardDrawn(1, 0, 1004);
        this.assertCardDrawn(1, 0, 1003);
        this.assertCardDrawn(1, 0, 1001);
        this.assertCardDrawn(1, 0, 1000);
        // start next round
        this.assertNextRound(1, 3);
        this.assertCrisis(1, 11);
        this.assertDieRolled(1, 0, 2);
        this.assertDieRolled(1, 0, 4);
        this.assertDieRolled(1, 0, 1);
        // player phase
        this.assertActNow(1);
        this.sendUseAbility(1, 2, 0);
        this.assertAbilityUsed(1, 2, 0);
        this.assertCrossroad(1, 1);
        this.assertFoodChanged(1, 2, FoodChange.CROSSROAD);
        this.assertActNow(1);
        this.sendEndTurn(1);
        this.assertColonyPhaseStarted(1);
        this.assertFoodChanged(1, -1, FoodChange.FOOD_CONSUMED);
        this.assertMoralChanged(1, -2, MoralChange.CRISIS);
        this.assertBarricadeDestroyed(1, 42, 0);

        this.assertNextRound(1, 2);
        this.assertCrisis(1, 12);
        this.assertDieRolled(1, 0, 5);
        this.assertDieRolled(1, 0, 3);
        this.assertDieRolled(1, 0, 5);
        this.assertActNow(1);
        this.sendUseAbility(1, 2, 0);
        this.assertAbilityUsed(1, 2, 0);
        this.assertCrossroad(1, 3);
        this.assertMoralChanged(1, 2, MoralChange.CROSSROAD);
        this.assertActNow(1);

        this.sendLeave(1);
        this.assertLeft(1, 0);




    }
}
