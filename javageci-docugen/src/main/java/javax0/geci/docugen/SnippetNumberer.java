package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;
import javax0.geci.tools.CompoundParamsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Geci("configBuilder configurableMnemonic='snippetCollector' localConfigMethod=\"\"")
public class SnippetNumberer extends AbstractGeneratorEx {
    private Context ctx = null;
    private Map<String, Snippet> snippets;

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
                    final var localSnippetName = name + "#" + snippetName;
                    final var originalSnippet = snippets.get(snippetName);
                    if (originalSnippet == null) {
                        throw new GeciException("The snippet '" + snippetName + "' is not defined but referenced in file '" + source.getAbsoluteFile() + "' in snippet");
                    }
                    if (!snippets.containsKey(localSnippetName)) {
                        snippets.put(localSnippetName, originalSnippet.copy());
                    }
                    final var localSnippet = snippets.get(localSnippetName);
                    numberSnippet(localSnippet, number);
                }
            }
        }
    }

    @Override
    public boolean activeIn(int phase) {
        return phase == config.phase;
    }

    private void numberSnippet(Snippet original, String options) {
        final var params = new CompoundParamsBuilder(options).build();
        final var start = Long.parseLong(params.get("start", "1"));
        final var step = Long.parseLong(params.get("step", "1"));
        final var format = params.get("format", "%d. ");
        final var lines = params.get("lines", ":");
        long startLine = 0;
        long endLine = original.lines().size();
        var linesSplit = lines.split(":", -1);
        if (linesSplit[0].length() > 0) {
            startLine = Long.parseLong(linesSplit[0]);
        }
        if (linesSplit[1].length() > 0) {
            endLine = Long.parseLong(linesSplit[1]);
        }
        if (startLine < 0) {
            startLine = original.lines().size() + startLine;
        }
        if (endLine < 0) {
            endLine = original.lines().size() + endLine;
        }
        final var modifiedLines = new ArrayList<String>();
        int index = 0;
        long number = start;
        for (final var line : original.lines()) {
            if (index >= startLine && index < endLine) {
                modifiedLines.add(String.format(format, number) + line);
                number += step;
            }else{
                modifiedLines.add(line);
            }
            index++;
        }
        original.lines().clear();
        original.lines().addAll(modifiedLines);
    }

    @Override
    public int phases() {
        return config.phase + 1;
    }

    @Override
    public void context(Context context) {
        ctx = context;
        snippets = ctx.get(SnippetCollector.CONTEXT_SNIPPET_KEY, HashMap::new);
        fileNamePattern = Pattern.compile(config.files);
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "snippetCollector";

    public String mnemonic() {
        return configuredMnemonic;
    }

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

