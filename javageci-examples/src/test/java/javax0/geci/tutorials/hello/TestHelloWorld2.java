package javax0.geci.tutorials.hello;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestHelloWorld2 {

    @Test
    @DisplayName("Start code generator for HelloWorld2")
    void testGenerateCode() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(geci
                .register(new HelloWorldGenerator2())
                .generate(), geci.failed());
    }
}
