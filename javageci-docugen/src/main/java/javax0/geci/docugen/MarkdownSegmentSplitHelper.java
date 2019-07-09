package javax0.geci.docugen;

import javax0.geci.engine.RegexBasedSegmentSplitHelper;

import java.util.regex.Pattern;

public class MarkdownSegmentSplitHelper extends RegexBasedSegmentSplitHelper {
    public MarkdownSegmentSplitHelper() {
        super(
// snippet MarkdownSegmentSplitHelper_patterns
                //startPattern
                // skip 1 line
            Pattern.compile(
                "^(\\s*)\\[//]:\\s*#\\s*\\(\\s*snip\\s+(.*)\\)\\s*$" +
                    "|" +
                        "^(\\s*)<!--\\s*snip\\s+(.*)-->\\s*$"
// skip 1 line
            ),
                // e_ndPattern
// skip 1 line
            Pattern.compile(
                "^(\\s*)```(\\s*)$" +
                    "|" +
                        "^(\\s*)\\[//]:\\s*#\\s*\\(\\s*end\\s+snip\\s*(.*)\\)\\s*$" +
                    "|" +
                        "^(\\s*)<!--\\s*end\\s+snip\\s*(.*)-->\\s*$"
// end snippet
            ),
                //default pattern skip 1 line
                Pattern.compile("DEFAULT SEGMENT") // probably will not match ever, if yes, c'mon!

        );
        setSegmentPreface("");
        setSegmentPostface("");
        defaultOffset = 4;
    }
}
