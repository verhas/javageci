package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.Distant;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Geci("configBuilder localConfigMethod='' configurableMnemonic='fragmentCollector'")
public class FragmentCollector extends AbstractSnippeter implements Distant {


    private static class Config extends AbstractSnippeter.Config {
        private final Map<String, Pattern> patternMap = new HashMap<>();
        // snippet FragmentCollector_config
        private Pattern snippetStart = Pattern.compile("^\\s*\\*\\s*-(?:\\s+(.*))?$");
        private Pattern snippetEnd = Pattern.compile("^\\s*\\*/\\s*$");
        private Function<String,String> transform = line ->
            line.replaceAll("^\\s*\\*\\s?","")
            .replaceAll("^\\s*</?p>\\s*$","");
        // end snippet
        private String param = null;
        private String regex = null;

        private void setRegex(String regex) {
            final var pattern = Pattern.compile(regex);
            if (param == null) {
                throw new GeciException("Regular expression for parameter extraction must have a name");
            }
            if (patternMap.containsKey(param)) {
                throw new GeciException("The parameter '" + param + "' has already have an extracting regular expression");
            }
            patternMap.put(param, pattern);
            param = null;
        }
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) throws Exception {
        throw new IllegalArgumentException("This method should never be invoked");
    }

    @Override
    public void processEx(Source source) throws Exception {
        final var absFileName = source.getAbsoluteFile();
        final var fileName = absFileName.substring(absFileName.lastIndexOf('/') + 1);
        final var dot = fileName.lastIndexOf('.');
        final var className = dot > -1 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        var snippetSubName = "";
        var snippetCounter = 1;
        SnippetBuilder builder = null;
        Snippet snippetToResolv = null;
        for (final var line : source.getLines()) {
            if (snippetToResolv != null) {
                resolveReferences(snippetToResolv, line);
                snippetToResolv = null;
            }
            final var starter = config.snippetStart.matcher(line);
            if (builder == null && starter.find()) {
                if (starter.group(1) != null ) {
                    snippetSubName = starter.group(1);
                    snippetCounter = 1;
                }
                builder = new SnippetBuilder(String.format("%s_%s_%06d",className,snippetSubName,snippetCounter++));
            } else if (builder != null) {
                final var stopper = config.snippetEnd.matcher(line);
                // skip
                if (stopper.find()) {
                    snippetToResolv = builder.build();
                    snippets.put(builder.snippetName(), snippetToResolv, source);
                    builder = null;
                } else {
                    final var convertedLine = config.transform.apply(line);
                    builder.add(convertedLine);
                }
                // skip end
            }
        }
        if (builder != null) {
            throw new GeciException("Snippet " + builder.snippetName() + " was not finished before end of the file " + source.getAbsoluteFile());
        }
    }

    private void resolveReferences(final Snippet snippet, String line){
        final var resolved = new ArrayList<String>();
        for (final var snippetLine : snippet.lines()) {
            String resolvedLine = snippetLine;
            for (final var entry : config.patternMap.entrySet()) {
                final var placeHolder = "{{" + entry.getKey() + "}}";
                if (snippetLine.contains(placeHolder)) {
                    final var matcher = entry.getValue().matcher(line);
                    if (matcher.find() && matcher.groupCount() >= 1) {
                        resolvedLine = resolvedLine.replaceAll(Pattern.quote(placeHolder), Matcher.quoteReplacement(matcher.group(1)));
                    }
                }
            }
            resolved.add(resolvedLine);
        }
        snippet.lines().clear();
        snippet.lines().addAll(resolved);
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "fragmentCollector";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static FragmentCollector.Builder builder() {
        return new FragmentCollector().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder {
        public Builder param(String param) {
            config.param = param;
            return this;
        }

        public Builder regex(String regex) {
            config.setRegex(regex);
            return this;
        }

        public Builder snippetEnd(java.util.regex.Pattern snippetEnd) {
            config.snippetEnd = snippetEnd;
            return this;
        }

        public Builder snippetStart(java.util.regex.Pattern snippetStart) {
            config.snippetStart = snippetStart;
            return this;
        }

        public Builder transform(java.util.function.Function<String,String> transform) {
            config.transform = transform;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public FragmentCollector build() {
            return FragmentCollector.this;
        }
    }
    //</editor-fold>
}
