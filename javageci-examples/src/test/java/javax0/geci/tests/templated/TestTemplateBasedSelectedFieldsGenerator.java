package javax0.geci.tests.templated;

import javax0.geci.engine.Geci;
import javax0.geci.templated.TemplateBasedSelectedMemberGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestTemplateBasedSelectedFieldsGenerator {

    private static final String TEMPLATE_DIR = "javax0/geci/tests/templated/";

    @Test
    public void testTemplated() throws Exception {
        Assertions.assertFalse(
            new Geci().source(maven().module("javageci-examples").mainSource())
                .register(
                    TemplateBasedSelectedMemberGenerator
                        .builder()
                        .selector("needs")
                        .preprocess(TEMPLATE_DIR + "preprocess.java")
                        .processField(TEMPLATE_DIR + "processField.java")
                        .processFields(TEMPLATE_DIR + "processFields.java")
                        .postprocess(TEMPLATE_DIR + "postprocess.java")
                        .selector("needed")
                        .processField(TEMPLATE_DIR + "processFieldNeeded.java")
                        .build()
                ).generate(),
            Geci.FAILED);
    }
}
