package javax0.geci.jamal_test;

import javax0.geci.engine.Geci;
import javax0.geci.jamal.JamalGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestJamalGenerator {

    @Test
    public void testJamalGenerator() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven().module("javageci-jamal-test").testSource())
                        .register(new JamalGenerator())
                        .generate(),
                geci.failed());
    }
}
