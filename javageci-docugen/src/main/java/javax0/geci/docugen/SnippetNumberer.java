package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;
import javax0.geci.tools.CompoundParamsBuilder;

import java.util.ArrayList;
import java.util.regex.Pattern;

@Geci("configBuilder localConfigMethod=\"\"")
public class SnippetNumberer extends AbstractGeneratorEx {
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
                final var params = segment.sourceParams();
                final var number = params.get("number");
                if (number.length() > 0) {
                    final var snippetName = params.get("snippet", name);
                    final var snippet = snippets.get(name, snippetName);
                    if (snippet == null) {
                        throw new GeciException("The snippet '" + snippetName + "' is not defined but referenced in file '" + source.getAbsoluteFile() + "' in snippet");
                    }
                    numberSnippet(snippet, number);
                }
            }
        }
    }

    @Override
    public boolean activeIn(int phase) {
        return phase == config.phase;
    }

    private void numberSnippet(Snippet snippet, String options) {
        final var params = new CompoundParamsBuilder(options).build();
        final var start = Long.parseLong(params.get("start", "1"));
        final var step = Long.parseLong(params.get("step", "1"));
        final var format = params.get("format", "%d. ");
        final var startLine = calculateLineNumber(params.get("from", "0"), snippet.lines().size());
        final var endLine = calculateLineNumber(params.get("to", "" + snippet.lines().size()), snippet.lines().size());

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
    public static SnippetNumberer.Builder builder() {
        return new SnippetNumberer().new Builder();
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

        public SnippetNumberer build() {
            return SnippetNumberer.this;
        }
    }
    //</editor-fold>
}

