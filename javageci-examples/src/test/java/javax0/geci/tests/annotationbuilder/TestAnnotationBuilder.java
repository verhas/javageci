package javax0.geci.tests.annotationbuilder;

import javax0.geci.annotationbuilder.AnnotationBuilder;
import javax0.geci.api.Source;
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
                .source(Source.Set.set("annotation-output"),
                    "./javageci-core-annotations/src/main/java",
                    "../javageci-core-annotations/src/main/java")
                .register(AnnotationBuilder.builder()
                    .set("annotation-output")
                    .absolute("yes")
                    .in("javax0.geci.core.annotations")
                    .build())
                .generate(),
            geci.failed());
    }
}
