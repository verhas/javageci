package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.ArrayList;
import java.util.function.Function;

@Geci("configBuilder localConfigMethod='' configurableMnemonic='regex'")
public class SnippetRegex extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
        private String replace = "";
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var search = new ArrayList<String>();
        final var replace = new ArrayList<String>();
        final var escape = params.get("escape");
        for (final var rep : params.getValueList("replace")) {
            if (rep.length() == 0) {
                throw new GeciException("Replace parameter in snippet " + snippet.name() + " in source " + source.getAbsoluteFile() + " has zero length");
            }
            final var startChar = rep.substring(0, 1);
            if (!rep.endsWith(startChar)) {
                throw new GeciException("Replace parameter in snippet " + snippet.name() + " in source " + source.getAbsoluteFile() + " does not end with the character it starts with.");
            }
            final var sepPos = rep.indexOf(startChar, 1);
            search.add((
                    escape.length() > 0
                        ?
                            (Function<String, String>) (String s) -> s.replace(escape, "\\")
                        :
                        (Function<String, String>) (String s) -> s
                ).apply(rep.substring(1, sepPos))
            );
            replace.add(rep.substring(sepPos + 1, rep.length() - 1));
        }

        final var modifiedLines = new ArrayList<String>();
        for (final var line : snippet.lines()) {
            var s = line;
            for (int i = 0; i < search.size(); i++) {
                s = s.replaceAll(search.get(i), replace.get(i));
            }
            modifiedLines.add(s);
        }
        snippet.lines().clear();
        snippet.lines().addAll(modifiedLines);
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "regex";

    @Override
    public String mnemonic() {
        return configuredMnemonic;
    }

    private final Config config = new Config();

    public static SnippetRegex.Builder builder() {
        return new SnippetRegex().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder {
        public Builder replace(String replace) {
            config.replace = replace;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public SnippetRegex build() {
            return SnippetRegex.this;
        }
    }
    //</editor-fold>
}
