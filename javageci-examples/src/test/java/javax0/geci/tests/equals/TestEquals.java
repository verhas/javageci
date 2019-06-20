package javax0.geci.tests.equals;

import javax0.geci.engine.Geci;
import javax0.geci.equals.Equals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestEquals {

    @Test
    public void testEquals() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(
                        maven()
                                .module("javageci-examples")
                                .mainSource())
                        .register(Equals.builder().build())
                        .generate(),
                geci.failed());
    }
}
