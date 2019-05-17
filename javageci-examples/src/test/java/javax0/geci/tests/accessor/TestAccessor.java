//START SNIPPET TestAccessor
package javax0.geci.tests.accessor;

import javax0.geci.accessor.Accessor;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestAccessor {

    @Test
    public void testAccessor() throws Exception {
        Assertions.assertFalse(new Geci().source(maven()
                        .module("javageci-examples").mainSource())
                        .register(new Accessor()).generate(),
                Geci.FAILED);
    }
}
//END SNIPPET