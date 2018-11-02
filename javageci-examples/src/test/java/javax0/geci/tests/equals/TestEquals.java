package javax0.geci.tests.equals;

import javax0.geci.engine.Geci;
import javax0.geci.equals.Equals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestEquals {

    @Test
    public void testEquals() throws Exception {
        if (new Geci().source(maven().module("javageci-examples").javaSource()).register(new Equals()).generate()) {
            Assertions.fail(Geci.FAILED);
        }
    }
}
