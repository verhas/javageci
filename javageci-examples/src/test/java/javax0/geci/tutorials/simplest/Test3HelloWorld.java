package javax0.geci.tutorials.simplest;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("SameReturnValue")
@javax0.geci.annotations.Geci("HelloWorldTest")
public class Test3HelloWorld {

    @Test
    public void generateOkay() throws Exception {
        if (new Geci().source("src/test/java").register(new HelloWorldTestGenerator1()).generate()) {
            Assertions.fail("Code was regenerated");
        }
    }

// snippet Test3HelloWorld
    @Test
    public void generatedMethodReturnsGreetings() {
        Assertions.assertEquals("greetings", greeting());
    }
// end snippet

    //<editor-fold id="HelloWorldTest">
    private static String greeting() {
        return "greetings";
    }
    //</editor-fold>
}
