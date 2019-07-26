package javax0.geci.tutorials.beangenerator;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.Predicates.hasTheFile;
import static javax0.geci.api.Source.Set.set;

public class TestBeanGenerator {
    // snippet testBeanGenerator
    @Test
    public void testBeanGenerator() throws Exception {
        if (new Geci()
            .source(hasTheFile("javax0/geci/tutorials/beangenerator/SampleBean.xml"),"./src/test/resources", "./javageci-examples/src/test/resources")
            .source(set("java"),"./src/test/java", "./javageci-examples/src/test/java")
            .register(new BeanGenerator()).generate()) {
            Assertions.fail("Code was changed during test phase.");
        }
    }
    // end snippet
}
