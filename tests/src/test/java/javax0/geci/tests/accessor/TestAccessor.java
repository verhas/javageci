package javax0.geci.tests.accessor;

import javax0.geci.accessor.Accessor;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestAccessor {

    @Test
    public void testAccessor() throws Exception {
        if (new Geci().source("./src/main/java","./tests/src/main/java").register(new Accessor()).generate()) {
            Assertions.fail("Code was changed during test phase.");
        }
    }
}
