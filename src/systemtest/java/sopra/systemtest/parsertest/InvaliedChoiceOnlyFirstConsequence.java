package sopra.systemtest.parsertest;

import sopra.comm.TimeoutException;
import sopra.systemtest.api.SystemTest;
import sopra.systemtest.api.Utils;

import java.util.Set;

public class InvaliedChoiceOnlyFirstConsequence extends SystemTest {

    public InvaliedChoiceOnlyFirstConsequence() {
        super(InvaliedChoiceOnlyFirstConsequence.class, true);
    }



    @Override
    protected String createConfig() {
        return Utils.loadResource(InvaliedCrossroadId.class,
                "wrongchoice.json");
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
