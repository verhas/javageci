package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.CompoundParams;

import java.util.ArrayList;
import java.util.regex.Pattern;

@Geci("configBuilder localConfigMethod='' configurableMnemonic='number'")
public class SnippetNumberer extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
        private String start = "1";
        private String step = "1";
        private String format = "%d. ";
        private String from = "0";
        private String to = "";
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var start = Long.parseLong(params.get("start", config.start));
        final var step = Long.parseLong(params.get("step", config.step));
        final var format = params.get("format", config.format);
        final var startLine = calculateLineNumber(
                params.get("from", config.from),
                snippet.lines().size()
        );
        final var endLine = calculateLineNumber(
                params.get("to", config.to.length() > 0 ? config.to : "" + snippet.lines().size())
                , snippet.lines().size()
        );

        final var modifiedLines = new ArrayList<String>();
        int index = 0;
        long number = start;
        for (final var line : snippet.lines()) {
            final var formattedNumber = String.format(format, number);
            if (index >= startLine && index < endLine) {
                modifiedLines.add(formattedNumber + line);
                number += step;
            } else {
                modifiedLines.add(" ".repeat(formattedNumber.length()) + line);
            }
            index++;
        }
        snippet.lines().clear();
        snippet.lines().addAll(modifiedLines);
    }

    private long calculateLineNumber(String number, int max) {
        final var z = Long.parseLong(number);
        if (z < 0) {
            return max + z;
        } else {
            return z;
        }
    }


    @Override
    public void context(Context context) {
        super.context(context);
        fileNamePattern = Pattern.compile(config.files);
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "number";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static SnippetNumberer.Builder builder() {
        return new SnippetNumberer().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder {
        public Builder format(String format) {
            config.format = format;
            return this;
        }

        public Builder from(String from) {
            config.from = from;
            return this;
        }

        public Builder start(String start) {
            config.start = start;
            return this;
        }

        public Builder step(String step) {
            config.step = step;
            return this;
        }

        public Builder to(String to) {
            config.to = to;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public SnippetNumberer build() {
            return SnippetNumberer.this;
        }
    }
    //</editor-fold>
}

