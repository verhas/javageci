package javax0.geci.jdocify;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;
import static javax0.geci.engine.Geci.JAVA_COMPARATOR_COMMENT;

public class TestRunJdocify {

    @Test
    void rundJdocify() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven("..").module("javageci-engine").mainSource())
                        .comparator(JAVA_COMPARATOR_COMMENT)
                        .only("FieldsGenerator.java$")
                        .only("Geci")
                        .register(Jdocify.builder().processAllClasses(true))
                        .generate()
                , geci.failed()
        );
    }
}
