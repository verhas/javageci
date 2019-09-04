package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.*;
import javax0.geci.tools.AbstractGeneratorEx;
import javax0.geci.tools.CompoundParamsBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Geci("configBuilder localConfigMethod=''")
public abstract class AbstractSnippeter extends AbstractGeneratorEx {

    private static final Object ALLOWED_KEYS_CONTEXT_KEY = new Object();
    private static final Object SNIPPET_CONTEXT_KEY = new Object();

    protected static class Config {
        /*
         * - config
         * #### `{{configVariableName}} = {{configDefaultValue}}`
         *
         *
         * The phase parameter defines the phase that the snippet
         * modifying generator is to be run. As this is not a `String`
         * parameter it can only be configured in the builder when the
         * generator instance is created. The generator will return the
         * value `phase + 1` when the framework queries the number of
         * phases the generator needs and when asked if it has to be
         * active in a phase it will return `true` if the actual phase is
         * the same as the one configured.
         */
        protected int phase = 1;

        /*
         * -
         *
         * #### `{{configVariableName}} = "{{configDefaultValue}}"`
         *
         *
         * This configuration parameter can limit the file name pattern
         * for which the snippet generator will run. The default value is
         * to run for every file that has the extension `.md`. If you have
         * other file extensions in your documentation you can configure it
         * in the builder interface.
         *
         */
        protected CharSequence files = "\\.md$";
    }

    protected SnippetStore snippets;
    /**
     * The allowed keys that can be used on a segment that use the
     * snippeter generators. These are {@code snippet}, {@code id} and
     * the actual mnemonics of the registered snippeter generators. The
     * set is collected in the method {@link #context(Context)} and used
     * to set constraints to the segment parameters. This helps to
     * discover parameter configuration typos in the segment headers.
     */
    private Set<String> allowedKeys;

    /**
     * The file name pattern is configurable in every snippeter
     * generator. The default is {@code .md} file extension in the file
     * name.
     */
    protected Pattern fileNamePattern;

    protected abstract void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) throws Exception;

    /**
     * The mnemonics of the concrete extensions of this class are used
     * in the segment configuration. If the mnemonic is used on the
     * segment as a parameter then the actual extension's {@code
     * modify()} method is invoked from this abstract class otherwise
     * not.
     *
     * The extensions that implement the interface {@link
     * NonConfigurable} will be invoked regardless of any configuration
     * on the segment.
     *
     * @return the mnemonic of the snippeter
     */
    public abstract String mnemonic();

    @Override
    public void processEx(Source source) throws Exception {
        if (fileNamePattern.matcher(source.getAbsoluteFile()).find()) {
            for (final var name : source.segmentNames()) {
                final var segment = source.safeOpen(name);
                final var sourceParams = segment.sourceParams();
                sourceParams.setConstraints(source, "'snip'", allowedKeys);
                final var snippetName = sourceParams.get("snippet", name);
                if (snippets == null) {
                    throw new GeciException("There are no snippets. Probably the method class "
                        + this.getClass().getName()
                        + ".context() did not call 'super.context(context)'");
                }
                final var snippet = snippets.get(name, snippetName);
                if (snippet == null) {
                    throw new GeciException("The snippet '" + snippetName
                        + "' is not defined but referenced in file '" + source.getAbsoluteFile()
                        + "' in snippet");
                }
                if (this instanceof NonConfigurable) {
                    modify(source, segment, snippet, null);
                } else {
                    final var configString = sourceParams.get(mnemonic());
                    if (configString.length() > 0) {
                        modify(source, segment, snippet, new CompoundParamsBuilder(configString).build());
                    }
                }
            }
        }

    }

    @Override
    public boolean activeIn(int phase) {
        return phase == config.phase;
    }

    @Override
    public int phases() {
        return config.phase + 1;
    }

    @Override
    public void context(Context context) {
        snippets = context.get(SNIPPET_CONTEXT_KEY, SnippetStore::new);
        fileNamePattern = Pattern.compile(config.files.toString());
        allowedKeys = context.get(ALLOWED_KEYS_CONTEXT_KEY, HashSet::new);
        if (allowedKeys.isEmpty()) {
            allowedKeys.add("snippet");
            allowedKeys.add("id");
        }
        if (mnemonic() != null) {
            allowedKeys.add(mnemonic());
        }
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();

    public class Builder {
        public Builder files(CharSequence files) {
            config.files = files;
            return this;
        }

        public Builder phase(int phase) {
            config.phase = phase;
            return this;
        }

        public AbstractSnippeter build() {
            return AbstractSnippeter.this;
        }
    }
    //</editor-fold>
}
