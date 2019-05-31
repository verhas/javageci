package javax0.geci.tests.cloner;

import javax0.geci.cloner.Cloner;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestCloner {

    @Test
    void testCloner() throws Exception {
        Assertions.assertFalse(
                new Geci()
                        .source(
                                maven().module("javageci-examples")
                                        .mainSource())
                        .register(Cloner.builder().build())
                        .generate(),
                Geci.FAILED
        );
    }
}
