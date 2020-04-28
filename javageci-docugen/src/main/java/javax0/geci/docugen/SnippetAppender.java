package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * // snippet SnippetAppender_doc
 *
 * Snippet appender can be used to append multiple snippets together. This is useful when the content of the
 * documentation is in different places and it would be inconvenient to include the individual snippets one by one
 * into the documentation. The snippets can be referenced using their name and the reference can also contain regular
 * expression. In that case all the snippets that match the regular expression will be joined together and added to the
 * actual snippet in alphabetical order.
 *
 * The generator has one configuration parameter `snippets` (note the plural) that should list the snippets that are to
 * be appended to the original snippet. Remember that the base snippet is referenced in the segment using the parameter
 * `snippet` or using the name of the segment id as a snippet name by default and the appended snippets are appended to
 * this snippet.
 *
 * There can be multiple `snippets` values on the configuration of the snippet appender. They are worked up from left to
 * right and when a value matches many snippet names as a regular expression then these snippets will be appended in
 * alphabetical order. The usual practice is to name these snippets something like `snippetName_001`, `snippetName_002`
 * and so on and use the configuration `append="snippets='snippetName_.*'"`.
 *
 * The regular expressions sometimes may need escaping and the same way as in case of the snippet modifying generator
 * `regex` there is a parameter `escape7 that can be used to specify the character that is used instead of `\\\\`.
 *
 * Sometimes there is no base snippet to append to. The documentation, for example, needs to join together the snippets
 * `manySniipets_.*` that are `manySniipets_001`, `manySniipets_002`, and `manySniipets_003`. In that case if the
 * segment references the first snippet, `snippet="manySniipets_001" append="snippets='manySniipets_.*'"` then the first
 * snippet will be copied twice. For this reason there is a predefined snippet in the snippet store called `epsilon`
 * that contains no lines. Using that we can write
 *
 * snippet="epsilon" append="snippets='manySniipets_.*'"
 *
 * The snippets are copied from the original, unmodified version of the snippet. It is not possible to execute
 * modifications on the different snippets and perform the appending afterwards.
 *
 * //end snippet
 */
@Geci("configBuilder localConfigMethod='' configurableMnemonic='append'")
public class SnippetAppender extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
        // snippet SnippetAppender_Config_001
        private List<String> snippets = Collections.emptyList();
        /*

        This configuration parameter defines the snippets that are appended to the base snippet.

        end snippet */

        // snippet SnippetAppender_Config_002
        private String escape = "";
        /*

        This parameter can be used to define an escape character / string that can be used instead of the backslash
        character that would be needed four times to be used in the regular expression. It is recommended to use the
        tilde `~` character.

        end snippet */
    }

    //snippet SnippetAppender_modify
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var segmentName = segment.sourceParams().id();
        final var escape = params.get("escape", config.escape);
        final var namePatterns = params.getValueList("snippets", config.snippets).stream()
            .map(s -> escape.length() > 0 ? s.replace(escape, "\\") : s)
            .map(Pattern::compile)
            .collect(Collectors.toList());

        for (final var pattern : namePatterns) {
            final var thereWereSomeSnippets = new AtomicBoolean(false);
            snippets.names().stream()
                .filter(s -> pattern.matcher(s).matches())
                .sorted(String::compareTo)
                .map(name -> snippets.get(segmentName, name))
                .forEach(snip -> {
                    snippet.lines().addAll(snip.lines());
                    thereWereSomeSnippets.set(true);
                });
            if (!thereWereSomeSnippets.get()) {
                throw new GeciException("There is no snippet matching the pattern " + pattern +
                    " used by the segment " + segmentName + ".");
            }
        }
    }
    //end snippet

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "append";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static SnippetAppender.Builder builder() {
        return new SnippetAppender().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder escape(String escape) {
            config.escape = escape;
            return this;
        }

        public Builder snippets(java.util.List<String> snippets) {
            config.snippets = snippets;
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
