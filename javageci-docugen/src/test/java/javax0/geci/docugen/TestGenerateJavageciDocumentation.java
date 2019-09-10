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
        Assertions.assertFalse(
            geci.context(fragmentCollector.context())
            .trace("target/trace.xml")
                .source("..", ".")
                .ignoreBinary()
                .ignore(
                    "\\.git",
                    "target")
                .log(Geci.MODIFIED)
                .orderedRegister(SnippetCollector.builder(),
                    SnippetAppender.builder(),
                    SnippetRegex.builder(),
                    SnippetTrim.builder(),
                    SnippetNumberer.builder(),
                    SnipetLineSkipper.builder(),
                    MarkdownCodeInserter.builder(),
                    JavaDocSnippetInserter.builder())
                .splitHelper("md", new MarkdownSegmentSplitHelper())
                .splitHelper("java", new JavaDocSegmentSplitHelper())
                .generate(),
            geci.failed());
        // end snippet
    }
}
