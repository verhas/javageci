package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.CompoundParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Geci("configBuilder localConfigMethod='' configurableMnemonic='append'")
public class SnippetAppender extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
        private int phase = 1;
        private String files = "\\.md$";
    }

    //snippet SnippetAppender_modify
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var ph = new AtomicReference<Predicate<String>>(s -> false);
        Arrays.stream(params.get("snippets").split(","))
                .map(Pattern::compile)
                .map(Pattern::asMatchPredicate).forEach(
                       p -> ph.set(ph.get().or(p))
                );

        snippets.names().stream()
                .filter(ph.get())
                .sorted(String::compareTo)
                .map(n -> snippets.get(segment.sourceParams().id(), n))
                .forEach(s -> snippet.lines().addAll(s.lines()));
    }
    //end snippet

    @Override
    public boolean activeIn(int phase) {
        return phase == config.phase;
    }

    @Override
    public int phases() {
        return config.phase + 1;
    }

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
