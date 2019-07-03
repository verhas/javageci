package javax0.geci.docugen;

import javax0.geci.api.GeciException;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestSnippetCollector {

    @Test
    void buildGenerators() throws Exception {
        final var geci = new Geci();
        final var generator = SnippetCollector.builder().build();
        Assertions.assertThrows(GeciException.class, () ->
                geci.source(maven().module("javageci-docugen"))
                        .register(generator)
                        .generate());
    }
}
