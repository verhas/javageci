package javax0.geci.tutorials.beangenerator;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.Set.set;

public class BeanGeneratorTest {
    // START SNIPPET testBeanGenerator
    @Test
    public void testBeanGenerator() throws Exception {
        if (new Geci()
            .source("./src/test/resources", "./javageci-examples/src/test/resources")
            .source(set("java"),"./src/test/java", "./javageci-examples/src/test/java")
            .register(new BeanGenerator()).generate()) {
            Assertions.fail("Code was changed during test phase.");
        }
    }
    // END SNIPPET
}
