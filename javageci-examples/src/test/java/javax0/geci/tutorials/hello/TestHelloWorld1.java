package javax0.geci.tutorials.hello;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestHelloWorld1 {

    @Test
    @DisplayName("Start code generator for HelloWorld1")
    void testGenerateCode() throws Exception {
        Assertions.assertFalse(new Geci()
                .only("^.*/HelloWorld1.java$")
                .diffOutput("target/geci/diff")
                .register(new HelloWorldGenerator1()).generate(), Geci.FAILED);
    }
}
