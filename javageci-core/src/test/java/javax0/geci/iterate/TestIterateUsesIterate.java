package javax0.geci.iterate;

import javax0.geci.api.Source;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestIterateUsesIterate {

    @Test
    void generate() throws IOException {

        Geci geci = new Geci();
        Assertions.assertFalse(geci
                                   .register(Iterate.builder().build())
                                   .source(Source.maven().module("javageci-core"))
                                   .generate(),
            geci.failed());
    }

}
