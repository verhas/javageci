package javax0.geci.docugen;

import javax0.geci.config.ConfigBuilder;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class GenerateConfig {

    @Test
    void buildGenerators() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven().module("javageci-docugen"))
                        .register(ConfigBuilder.builder()
                                .generateImplementedKeys("false")
                                .build())
                        .generate(),
                geci.failed()
        );
    }
}
