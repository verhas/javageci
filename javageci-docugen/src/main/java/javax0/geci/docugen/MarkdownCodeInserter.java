package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

@Geci("configBuilder localConfigMethod=''")
public class MarkdownCodeInserter extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
    }

    @Override
    public void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var originalLines = segment.originalLines();
        if (originalLines.size() > 0 && originalLines.get(0).startsWith("```")) {
            segment.write(originalLines.get(0));
        }
        snippet.lines().forEach(l -> segment.write(l));
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

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder {

        public MarkdownCodeInserter build() {
            return MarkdownCodeInserter.this;
        }
    }
    //</editor-fold>
}
