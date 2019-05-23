package javax0.geci.tests.builder;

import javax0.geci.api.Source;
import javax0.geci.builder.Builder;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBuilder {

    @Test
    void testBuilder() throws Exception {
        Assertions.assertFalse(
                new Geci().source(Source.maven().module("java-core").mainSource()).register(new Builder()).generate(),
                Geci.FAILED
        );
    }
}
