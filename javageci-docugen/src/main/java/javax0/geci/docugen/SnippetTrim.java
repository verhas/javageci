package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.ArrayList;

@Geci("configBuilder localConfigMethod='' configurableMnemonic='trim'")
public class SnippetTrim extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
        private String to = "0";
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final int to;
        try {
            to = Integer.parseInt(params.get("to", config.to));
        } catch (NumberFormatException nfe) {
            throw new GeciException("Can not interpret 'to' parameter " + params.get("to", config.to) + " as a number in snippet " + snippet.name() + " in source " + source.getAbsoluteFile());
        }
        final var untab = calculateTabbing(snippet);

        final var modifiedLines = new ArrayList<String>();
        for (final var line : snippet.lines()) {
            modifiedLines.add(" ".repeat(to) + line.substring(untab));
        }
        snippet.lines().clear();
        snippet.lines().addAll(modifiedLines);
    }

    private int calculateTabbing(Snippet snippet) {
        var min = Integer.MAX_VALUE;
        for (final var line : snippet.lines()) {
            final var spaces = line.length() - line.stripLeading().length();
            if (spaces < min) {
                min = spaces;
            }
        }
        return min;
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "trim";

    @Override
    public String mnemonic() {
        return configuredMnemonic;
    }

    private final Config config = new Config();

    public static SnippetTrim.Builder builder() {
        return new SnippetTrim().new Builder();
    }

public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder {
    public Builder to(String to) {
        config.to = to;
        return this;
    }

    public Builder mnemonic(String mnemonic) {
        configuredMnemonic = mnemonic;
        return this;
    }

    public SnippetTrim build() {
        return SnippetTrim.this;
    }
}
//</editor-fold>
}
