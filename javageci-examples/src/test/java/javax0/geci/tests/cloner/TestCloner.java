package javax0.geci.tests.cloner;

import javax0.geci.cloner.Cloner;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestCloner {

    @Test
    void testCloner() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(
                        maven().module("javageci-examples")
                                .mainSource())
                        .register(Cloner.builder().build())
                        .generate(),
                geci.failed()
        );
    }
}
