package sopra.systemtest.parsertest;

import sopra.comm.TimeoutException;
import sopra.systemtest.api.SystemTest;


public abstract class HugeSystemTest extends SystemTest {

    protected HugeSystemTest(final Class<?> subclass, final boolean fail) {
        super(subclass, fail);
    }

    public void registerTwoPlayers() throws TimeoutException {
        this.sendRegister(1, "Grandpa");
        this.assertEvent(1); // Config
        this.sendRegister(2, "Ahmad");
        this.assertEvent(2);
        this.assertGameStarted(1);
        this.assertGameStarted(2);

        this.assertPlayer(1, 0, "Grandpa");
        this.assertPlayer(2, 0, "Grandpa");
        this.assertPlayer(1, 1, "Ahmad");
        this.assertPlayer(2, 1, "Ahmad");

        this.assertCharacters(1, 2, 4, 5, 1);
        this.sendSelectCharacters(1, 4, 2);


        this.assertCharacterSpawned(1, 0, 2);
        this.assertCharacterSpawned(1, 0, 4);
        this.assertCharacterSpawned(2, 0, 2);
        this.assertCharacterSpawned(2, 0, 4);


        this.assertCardDrawn(1, 0, 1019);
        this.assertCardDrawn(2, 0, 1019);

        this.assertCardDrawn(1, 0, 1003);
        this.assertCardDrawn(2, 0, 1003);

        this.assertCardDrawn(1, 0, 1018);
        this.assertCardDrawn(2, 0, 1018);

        this.assertCardDrawn(1, 0, 1001);
        this.assertCardDrawn(2, 0, 1001);

        this.assertCardDrawn(1, 0, 1016);
        this.assertCardDrawn(2, 0, 1016);

        this.assertCharacters(2, 6, 1, 3, 5);
        this.sendSelectCharacters(2, 1, 3);

        this.assertCharacterSpawned(1, 1, 1);
        this.assertCharacterSpawned(1, 1, 3);
        this.assertCharacterSpawned(2, 1, 1);
        this.assertCharacterSpawned(2, 1, 3);

        this.assertCardDrawn(1, 1, 1002);
        this.assertCardDrawn(2, 1, 1002);

        this.assertCardDrawn(1, 1, 1000);
        this.assertCardDrawn(2, 1, 1000);

        this.assertCardDrawn(1, 1, 1017);
        this.assertCardDrawn(2, 1, 1017);

        this.assertCardDrawn(1, 1, 1004);
        this.assertCardDrawn(2, 1, 1004);

        this.assertCardDrawn(1, 1, 1015);
        this.assertCardDrawn(2, 1, 1015);
    }

    public void roundAndDice() throws TimeoutException {
        this.assertNextRound(1, 1);
        this.assertNextRound(2, 1);

        this.assertCrisis(1, 13);
        this.assertCrisis(2, 13);

        this.assertDieRolled(1, 0, 1);
        this.assertDieRolled(1, 0, 3);
        this.assertDieRolled(1, 0, 4);

        this.assertDieRolled(2, 0, 1);
        this.assertDieRolled(2, 0, 3);
        this.assertDieRolled(2, 0, 4);

        this.assertDieRolled(1, 1, 2);
        this.assertDieRolled(1, 1, 1);
        this.assertDieRolled(1, 1, 4);

        this.assertDieRolled(2, 1, 2);
        this.assertDieRolled(2, 1, 1);
        this.assertDieRolled(2, 1, 4);
    }

}
