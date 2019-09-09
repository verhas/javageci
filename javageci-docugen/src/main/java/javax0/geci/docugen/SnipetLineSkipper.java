package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.ArrayList;
import java.util.regex.Pattern;

/** // snippet SnipetLineSkipper_doc
 *
 *  The `skip` functionality is implemented in the class `SnippetLineSkipper`. This generator can remove certain lines
 *  from a snippet that are denoted to be removed. Many times some code segment contains lines that are not necessarily
 *  in the documentation and may distract the reader from the important points of the code. For example you want to
 *  include a whole Java class into the documentation and you believe that the `import` statements are not that
 *  important. In such cases the snippet may contain some lines that the `SnippetLineSkipper` will recognize and remove
 *  the lines indicated. There are three different possibilities.
 *
 *  The first one is to signal the start and the end of the lines to be removed like
 *
 *         // skip
 *         import javax0.geci.annotations.Geci;
 *         import javax0.geci.api.CompoundParams;
 *         import javax0.geci.api.Segment;
 *         import javax0.geci.api.Source;
 *
 *         import java.util.ArrayList;
 *         import java.util.regex.Pattern;
 *         // skip end
 *
 * The skipper will remove the lines `// skip`, `// skip end` and also the lines between.
 *
 * The second possibility is to write
 *
 *         // skip N lines
 *
 * The number `N` has to be a positive decimal number and the following `N` lines plus the `// skip N lines` will also
 * be deleted.
 *
 * The third possibility is to write
 *
 *        // skip till regexp
 *
 * that will delete this line and all following lines until there is a line that matches the regular expression.
 *
 * The generator will work only for segments that has the configuration parameter `skip="do"` with non zero length string
 * value. There is only one configuration possibility. If the value of the `skip` parameter on the segment is `remove`
 * (a.k.a. the segment contains the `skip="remove"` parameter) then only the lines that are instructions for the line
 * skippings are removed. In any other cases the line removing is performed as described above.
 *
 * In the generator builder the regular expression patterns that match `skip`, `skip end` and so on are configurable.
 *
 *  // end snippet
 */
@Geci("configBuilder localConfigMethod='' configurableMnemonic='skip'")
public class SnipetLineSkipper extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
        // snippet SnipetLineSkipper_Config_001
        private Pattern skip = Pattern.compile("skip");
        /*

        This pattern defines the line that starts the skipping and which skipping is finished by the pattern configured
        using the next configuration option `skipEnd`.

        end snippet */

        // snippet SnipetLineSkipper_Config_002
        private Pattern skipEnd = Pattern.compile("skip\\s+end");
        /*

        This pattern defines the line that stops line skipping in case it was started using a line matched by the
        previous pattern `skip`.

        end snippet */

        // snippet SnipetLineSkipper_Config_003
        private Pattern skipNrLines = Pattern.compile("skip\\s+(\\+?\\d+)\\s+lines?");
        /*

        This pattern finds the line that signals that a certain number of lines should be skipped. If configured other
        than the default then the regular expression MUST have a single matching group (the part between the parentheses)
        that will match a substring that is a positive decimal number.

        end snippet */

        // snippet SnipetLineSkipper_Config_004
        private Pattern skipTill = Pattern.compile("skip\\s+till\\s+/(.*?)/");
        /*

        This pattern finds athe line that is the start of skipping specifying a regular expression that should match
        a later line, which will be the stopping signal for skipping. The line that matches the regular expression
        at the end will NOT be included in the snippet. It will be skipped.

        If configured other than the default then the regular expression MUST have a single matching group (the part
        between the parentheses) that will match a substring that will be used as a regular expression to find the
        skipping end.

        end snippet */
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        if (params.id().equals("remove")) {
            removeSkippers(snippet);
        } else {
            skipLines(snippet);
        }
    }


    private void removeSkippers(Snippet snippet) {
        final var modifiedLines = new ArrayList<String>();
        for (final var line : snippet.lines()) {
            if (!config.skip.matcher(line).find()) {
                modifiedLines.add(line);
            }
        }
        snippet.lines().clear();
        snippet.lines().addAll(modifiedLines);
    }

    private void skipLines(Snippet snippet) {
        final var modifiedLines = new ArrayList<String>();
        var skipping = false;
        int skipCounter=0;
        Pattern skipPattern = null;
        for (final var line : snippet.lines()) {
            if (skipping) {
                if (skipCounter > 0) {
                    skipCounter--;
                    skipping = skipCounter > 0 ;
                    skipPattern = null;
                    continue;
                }
                if( skipPattern != null && skipPattern.matcher(line).find()){
                    modifiedLines.add(line);
                    skipping = false;
                    skipPattern = null;
                    skipCounter = 0;
                    continue;
                }
                if( config.skipEnd.matcher(line).find()){
                    skipping = false;
                    skipPattern = null;
                    skipCounter = 0;
                    continue;
                }
            } else {
                final var skipNrLinesMatcher = config.skipNrLines.matcher(line);
                if (skipNrLinesMatcher.find()) {
                    skipCounter = Integer.parseInt(skipNrLinesMatcher.group(1));
                    skipPattern = null;
                    skipping = true;
                    continue;
                }
                final var skipTillMatcher = config.skipTill.matcher(line);
                if (skipTillMatcher.find()) {
                    skipPattern = Pattern.compile(skipTillMatcher.group(1));
                    skipCounter = 0;
                    skipping = true;
                    continue;
                }
                final var skipMatcher = config.skip.matcher(line);
                if (skipMatcher.find()) {
                    skipPattern = null;
                    skipCounter = 0;
                    skipping = true;
                    continue;
                }
                modifiedLines.add(line);
            }
        }
        snippet.lines().clear();
        snippet.lines().addAll(modifiedLines);
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "skip";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static SnipetLineSkipper.Builder builder() {
        return new SnipetLineSkipper().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder skip(java.util.regex.Pattern skip) {
            config.skip = skip;
            return this;
        }

        public Builder skipEnd(java.util.regex.Pattern skipEnd) {
            config.skipEnd = skipEnd;
            return this;
        }

        public Builder skipNrLines(java.util.regex.Pattern skipNrLines) {
            config.skipNrLines = skipNrLines;
            return this;
        }

        public Builder skipTill(java.util.regex.Pattern skipTill) {
            config.skipTill = skipTill;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public SnipetLineSkipper build() {
            return SnipetLineSkipper.this;
        }
    }
    //</editor-fold>
}
