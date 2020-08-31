package javax0.geci.docugen;

import javax0.geci.api.Source;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestGenerateJavageciDocumentation {

    @Test
    @DisplayName("Run the different snippets and update Java::Geci documentation")
    void generateJavaGeciDocumenationNewApi() throws Exception {
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
                .source("..", ".")
                .ignoreBinary()
                .ignore(
                    "\\.git", "\\.idea", "\\.iml$",
                    "javageci-livetemplates", "target",
                        "LIVETEMPLATES.md" // contains sample snip codes that should not be processed
                )
                .log(Geci.MODIFIED)
                // snippet RegisteringAllSnippets
                .register(Register.allSnippetHandlers())
                // end snippet

                .splitHelper("adoc", new AdocSegmentSplitHelper())
                .splitHelper("md", new MarkdownSegmentSplitHelper())
                .splitHelper("java", new JavaDocSegmentSplitHelper())
                .generate(),
            geci.failed());
    }

    // @Test
    // This is not an ignored test. We do not run it because it is outdated and we do not want to run
    // document generation twice. See the method above, that does the same thing just more modern and shorter.
    // on the other hand this code is needed for the documentation to explain how the different
    // snip!!!pet generators are configured to execute in different phases, before showing the convenience
    // method. (snip!!!pet needs something between the two 'p' letters otherwise snip!!!pet handlers will think it is a snip!!!pet)
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
                .source("..", ".")
                .ignoreBinary()
                .ignore(
                    "\\.git", "\\.idea", "\\.iml$",
                    "javageci-livetemplates", "target")
                .log(Geci.MODIFIED)
                // outdated registration of the document generators, see the real example in the documentation below
                .register(SnippetCollector.builder().files("\\.md$|\\.java$|\\.adoc$")
                    .phase(0).build())
                .register(SnippetAppender.builder().files("\\.md$|\\.java$|\\.adoc$")
                    .phase(1).build())
                .register(SnippetTrim.builder().files("\\.md$|\\.java$|\\.adoc$")
                    .phase(2).build())
                .register(SnippetNumberer.builder().mnemonic("prenumber").files("\\.md$|\\.java$|\\.adoc$")
                    .phase(3).build())
                .register(SnippetRegex.builder().files("\\.md$|\\.java$|\\.adoc$")
                    .phase(4).build())
                .register(SnippetLineSkipper.builder().files("\\.md$|\\.java$|\\.adoc$")
                    .phase(5).build())
                .register(SnippetNumberer.builder().files("\\.md$|\\.java$|\\.adoc$")
                    .phase(6).build())
                .register(MarkdownCodeInserter.builder()
                    .phase(7).build())
                .register(JavaDocSnippetInserter.builder()
                    .phase(8).build())
                .splitHelper("adoc", new AdocSegmentSplitHelper())
                .splitHelper("md", new MarkdownSegmentSplitHelper())
                .splitHelper("java", new JavaDocSegmentSplitHelper())
                .generate(),
            geci.failed());
        // end snippet
    }
}
