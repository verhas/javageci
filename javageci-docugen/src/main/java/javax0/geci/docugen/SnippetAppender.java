package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.CompoundParams;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Geci("configBuilder localConfigMethod='' configurableMnemonic='append'")
public class SnippetAppender extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
    }

    //snippet SnippetAppender_modify
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var segmentName = segment.sourceParams().id();
        final var namePatterns = Arrays.stream(params.get("snippets").split(","))
            .map(Pattern::compile)
            .map(Pattern::asMatchPredicate).collect(Collectors.toList());

        for (final var pattern : namePatterns) {
            snippets.names().stream()
                .filter(pattern)
                .sorted(String::compareTo)
                .map(name -> snippets.get(segmentName, name))
                .forEach(snip -> snippet.lines().addAll(snip.lines()));
        }
    }
    //end snippet

    //snippet SnippetAppender_context
    @Override
    public void context(Context context) {
        super.context(context);
        fileNamePattern = Pattern.compile(config.files);
    }
    //end snippet

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "append";

    @Override
    public String mnemonic() {
        return configuredMnemonic;
    }

    private final Config config = new Config();

    public static SnippetAppender.Builder builder() {
        return new SnippetAppender().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder {
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

        public SnippetAppender build() {
            return SnippetAppender.this;
        }
    }
    //</editor-fold>
}
