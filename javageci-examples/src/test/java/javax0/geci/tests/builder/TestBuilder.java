package javax0.geci.tests.builder;

import javax0.geci.builder.Builder;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestBuilder {

    @Test
    void buildGenerators() throws Exception {
        Assertions.assertFalse(
                new Geci().source("./javageci-core/src/main/java/", "../javageci-core/src/main/java/").register(Builder.builder().generatedAnnotation(null).build()).generate(),
                Geci.FAILED
        );
    }

    @Test
    void testBuilder() throws Exception {
        Assertions.assertFalse(
                new Geci()
                        .source(
                                maven().module("javageci-examples")
                                        .mainSource())
                        .register(Builder.builder().build())
                        .generate(),
                Geci.FAILED
        );
    }
}
