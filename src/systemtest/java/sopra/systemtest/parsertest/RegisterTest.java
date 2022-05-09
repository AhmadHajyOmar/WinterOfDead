package sopra.systemtest.parsertest;

import sopra.comm.TimeoutException;
import sopra.systemtest.api.SystemTest;
import sopra.systemtest.api.Utils;

import java.util.Set;

public class RegisterTest extends SystemTest {

    public RegisterTest() {
        super(RegisterTest.class, false);
    }

    @Override
    protected String createConfig() {
        return Utils.loadResource(RegisterTest.class, "configuration.json");
    }

    @Override
    protected long createSeed() {
        return 55;
    }

    @Override
    protected Set<Integer> createSockets() {
        return Set.of(1, 2, 3, 4, 5, 6);
    }

    @Override
    protected void run() throws TimeoutException, AssertionError {

        //Registrierung
        this.sendRegister(1, "Lisa");
        this.assertEvent(1); //Config

        this.sendLeave(1);
        this.assertRegistrationAborted(1);
    }
}
