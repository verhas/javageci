package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * // snippet SnippetRegex_doc
 *
 * The `regex` snippet generator goes through each line of the snippet
 * and does regular expression based search and replace. It can be used
 * for example as
 *
 *      regex="replace='/a/b/'"
 *
 * to replace each occurrence of `a` to `b`.
 *
 * A real life example is:
 *
 *      regex="replace='/^\\\\s*\\\\*\\\\s?//'
 *
 * It is used in the documentation of docugen to include the snippet
 * defined in the JavaDoc of the class `SnippetRegex` and to remove the
 * `*` characters from the start of the line. The result is what you are
 * reading now (or you may be reading the original JavaDoc in the Java
 * file.)
 *
 * The replace configuration string should have the syntax
 *
 *     W + search string + W + replace string + W
 *
 * where `W` is just any character. It is usually `/` so that the search
 * and replace string looks like the usual vi editor search and replace.
 *
 * When we write regular expressions inside the `replace` string it is
 * interpreted as a Java string already twice. This means that the
 * escape characters used in regular expressions as well as in strings,
 * the backslash characters had to be repeated four times. This would
 * greatly decrease readability. Instead of `\s` we could write
 * `\\\\s`. (As a matter of fact it is a possibility.)
 *
 * To lessen the number of backslash characters and to avoid building
 * `\\\\\\` fences instead of coding it is possible to define a
 * character that is used instead of `\` in the regular expressions. The
 * configuration parameter is `escape` and you can write on a segment
 * line
 *
 *     regex="replace='/^~s*~*~s?//' escape='~'
 *
 * to use the tilde character instead of
 *
 *     regex="replace='/^\\\\s*\\\\*\\\\s?//'
 *
 * The recommendation is to use the tilde characters. There can only be
 * one `escape` definition in a single segment, but there can be many
 * `replace` strings and they will be executed in the order they are
 * defined.
 *
 * Note that the default escape string is a string and not only a single
 * character, but it does not really make sense to use many characters
 * when the major aim of this configuration is to shorten the regular
 * expression escape sequences.
 *
 * If you want to use the same escape string in all the snippet regex
 * modifications then you can configure it in the builder when the
 * regex object is created.
 *
 * // end snippet
 */
@Geci("configBuilder localConfigMethod='' configurableMnemonic='regex'")
public class SnippetRegex extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
        private String replace = "";
        private String escape = "";
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var search = new ArrayList<String>();
        final var replace = new ArrayList<String>();
        final var escape = params.get("escape");
        for (final var rep : params.getValueList("replace")) {
            if (rep.length() == 0) {
                throw new GeciException("Replace parameter in snippet " + snippet.name() + " in source " + source.getAbsoluteFile() + " has zero length");
            }
            final var startChar = rep.substring(0, 1);
            if (!rep.endsWith(startChar)) {
                throw new GeciException("Replace parameter in snippet " + snippet.name() + " in source " + source.getAbsoluteFile() + " does not end with the character it starts with.");
            }
            final var sepPos = rep.indexOf(startChar, 1);
            search.add((
                    escape.length() > 0
                        ?
                            (Function<String, String>) (String s) -> s.replace(escape, "\\")
                        :
                        (Function<String, String>) (String s) -> s
                ).apply(rep.substring(1, sepPos))
            );
            replace.add(rep.substring(sepPos + 1, rep.length() - 1));
        }

        final var modifiedLines = new ArrayList<String>();
        for (final var line : snippet.lines()) {
            var s = line;
            for (int i = 0; i < search.size(); i++) {
                s = s.replaceAll(search.get(i), replace.get(i));
            }
            modifiedLines.add(s);
        }
        snippet.lines().clear();
        snippet.lines().addAll(modifiedLines);
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "regex";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static SnippetRegex.Builder builder() {
        return new SnippetRegex().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder {
        public Builder escape(String escape) {
            config.escape = escape;
            return this;
        }

        public Builder replace(String replace) {
            config.replace = replace;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public SnippetRegex build() {
            return SnippetRegex.this;
        }
    }
    //</editor-fold>
}
