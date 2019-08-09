package javax0.geci.tests.delegator;

import javax0.geci.delegator.Delegator;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestDelegator {

    // snippet TestDelegator
    @Test
    public void testDelegator() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven()
                        .module("javageci-examples")
                        .mainSource())
                        .register(Delegator.builder().build())
                        .generate(),
                geci.failed());
    }
    // end snippet
}
