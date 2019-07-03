package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;

import java.util.Map;

@Geci("configBuilder configurableMnemonic='snippetCollector' localConfigMethod=\"\"")
public class MarkdownSnippetInserter extends AbstractGeneratorEx {
    private Context ctx = null;
    private Map<String, Snippet> snippets;

    private static class Config {
        private int phase = 1;
    }

    @Override
    public void processEx(Source source) throws Exception {
        final var names = source.segmentNames();
        for (final var name : names) {
            final var segment = source.safeOpen(name);
            final var originalLines = segment.originalLines();
            if( originalLines.size() > 0 ){
                segment.write(originalLines.get(0));
            }
            final var snippet = snippets.get(name);
            snippet.lines().forEach( l -> segment.write(l));
        }
    }

    @Override
    public boolean activeIn(int phase) {
        return phase == config.phase;
    }

    @Override
    public int phases() {
        return 0;
    }

    @Override
    public void context(Context context) {
        ctx = context;
        snippets = ctx.get(SnippetCollector.CONTEXT_SNIPPET_KEY);
        if (snippets == null || snippets.size() == 0) {
            throw new GeciException("There are no snippets collected");
        }
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "snippetCollector";

    public String mnemonic() {
        return configuredMnemonic;
    }

    private final Config config = new Config();

    public static MarkdownSnippetInserter.Builder builder() {
        return new MarkdownSnippetInserter().new Builder();
    }

    public class Builder {
        public Builder phase(int phase) {
            config.phase = phase;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public MarkdownSnippetInserter build() {
            return MarkdownSnippetInserter.this;
        }
    }
    //</editor-fold>
}
