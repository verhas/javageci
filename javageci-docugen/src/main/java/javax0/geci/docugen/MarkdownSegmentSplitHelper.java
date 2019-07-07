package javax0.geci.docugen;

import javax0.geci.engine.RegexBasedSegmentSplitHelper;

import java.util.regex.Pattern;

public class MarkdownSegmentSplitHelper extends RegexBasedSegmentSplitHelper {
    public MarkdownSegmentSplitHelper() {
        super(

            //startPattern
            Pattern.compile(
                "^(\\s*)\\[//]:\\s*#\\s*\\(\\s*snip\\s+(.*)\\)\\s*$" +
                    "|" +
                    "^(\\s*)<!--\\s*snip\\s+(.*)-->\\s*$"),

            // endPattern
            Pattern.compile(
                "^(\\s*)```(\\s*)$" +
                    "|" +
                    "^(\\s*)\\[//]:\\s*#\\s*\\(\\s*end\\s+snip\\s*(.*)\\)\\s*$" +
                    "|" +
                    "^(\\s*)<!--\\s*end\\s+snip\\s*(.*)-->\\s*$"),

            //default pattern
            Pattern.compile("DEFAULT SEGMENT") // probably will not match ever, if yes, c'mon!

        );
        setSegmentPreface("");
        setSegmentPostface("");
        defaultOffset = 4;
    }
}
