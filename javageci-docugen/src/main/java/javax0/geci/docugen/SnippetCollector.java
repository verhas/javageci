package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.Distant;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.Tracer;

import java.util.regex.Pattern;

/**
 * A snippet collector.
 * <p>
 * This generator collects all the snippets that are between lines
 *
 * <pre>
 *     {@code
 *     //snippet snippetname
 *     }
 * </pre>
 *
 * <p> and
 *
 * <pre>
 *     {@code
 *     //end snippet
 *     }
 * </pre>
 *
 * <p> The collected snippets get into a Map keyed by the names of the
 * snippets and can be used by other generators. Note that this
 * generator does not touch any source therefore it should not and
 * cannot be used alone.
 */
@Geci("configBuilder localConfigMethod='' configurableMnemonic='snippetCollector'")
public class SnippetCollector extends AbstractSnippeter implements Distant {

    private static class Config extends AbstractSnippeter.Config {
        // snippet SnippetCollector_config
        private Pattern snippetStart = Pattern.compile("(?://|/\\*)\\s*snipp?et\\s+(.*)$");
        private Pattern snippetEnd = Pattern.compile("(?://\\s*end\\s+snipp?et|end\\s+snipp?et\\s*\\*/)");
        // end snippet
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) throws Exception {
        final var iae = new IllegalArgumentException("This method should never be invoked");
        Tracer.log(iae);
        throw iae;
    }

    //snippet SnippetCollectorProcessExCode skipper="true"
    @Override
    public void processEx(Source source) throws Exception {
        Tracer.log("SnippetCollector", "Starting snippet collector for source '" + source.getAbsoluteFile() + "'");
        SnippetBuilder builder = null;
        try (final var tracer = Tracer.push("Lines", "Collecting from source lines")) {
            for (final var line : source.getLines()) {
                Tracer.log("line", line);
                final var starter = config.snippetStart.matcher(line);
                if (builder == null && starter.find()) {
                    builder = new SnippetBuilder(starter.group(1));
                    Tracer.prepend("START[" +builder.snippetName() + "]:");
                } else if (builder != null) {
                    final var stopper = config.snippetEnd.matcher(line);
                    // skip
                    if (stopper.find()) {
                        Tracer.prepend( "END["+builder.snippetName()+"]:");
                        snippets.put(builder.snippetName(), builder.build(), source);
                        builder = null;
                    } else {
                        Tracer.prepend("SNIPPET:");
                        builder.add(line);
                    }
                    // skip end
                } else {
                    Tracer.prepend( "IGNORE:");
                }
            }
        }
        if (builder != null) {
            throw new GeciException("Snippet " + builder.snippetName() + " was not finished before end of the file.");
        }
        Tracer.log("SnippetCollector", "Finishing snippet collector for source '" + source.getAbsoluteFile() + "'");
    }
    //end snippet

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "snippetCollector";

    @Override
    public String mnemonic() {
        return configuredMnemonic;
    }

    private final Config config = new Config();

    public static SnippetCollector.Builder builder() {
        return new SnippetCollector().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder snippetEnd(java.util.regex.Pattern snippetEnd) {
            config.snippetEnd = snippetEnd;
            return this;
        }

        public Builder snippetStart(java.util.regex.Pattern snippetStart) {
            config.snippetStart = snippetStart;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public SnippetCollector build() {
            return SnippetCollector.this;
        }
    }
    //</editor-fold>
}
