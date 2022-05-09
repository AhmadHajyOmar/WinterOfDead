package sopra.systemtest.parsertest;

import sopra.comm.TimeoutException;
import sopra.systemtest.api.SystemTest;
import sopra.systemtest.api.Utils;

import java.util.Set;

public class InvaliedLocationId extends SystemTest {

    public InvaliedLocationId() {
        super(InvaliedLocationId.class, true);
    }

    @Override
    protected String createConfig() {
        return Utils.loadResource(InvaliedCrossroadId.class,
                "configlocation.json");
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
