package javax0.geci.engine;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestNoGenerator {

    public static class NoGenerator implements Generator {

        @Override
        public void process(Source source) {

        }
    }

    @Test
    @DisplayName("When a generator does not touch any source it throws GeciException")
    void testNoGeneratorThrowing() {
        Assertions.assertThrows(GeciException.class, () -> new Geci()
                .source("../javageci-examples/src/main/java", "./javageci-examples/src/main/java")
                .register(new NoGenerator())
                .generate());
    }

}
