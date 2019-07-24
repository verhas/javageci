package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *- doc
 *
 * The `regex` snippet generator goes through each line of the snippet
 * and does regular expression based search and replace and it also
 * deletes certain lines that match a regular expression kill pattern.
 *
 * It can be used for example as
 *
 *      regex="replace='/a/b/' kill='abraka'"
 *
 * to replace each occurrence of `a` to `b` and the same time delete all
 * lines from the snippet that contains `abraka`.
 *
 * A real life example is:
 *
 *      regex="replace='/^\\\\s*\\\\*\\\\s?//'
 *
 * It was used in the documentation of DOCUGEN.md to include the snippet
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
 * regex snippet modifier generator object is created.
 *
 * It is to note that by default the replacements are performed on each
 * line and then the code decides if the line has to be killed or not.
 * You can reverse this order using the configuration
 *
 *     killFirst=true
 *
 * on the segment header. If there is a need for this parameter it is a
 * smell that something is overcomplicated in the use of the snippets.
 *
 */
@Geci("configBuilder localConfigMethod='' configurableMnemonic='regex'")
public class SnippetRegex extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
        private String replace = "";
        private String escape = "";
        private String kill = "";
        private String killFirst = "false";
    }

    private static String descape(String s, String escape) {
        if (escape == null || escape.length() == 0) {
            return s;
        }
        return s.replace(escape, "\\");
    }

    /**
     * Split up the {@code replaceString} into two parts.
     *
     * <p>The string should have the syntax {@code /search/replace/} and
     * you can just use any character instead of {@code /}. The
     * requirement is that the string starts and ends with the same
     * character and the search part ends at the second occurence of the
     * character. Thus {@code asearchareplacea} is totally legit. The
     * first and last character is {@code a}, the search string is
     * {@code se} the replacement string is {@code rchareplace}. It is
     * recommended to use {@code /} if it does not appear in the search
     * string and if {@code /} is not usable then use some other special
     * character like {@code |} or {@code :}.
     *
     * <p>The search part of the string is interpreted later as a
     * regular expression. Because the place where these expressions are
     * used are many times inside strings that are indeed inside strings
     * it would require {@code \\\\} instead of a single {@code \}.
     * (Every {@code \\} becomes {@code \} when the string is parsed and
     * string inside string is parsed twice so we end up drawing fences.
     *
     * <p>The remediation of this is that the regular expressions in the
     * application at lot of places are allowed to use a different
     * character instead of {@code |}. (It can be a multiple character
     * string, but the recommendation is to use a single character and
     * if possible that single character should be {@code ~} tilde.) The
     * character that can be used instead of a {@code \} is usually
     * defined in a snippet parameter named {@code escape}. The value of
     * this parameter is in the argument {@code escape}. If this
     * parameter is not null or zero length then all occurrences of this
     * character in the search string will be replaces with the
     * backslash character. That way a regular expression can be written
     * like {@code ~s(.*?)~s} instead of {@code \\\\s(.*?)\\\\s} to get
     * the final regular expression {@code \s(.*?)\s}.
     *
     * <p>If the string starts with a different character than it ends
     * with, if that character cannot be found in between and thus the
     * string cannot be split into two parts, or if the string is too
     * short (the minimum length is 3 as for {@code ///} that replaces
     * the empty string with empty string and it is useless anyway but
     * still okay) then the code will throw a {@link GeciException}.
     *
     * @param replaceString the string to split up
     * @param escape        the escape character (or string)
     * @param snippet       the snippet. this parameter is used to
     *                      construct meaningful exception pointing out
     *                      the name of te snippet where the malformed
     *                      replace string is
     * @param source        the source. This parameter is also used only
     *                      to create meaningful exceptions.
     * @return the array of the two parts, {@code part[0]} the search
     * regular expression and {@code part[1]} the replacement string
     */
    private static String[] getParts(String replaceString,
                                     String escape,
                                     Snippet snippet,
                                     Source source
    ) {
        if (replaceString.length() < 3) {
            throw new GeciException("Replace parameter in snippet "
                    + snippet.name()
                    + " in source "
                    + source.getAbsoluteFile()
                    + " is too short (3, like '///' is the minimum).");
        }
        final var startChar = replaceString.substring(0, 1);
        if (!replaceString.endsWith(startChar)) {
            throw new GeciException("Replace parameter in snippet "
                    + snippet.name()
                    + " in source "
                    + source.getAbsoluteFile()
                    + " does not end with the character it starts with.");
        }
        final var mid = replaceString.indexOf(startChar, 1);
        if (mid == replaceString.length() - 1) {
            throw new GeciException("Replace parameter in snippet "
                    + snippet.name()
                    + " in source "
                    + source.getAbsoluteFile()
                    + " does not have two parts only one.");
        }
        final var part = new String[2];
        part[0] = descape(replaceString.substring(1, mid), escape);
        part[1] = replaceString.substring(mid + 1, replaceString.length() - 1);
        return part;
    }

    private static boolean lineIsKilled(List<Pattern> patterns, String line, Source source, Segment segment, Snippet snippet) {
        for (final var pattern : patterns) {
            if (pattern.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

    private Pattern getKillPattern(String regex, Snippet snippet, Source source) {
        try {
            return Pattern.compile(regex);
        } catch (Exception e) {
            throw new GeciException("Line kill pattern '" + regex + "' in "
                    + snippet.name()
                    + " in source "
                    + source.getAbsoluteFile()
                    + " cannot be compiled.", e);
        }
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var search = new ArrayList<String>();
        final var replace = new ArrayList<String>();
        final var escape = params.get("escape");
        if (params.getValueList("replace") != null) {
            for (final var rep : params.getValueList("replace")) {
                final var part = getParts(rep, escape, snippet, source);
                search.add(part[0]);
                replace.add(part[1]);
            }
        }

        final var kill = new ArrayList<Pattern>();
        if (params.getValueList("kill") != null) {
            for (final var regex : params.getValueList("kill")) {
                kill.add(getKillPattern(descape(regex, escape), snippet, source));
            }
        }
        final var modifiedLines = new ArrayList<String>();
        for (final var line : snippet.lines()) {
            var s = line;
            if (params.is("killFirst") && lineIsKilled(kill, s, source, segment, snippet)) {
                continue;
            }
            for (int i = 0; i < search.size(); i++) {
                try {
                    s = s.replaceAll(search.get(i), replace.get(i));
                } catch (PatternSyntaxException pse) {
                    throw new GeciException("There is a problem with the pattern\n"
                            + search.get(i)
                            + "in snippet "
                            + snippet.name()
                            + " in source "
                            + source.getAbsoluteFile(), pse);
                }
            }
            if (!params.is("killFirst") && lineIsKilled(kill, s, source, segment, snippet)) {
                continue;
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

        public Builder kill(String kill) {
            config.kill = kill;
            return this;
        }

        public Builder killFirst(String killFirst) {
            config.killFirst = killFirst;
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
