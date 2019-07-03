package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;

@Geci("configBuilder configurableMnemonic='snippetCollector' localConfigMethod=\"\"")
public class MarkdownSnippetInserter extends AbstractGeneratorEx {

    private static class Config {
        private int phase = 0;
    }

    @Override
    public void processEx(Source source) throws Exception {

    }

    @Override
    public boolean activeIn(int phase) {
        return false;
    }

    @Override
    public int phases() {
        return 0;
    }

    @Override
    public void context(Context context) {

    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "snippetCollector";

    public String mnemonic(){
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
