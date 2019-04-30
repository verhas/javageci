package javax0.geci.tutorials.hello;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestHelloWorld2 {

    @Test
    @DisplayName("Start code generator for HelloWorld2")
    void testGenerateCode() throws Exception {
        Assertions.assertFalse(new Geci().source(maven().mainSource())
                .register(new HelloWorldGenerator2()).generate(), Geci.FAILED);
    }
}
