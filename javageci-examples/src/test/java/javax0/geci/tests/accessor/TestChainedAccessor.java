//START SNIPPET TestAccessor
package javax0.geci.tests.accessor;

import javax0.geci.accessor.ChainedAccessor;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestChainedAccessor {

    @Test
    public void testAccessor() throws Exception {
        Assertions.assertFalse(new Geci().source(maven()
                        .module("javageci-examples").mainSource())
                        .register(new ChainedAccessor()).generate(),
                Geci.FAILED);
    }
}
//END SNIPPET