package javax0.geci.docugen;

import java.util.regex.Pattern;

public class JavaDocSegmentSplitHelper extends AbstractXMLSegmentSplitHelper {
    public JavaDocSegmentSplitHelper() {
        super(
            Pattern.compile(
                "^(\\s*)\\*?\\s*<!--\\s*snip\\s+(.*)-->\\s*$"
            ),
            Pattern.compile(
                    "^(\\s*)\\*?\\s*<!--\\s*end\\s+snip\\s*(.*)-->\\s*$"
            ),
            Pattern.compile("DEFAULT SEGMENT") // probably will not match ever, if yes, c'mon!

        );
    }

    protected String stripLine(String line) {
        return line.stripLeading().replaceAll("^\\*\\s*", "");
    }

    protected String stripContinuationLine(String line) {
        return line.replaceAll("^(\\s*)\\*\\s*", "$1");
    }
}
