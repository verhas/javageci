package javax0.geci.tests.templated;

import javax0.geci.engine.Geci;
import javax0.geci.templated.Templated;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestTemplated {

    private static final String TEMPLATE_DIR = "javax0/geci/tests/templated/";

    @Test
    void testTemplated() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven().module("javageci-examples").mainSource())
                        .register(
                                Templated
                                        .builder()
                                        .selector("needs")
                                        .preprocess(TEMPLATE_DIR + "preprocess.java")
                                        .processField(TEMPLATE_DIR + "processField.java")
                                        .processFields(TEMPLATE_DIR + "processFields.java")
                                        .postprocess(TEMPLATE_DIR + "postprocess.java")
                                        .selector("needed")
                                        .processField(TEMPLATE_DIR + "processFieldNeeded.java")
                                        .preprocessParams((ctx) -> ctx.segment().param("wuff", "112"))
                                        .build()
                        ).generate(),
                geci.failed());
    }
}
