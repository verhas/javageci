package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.Tracer;

import java.util.List;

/**
 * Snippet inserter that inserts snippets into asciidoc and into
 * markdown format files.
 */
@Geci("configBuilder localConfigMethod=''")
public class MarkdownCodeInserter extends AbstractSnippeter implements NonConfigurable {

    private static class Config extends AbstractSnippeter.Config {
    }

    @Override
    public void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var originalLines = segment.originalLines();
        if (hasMarkdownCodeSegment(originalLines)) {
            writeBackLines(segment, originalLines, 1);
        } else if (hasAsciiDocCodeSegment(originalLines)) {
            writeBackLines(segment, originalLines, 2);
        }
        snippet.lines().forEach(l -> {
            Tracer.log("snippetLine",l);
            segment.write(l);
            }
        );
    }

    private boolean hasMarkdownCodeSegment(List<String> originalLines) {
        return originalLines.size() > 0 && originalLines.get(0).matches("^(\\s*)```(.*)$");
    }

    private boolean hasAsciiDocCodeSegment(List<String> originalLines) {
        return originalLines.size() > 1 &&
                originalLines.get(0).matches("^(\\s*)\\[(\\s*)source(.*)$") &&
                originalLines.get(1).matches("^(\\s*)----(\\s*)$");
    }

    private void writeBackLines(Segment segment, List<String> originalLines, int count) {
        for (int i = 0; i < count; i++) {
            Tracer.log("snippetLine", originalLines.get(i));
            segment.write(originalLines.get(i));
        }
    }

    /**
     * The mnemonic of this generator has to be null. If the mnemonic of
     * a snippet handling generator is not null then it is only invoked
     * from {@link AbstractSnippeter} if the mnemonic is configured in
     * the snippet header (it is used). When we insert snippet into the
     * source (markdown) we do not request it explicitly, but since the
     * mnemonic returned from here is {@code null} and could not match
     * any string in the header it is invoked.
     *
     * @return {@code null}
     */
    @Override
    public String mnemonic(){
        return null;
    }
    //<editor-fold id="configBuilder">
    private final Config config = new Config();
    public static MarkdownCodeInserter.Builder builder() {
        return new MarkdownCodeInserter().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder implements javax0.geci.api.GeneratorBuilder {
        public MarkdownCodeInserter build() {
            return MarkdownCodeInserter.this;
        }
    }
    //</editor-fold>
}
