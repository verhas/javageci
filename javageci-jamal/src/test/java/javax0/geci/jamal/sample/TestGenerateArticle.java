package javax0.geci.jamal.sample;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.engine.Processor;
import javax0.jamal.tools.FileTools;
import org.junit.jupiter.api.Test;

public class TestGenerateArticle {

    @Test
    void testGenerateArticle() throws BadSyntax {
        final var in = FileTools.getInput("ARTICLE1.wp.jam");
        final var processor = new Processor("{%", "%}");
        final var result = processor.process(in);
        FileTools.writeFileContent("ARTICLE1.wp", result);
    }
}
