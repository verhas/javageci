package javax0.geci.annotations;

import javax0.geci.engine.Geci;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AnnotationBuilderTest {
    @Test
    @DisplayName("Should create new file with properly generated code")
    public void tambourine() throws Exception {
        Assertions.assertFalse(new Geci()
            .source("./src/main/java")
            .register(new AnnotationBuilder())
            .generate(),
            Geci.FAILED);
    }
}
