package javax0.geci.docugen;

import javax0.geci.api.Source;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestGenerateJavageciDocumentation {

    @Test
    @DisplayName("Run the different snippets and generate test.md")
    void generateJavaGeciDocumenation() throws Exception {
        // snippet TestGenerateJavageciDocumentation
        final var fragmentCollector = new Geci();
        fragmentCollector
            .source(Source.maven().module("javageci-tools").mainSource())
            .source(Source.maven().module("javageci-core").mainSource())
            .source(Source.maven().module("javageci-docugen").mainSource())
            .register(FragmentCollector.builder()
                .param("configVariableName").regex("\\w+\\s+.*?(\\w+)\\s*=")
                .param("configDefaultValue").regex("=\\s*\"?(.*?)\"?;")
                .build())
            .generate();

        final var geci = new Geci();
        int i = 0;
        Assertions.assertFalse(
            geci.context(fragmentCollector.context())
                .source("..", ".")
                .ignoreBinary()
                .ignore(
                    "\\.git",
                    "target")
                .log(Geci.MODIFIED)
                .register(SnippetCollector.builder().phase(i++))
                .register(SnippetAppender.builder().phase(i++).build())
                .register(SnippetRegex.builder().phase(i++).build())
                .register(SnippetTrim.builder().phase(i++).build())
                .register(SnippetNumberer.builder().phase(i++).build())
                .register(SnipetLineSkipper.builder().phase(i++).build())
                .register(MarkdownCodeInserter.builder().phase(i++).build())
                .register(JavaDocSnippetInserter.builder().phase(i++).build())
                .splitHelper("md", new MarkdownSegmentSplitHelper())
                .splitHelper("java", new JavaDocSegmentSplitHelper())
                .generate(),
            geci.failed());
        // end snippet
    }
}
