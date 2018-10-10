package javax0.geci.tutorials.simplest;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("SameReturnValue")
@javax0.geci.annotations.Geci("HelloWorldTest")
public class HelloWorldTest3 {

    @Test
    public void generateOkay() throws Exception {
        if (new Geci().source("src/test/java").register(new HelloWorldTestGenerator1()).generate()) {
            Assertions.fail("Code was regenerated");
        }
    }

// START SNIPPET HelloWorldTest3
    @Test
    public void generatedMethodReturnsGreetings() {
        Assertions.assertEquals("greetings", greeting());
    }
// END SNIPPET

    //<editor-fold id="HelloWorldTest">
    private static String greeting(){
        return "greetings";
        }
    //</editor-fold>
}
