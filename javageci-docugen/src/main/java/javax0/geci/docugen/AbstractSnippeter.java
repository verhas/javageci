package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.*;
import javax0.geci.tools.AbstractGeneratorEx;
import javax0.geci.tools.CompoundParamsBuilder;

import java.util.regex.Pattern;

@Geci("configBuilder localConfigMethod=''")
public abstract class AbstractSnippeter extends AbstractGeneratorEx {

    protected static class Config {
        protected int phase = 1;
        protected String files = "\\.md$";
    }

    protected SnippetStore snippets;

    protected Pattern fileNamePattern;

    protected abstract void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) throws Exception;

    public abstract String mnemonic();

    @Override
    public void processEx(Source source) throws Exception {
        if (fileNamePattern.matcher(source.getAbsoluteFile()).find()) {
            for (final var name : source.segmentNames()) {
                final var segment = source.safeOpen(name);
                final var sourceParams = segment.sourceParams();
                final var snippetName = sourceParams.get("snippet", name);
                if (snippets == null) {
                    throw new GeciException("The method class " + this.getClass().getName() + ".context() did not call 'super.context(context)'");
                }
                final var snippet = snippets.get(name, snippetName);
                if (snippet == null) {
                    throw new GeciException("The snippet '" + snippetName + "' is not defined but referenced in file '" + source.getAbsoluteFile() + "' in snippet");
                }
                if (mnemonic() != null) {
                    final var configString = sourceParams.get(mnemonic());
                    if (configString.length() > 0) {
                        modify(source, segment, snippet, new CompoundParamsBuilder(configString).build());
                    }
                } else {
                    modify(source, segment, snippet, null);
                }
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
        snippets = context.get(SnippetCollector.CONTEXT_SNIPPET_KEY, SnippetStore::new);
        fileNamePattern = Pattern.compile(config.files);
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();

    public class Builder {
        public Builder files(String files) {
            config.files = files;
            return this;
        }

        public Builder phase(int phase) {
            config.phase = phase;
            return this;
        }

        public AbstractSnippeter build() {
            return AbstractSnippeter.this;
        }
    }
    //</editor-fold>
}
