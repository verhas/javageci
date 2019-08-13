package javax0.geci.tests.annotationbuilder;

import javax0.geci.annotationbuilder.AnnotationBuilder;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

// snippet TestAnnotationBuilder
public class TestAnnotationBuilder {

    public static final String ANNOTATION_OUTPUT = "annotation-output";

    @Test
    public void testAnnotationBuilder() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
            geci.source(maven().module("javageci-core").mainSource())
                .source(ANNOTATION_OUTPUT, maven().module("javageci-core-annotations").mainSource())
                .register(AnnotationBuilder.builder()
                    .set(ANNOTATION_OUTPUT)
                    .in("javax0.geci.core.annotations")
                    .build())
                .generate(),
            geci.failed());
    }
}
// end snippet