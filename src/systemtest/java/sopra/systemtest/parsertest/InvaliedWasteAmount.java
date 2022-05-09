package sopra.systemtest.parsertest;

import sopra.comm.TimeoutException;
import sopra.systemtest.api.SystemTest;
import sopra.systemtest.api.Utils;

import java.util.Set;

public class InvaliedWasteAmount extends SystemTest {

    public InvaliedWasteAmount() {
        super(InvaliedWasteAmount.class, true);
    }




    @Override
    protected String createConfig() {
        return Utils.loadResource(InvaliedCrossroadId.class,
                "invaliedwasteamount.json");
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
        //do nothing



    }
}
