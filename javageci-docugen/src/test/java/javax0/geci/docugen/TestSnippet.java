package javax0.geci.docugen;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestSnippet {

    @Test
    void buildGenerators() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven().module("javageci-docugen"))
                        .register(SnippetCollector.builder().phase(0).build())
                        .register(SnippetAppender.builder().phase(1).build())
                        .register(SnippetNumberer.builder().phase(2).build())
                        .register(MarkdownCodeInserter.builder().phase(3).build())
                        .splitHelper("md", new MarkdownSegmentSplitHelper())
                        .generate());
    }
}
