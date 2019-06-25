package javax0.geci.tests.annotationbuilder;

import javax0.geci.annotationbuilder.AnnotationBuilder;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestAnnotationBuilder {

    @Test
    public void testAnnotationBuilder() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
            geci.source(
                    "./javageci-core/src/main/java/",
                    "../javageci-core/src/main/java/")
                .source(
                    "./javageci-core-annotations/src/main/java/",
                    "../javageci-core-annotations/src/main/java/")
                .register(AnnotationBuilder.builder().build())
                .generate(),
            geci.failed());
    }
}
