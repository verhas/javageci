package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Geci("configBuilder localConfigMethod=\"\"")
public class MarkdownCodeInserter extends AbstractGeneratorEx {
    private Context ctx = null;
    private SnippetStore snippets;

    private static class Config {
        private int phase = 1;
        private String files = "\\.md$";
    }

    private Pattern fileNamePattern;

    @Override
    public void processEx(Source source) throws Exception {
        if (fileNamePattern.matcher(source.getAbsoluteFile()).find()) {
            final var names = source.segmentNames();
            for (final var name : names) {
                final var segment = source.safeOpen(name);
                final var originalLines = segment.originalLines();
                if (originalLines.size() > 0) {
                    segment.write(originalLines.get(0));
                }
                final var params =  segment.sourceParams();
                final var snippetName = params.get("snippet",name);
                final var snippet = snippets.get(name, snippetName);
                if (snippet == null) {
                    throw new GeciException("The snippet '" + snippetName + "' is not defined but referenced in file '" + source.getAbsoluteFile() + "' in snippet");
                }
                snippet.lines().forEach(l -> segment.write(l));
            }
        }
    }

    @Override
    public boolean activeIn(int phase) {
        return phase == config.phase;
    }

    @Override
    public int phases() {
        return config.phase + 1;
    }

    @Override
    public void context(Context context) {
        ctx = context;
        snippets = ctx.get(SnippetCollector.CONTEXT_SNIPPET_KEY, SnippetStore::new);
        fileNamePattern = Pattern.compile(config.files);
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();
    public static MarkdownCodeInserter.Builder builder() {
        return new MarkdownCodeInserter().new Builder();
    }

    public class Builder {
        public Builder files(String files) {
            config.files = files;
            return this;
        }

        public Builder phase(int phase) {
            config.phase = phase;
            return this;
        }

        public MarkdownCodeInserter build() {
            return MarkdownCodeInserter.this;
        }
    }
    //</editor-fold>
}
