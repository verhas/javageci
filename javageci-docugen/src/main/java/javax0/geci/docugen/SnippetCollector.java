package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;
import javax0.geci.tools.CompoundParams;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Geci("configBuilder configurableMnemonic='snippetCollector'")
public class SnippetCollector extends AbstractGeneratorEx {
    public static final String CONTEXT_SNIPPET_KEY = "snippets";
    private Context ctx = null;
    private Map<String, Snippet> snippets;

    private static class Config {
        private Pattern snippetStart = Pattern.compile("//\\s*snipp?et\\s+(.*)$");
        private Pattern snippetEnd = Pattern.compile("//\\s*end");
    }


    @Override
    public void processEx(Source source) throws Exception {
        SnippetBuilder builder = null;
        for (final var line : source.getLines()) {
            final var starter = config.snippetStart.matcher(line);
            if( starter.find()){
                builder = new SnippetBuilder().startLine(starter.group(1));
            }
            if( builder != null ){
                final var stopper = config.snippetEnd.matcher(line);
                if( stopper.find()){
                    snippets.put(builder.snippetName(),builder.build());
                }
            }
        }
    }

    @Override
    public boolean activeIn(int phase) {
        return phase == 0;
    }

    @Override
    public void context(Context context) {
        ctx = context;
        snippets = ctx.get(CONTEXT_SNIPPET_KEY, () -> new HashMap<String, Snippet>());
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();

    public static SnippetCollector.Builder builder() {
        return new SnippetCollector().new Builder();
    }

    public class Builder {

        public Builder snippetEnd(java.util.regex.Pattern snippetEnd) {
            config.snippetEnd = snippetEnd;
            return this;
        }

        public Builder snippetStart(java.util.regex.Pattern snippetStart) {
            config.snippetStart = snippetStart;
            return this;
        }

        public SnippetCollector build() {
            return SnippetCollector.this;
        }
    }

    private Config localConfig(CompoundParams params) {
        final var local = new Config();
        local.snippetEnd = config.snippetEnd;
        local.snippetStart = config.snippetStart;
        return local;
    }
    //</editor-fold>
}
