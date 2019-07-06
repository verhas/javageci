package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.CompoundParams;

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
@Geci("configBuilder localConfigMethod=''")
public class SnippetCollector extends AbstractSnippeter {
    public static final String CONTEXT_SNIPPET_KEY = "snippets";

    private static class Config extends AbstractSnippeter.Config {
        private Pattern snippetStart = Pattern.compile("//\\s*snipp?et\\s+(.*)$");
        private Pattern snippetEnd = Pattern.compile("//\\s*end\\s+snipp?et");
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) throws Exception {
        throw new IllegalArgumentException("This method should never be invoked");
    }

    @Override
    public String mnemonic() {
        throw new IllegalArgumentException("This method should never be invoked");
    }

    //snippet SnippetCollectorProcessExCode
    @Override
    public void processEx(Source source) throws Exception {
        SnippetBuilder builder = null;
        for (final var line : source.getLines()) {
            final var starter = config.snippetStart.matcher(line);
            if (builder == null && starter.find()) {
                builder = new SnippetBuilder(starter.group(1));
            } else if (builder != null) {
                final var stopper = config.snippetEnd.matcher(line);
                if (stopper.find()) {
                    snippets.put(builder.snippetName(), builder.build());
                    builder = null;
                } else {
                    builder.add(line);
                }
            }
        }
        if (builder != null) {
            throw new GeciException(builder.snippetName() + " was not finished before end of the file " + source.getAbsoluteFile());
        }
    }
    //end snippet

    //<editor-fold id="configBuilder">
    private final Config config = new Config();

    public static SnippetCollector.Builder builder() {
        return new SnippetCollector().new Builder();
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

        public SnippetCollector build() {
            return SnippetCollector.this;
        }
    }
    //</editor-fold>
}
