//snippet TestChainedAccessor
package javax0.geci.tests.accessor;

import javax0.geci.accessor.ChainedAccessor;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestChainedAccessor {

    @Test
    public void testAccessor() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(geci.source(maven()
                        .module("javageci-examples").mainSource())
                        .register(ChainedAccessor.builder().build()).generate(),
                geci.failed());
    }
}
//end snippet