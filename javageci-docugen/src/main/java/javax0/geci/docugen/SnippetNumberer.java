package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.ArrayList;

import static javax0.geci.tools.JVM8Tools.space;

/** // snippet SnippetNumberer_doc
 *
 * The `number` snippet handling generator is implemented in the class `SnippetNumberer`.
 * It can number the individual lines in a snippet with increasing numbers.
 * The result can be used to have some code with line numbers so that the surrounding text can reference the actual lines.
 *
 *  // end snippet
 */
@Geci("configBuilder localConfigMethod='' configurableMnemonic='number'")
public class SnippetNumberer extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
        // snippet SnippetNumberer_Config_001
        private String start = "1";
        /*
        This is the number that will be used to denote the first line.
        The default is to start from one and to use increasing numbers.

        end snippet */

        // snippet SnippetNumberer_Config_002
        private String step = "1";
        /*
        This configuration parameter controls the increment between the consecutive numbers.
        If you want to number the lines in step of ten, like we did old times in BASIC then you can set `number="step='10'"`.

        end snippet */

        // snippet SnippetNumberer_Config_003
        private String format = "%d. ";
        /*
        This configuration option controls how the numbers are formatted.
        This is the string that is passed to the `String.format()` method as first argument when converting the line number to string.

        end snippet */

        // snippet SnippetNumberer_Config_004
        private String from = "0";
        /*
        When not all the lines are needed to be numbered in a snippet you can limit the numbering to start at certain line and finish at another line.
        This parameter can specify the index of the first line that is to be numbered.
        The default value is zero, which means that the very first line already will be numbered.
        If you want to number starting with the second line then you can use `number="from=1"` as a configuration.
        If you want to number only the last three lines then you can configure the generator on the segment providing the parameters `number="from=-3"`.

        end snippet */

        // snippet SnippetNumberer_Config_005
        private String to = "";
        /*
        You can limit the end of the numbering.
        This parameter defines the index of the last line that is numbered exclusive.
        If you want to number only the first three lines you can configure the generator as `number="to=3"`.
        That way only the lines with the indexes `0`, `1` and `2` will be numbered.

        Negative numbers, just as in case of `from` count backward from the end of the snippet.
        Empty value, which is the default means no limit.

        end snippet */
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var start = Long.parseLong(params.get("start", config.start));
        final var step = Long.parseLong(params.get("step", config.step));
        final var format = params.get("format", config.format);
        final var startLine = calculateLineNumber(
                params.get("from", config.from),
                snippet.lines().size()
        );
        final var endLine = calculateLineNumber(
                params.get("to", config.to.length() > 0 ? config.to : "" + snippet.lines().size())
                , snippet.lines().size()
        );

        final var modifiedLines = new ArrayList<String>();
        int index = 0;
        long number = start;
        for (final var line : snippet.lines()) {
            final var formattedNumber = String.format(format, number);
            if (index >= startLine && index < endLine) {
                modifiedLines.add(formattedNumber + line);
                number += step;
            } else {
                modifiedLines.add(space(formattedNumber.length()) + line);
            }
            index++;
        }
        snippet.lines().clear();
        snippet.lines().addAll(modifiedLines);
    }

    private long calculateLineNumber(String number, int max) {
        final var z = Long.parseLong(number);
        if (z < 0) {
            return max + z;
        } else {
            return z;
        }
    }


    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "number";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static SnippetNumberer.Builder builder() {
        return new SnippetNumberer().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder format(String format) {
            config.format = format;
            return this;
        }

        public Builder from(String from) {
            config.from = from;
            return this;
        }

        public Builder start(String start) {
            config.start = start;
            return this;
        }

        public Builder step(String step) {
            config.step = step;
            return this;
        }

        public Builder to(String to) {
            config.to = to;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public SnippetNumberer build() {
            return SnippetNumberer.this;
        }
    }
    //</editor-fold>
}

