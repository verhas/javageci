package javax0.geci.docugen;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestSnippetCollector {

    @Test
    void buildGenerators() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven().module("javageci-docugen"))
                        .register(SnippetCollector.builder()
                                .build())
                        .generate(),
                geci.failed()
        );
    }
}
