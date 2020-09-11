package javax0.geci.tutorials.use;

import javax0.geci.accessor.Accessor;
import javax0.geci.builder.Builder;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestUseExamples {
    @Test
    public void testUsageExamples() throws Exception {
        Geci geci;
        Assertions.assertFalse(
                (geci = new Geci())
                        .only("^.*/ExampleWith.*.java$")
                        .register(Accessor.builder().build())
                        .register(Builder.builder().build())
                        .generate(),
                geci.failed());
    }
}
