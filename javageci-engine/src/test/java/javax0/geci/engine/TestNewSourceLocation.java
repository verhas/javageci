package javax0.geci.engine;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static javax0.geci.api.Source.Set.set;

public class TestNewSourceLocation {


    public static class NewSource implements Generator {

        @Override
        public void process(Source source) {
            final var newSource1 = source.newSource("extraFile.txt");
            final var newSource2 = source.newSource("extraFile.txt");
            Assertions.assertSame(newSource1, newSource2);
            final var newSource3 = source.newSource(set("extra-sources"),"extraFile.txt");
            final var newSource4 = source.newSource(set("extra-sources"),"extraFile.txt");
            Assertions.assertSame(newSource3, newSource4);
        }
    }

    /**
     * This test just starts a generator and all the assertions are inside the generator.
     *
     * @throws IOException
     */
    @Test
    @DisplayName("Test inside the generator that the new source is okay.")
    void testGeneratorPassesAllAssertions() throws IOException {
        // we do not touch any source in this test
        Assertions.assertThrows(GeciException.class, () -> new Geci()
                .source("../javageci-examples/src/main/java", "./javageci-examples/src/main/java")
                .source(set("extra-sources"),"../")
                .output(set("extra-sources"))
                .register(new NewSource())
                .generate());
    }
}
