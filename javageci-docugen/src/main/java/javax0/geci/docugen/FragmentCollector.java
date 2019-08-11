package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.*;

import java.util.function.Function;
import java.util.regex.Pattern;

@Geci("configBuilder localConfigMethod='' configurableMnemonic='fragmentCollector'")
public class FragmentCollector extends AbstractSnippeter implements Distant {

    private static class Config extends AbstractSnippeter.Config {
        // snippet FragmentCollector_config
        private Pattern snippetStart = Pattern.compile("^\\s*\\*\\s*-(?:\\s+(.*))?$");
        private Pattern snippetEnd = Pattern.compile("^\\s*\\*/\\s*$");
        private Function<String,String> transform = line ->
            line.replaceAll("^\\s*\\*\\s?","")
            .replaceAll("^\\s*</?p>\\s*$","");
        // end snippet
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
        for (final var line : source.getLines()) {
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
                    snippets.put(builder.snippetName(), builder.build(), source);
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
