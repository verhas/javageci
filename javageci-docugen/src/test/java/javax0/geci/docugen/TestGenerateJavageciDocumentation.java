package javax0.geci.docugen;

import javax0.geci.api.Source;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestGenerateJavageciDocumentation {

    @Test
    @DisplayName("Run the different snippets and generate test.md")
    void generateJavaGeciDocumenation() throws Exception {
        // snippet TestGenerateJavageciDocumentation
        final var fragmentCollector = new Geci();
        fragmentCollector
            .source(Source.maven().module("javageci-docugen").mainSource())
            .register(FragmentCollector.builder().build())
            .generate();

        final var geci = new Geci();
        int i = 0;
        Assertions.assertFalse(
            geci.context(fragmentCollector.context())
                .source("..", ".").ignore("\\.git", "\\.(png|zip|class|jar|asc|graffle)$", "target")
                .log(Geci.MODIFIED)
                .register(SnippetCollector.builder().phase(i++).build())
                .register(SnippetAppender.builder().phase(i++).build())
                .register(SnippetRegex.builder().phase(i++).build())
                .register(SnippetTrim.builder().phase(i++).build())
                .register(SnippetNumberer.builder().phase(i++).build())
                .register(SnipetLineSkipper.builder().phase(i++).build())
                .register(MarkdownCodeInserter.builder().phase(i++).build())
                .splitHelper("md", new MarkdownSegmentSplitHelper())
                .generate(),
            geci.failed());
        // end snippet
    }
}
