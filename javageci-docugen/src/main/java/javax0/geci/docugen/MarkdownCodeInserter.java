package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

@Geci("configBuilder localConfigMethod='' configurableMnemonic='markdown'")
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

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "markdown";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static MarkdownCodeInserter.Builder builder() {
        return new MarkdownCodeInserter().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder {
        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public MarkdownCodeInserter build() {
            return MarkdownCodeInserter.this;
        }
    }
    //</editor-fold>
}
