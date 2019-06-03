package javax0.geci.tutorials.hello;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestHelloWorld3 {

    @Test
    @DisplayName("Start code generator for HelloWorld3")
    void testGenerateCode() throws Exception {
        Assertions.assertFalse(new Geci()
                .register(new HelloWorldGenerator3())
                .generate(), Geci.FAILED);
    }
}
