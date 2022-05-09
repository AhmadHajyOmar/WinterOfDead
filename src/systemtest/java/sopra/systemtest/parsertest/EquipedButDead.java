package sopra.systemtest.parsertest;

import sopra.comm.MoralChange;
import sopra.comm.TimeoutException;
import sopra.systemtest.api.SystemTest;
import sopra.systemtest.api.Utils;

import java.util.Set;

public class EquipedButDead extends SystemTest {

    public EquipedButDead() {
        super(EquipedButDead.class, false);
    }



    @Override
    protected String createConfig() {
        return Utils.loadResource(InvaliedCrossroadId.class,
                "equip.json");
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
        this.sendUseCard(1, 1001, 2, 3);
        this.assertCardUsed(1, 1001, 2, 3);
        this.assertCrossroad(1, 1);
        this.assertMoralChanged(1, -2, MoralChange.CROSSROAD);
        this.assertGameEnd(1, false);


    }
}
