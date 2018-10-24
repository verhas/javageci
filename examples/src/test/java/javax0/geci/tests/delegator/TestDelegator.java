package javax0.geci.tests.delegator;

import javax0.geci.delegator.Delegator;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestDelegator {

    @Test
    public void testDelegator() throws Exception {
        if (new Geci().source(maven().module("examples").javaSource()).register(new Delegator()).generate()) {
            Assertions.fail("Delegator modified source code. Please compile again.");
        }
    }
}
