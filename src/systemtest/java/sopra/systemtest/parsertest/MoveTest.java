package sopra.systemtest.parsertest;

import sopra.comm.MoralChange;
import sopra.comm.TimeoutException;
import sopra.systemtest.api.SystemTest;
import sopra.systemtest.api.Utils;

import java.util.Set;

public class MoveTest extends SystemTest {
    public MoveTest() {
        super(MoveTest.class, false);
    }


    @Override
    protected String createConfig() {
        return Utils.loadResource(InvaliedCrossroadId.class,
                "movetriggertwo.json");
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
        this.sendMove(1, 2, 6);
        this.assertMoved(1, 2, 6);
        this.assertEvent(1); // wound?
        this.assertActNow(1);

        this.sendEndTurn(1);
        this.assertColonyPhaseStarted(1);
        this.assertStarvationTokenAdded(1);
        this.assertMoralChanged(1, -1, MoralChange.STARVATION_TOKEN);
        this.assertMoralChanged(1, -2, MoralChange.CRISIS);
        this.assertChildKilled(1);
        this.assertMoralChanged(1, -1, MoralChange.CHARACTER_DIED);
        this.assertChildKilled(1);
        this.assertMoralChanged(1, -1, MoralChange.CHARACTER_DIED);
        this.assertChildKilled(1);
        this.assertMoralChanged(1, -1, MoralChange.CHARACTER_DIED);
        this.assertZombieSpawned(1, 6, 0);

        // start next round
        this.assertNextRound(1, 1);
        this.assertCrisis(1, 13);
        this.assertDieRolled(1, 0, 1);
        this.assertDieRolled(1, 0, 5);
        this.assertDieRolled(1, 0, 3);
        this.assertWounded(1, 2);
        this.assertActNow(1);
        this.sendMove(1, 2, 4);
        this.assertMoved(1, 2, 4);
        this.assertCrossroad(1, 2);
        this.assertCharacterSpawned(1, 0, 1);
        this.assertActNow(1);
        this.sendMove(1, 3, 4);
        this.assertMoved(1, 3, 4);
        this.assertEvent(1);
        this.assertActNow(1);



        this.sendLeave(1);
        this.assertLeft(1, 0);



    }
}
