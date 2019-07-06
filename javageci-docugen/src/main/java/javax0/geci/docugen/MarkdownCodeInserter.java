package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.CompoundParams;

import java.util.regex.Pattern;

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

    @Override
    public void context(Context context) {
        super.context(context);
        fileNamePattern = Pattern.compile(config.files);
    }

    public String mnemonic() {
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
