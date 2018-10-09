package javax0.geci.tutorials.beangenerator;

import javax0.geci.api.Source;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.Set.set;

public class BeanGeneratorTest {

    @Test
    public void testBeanGenerator() throws Exception {
        if (new Geci()
            .source(set("java"),"./src/test/java", "./tests/src/test/java")
            .source(set("resources"),"./src/test/resources", "./tests/src/test/resources")
            .register(new BeanGenerator()).generate()) {
            Assertions.fail("Code was changed during test phase.");
        }
    }
}
