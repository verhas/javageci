package javax0.geci.docugen;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestGenerateJavageciDocumentation {

    @Test
    @DisplayName("Run the different snippets and generate test.md")
    void testSnippet() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
            geci
                .source("..",".").ignore("\\.git","\\.(png|zip|class|jar|asc|graffle)$","target")/*
                .source(maven().module("javageci-annotation"))
                .source(maven().module("javageci-api"))
                .source(maven().module("javageci-core"))
                .source(maven().module("javageci-docugen"))
                .source(maven().module("javageci-engine"))
                .source(maven().module("javageci-examples"))
                .source(maven().module("javageci-jamal"))
                .source(maven().module("javageci-tools"))*/
                .register(SnippetCollector.builder().phase(0).build())
                .register(SnippetAppender.builder().phase(1).build())
                .register(SnippetRegex.builder().phase(2).build())
                .register(SnippetTrim.builder().phase(3).build())
                .register(SnippetNumberer.builder().phase(4).build())
                .register(SnipetLineSkipper.builder().phase(5).build())
                .register(MarkdownCodeInserter.builder().phase(6).build())
                .splitHelper("md", new MarkdownSegmentSplitHelper())
                .generate());
    }
}
