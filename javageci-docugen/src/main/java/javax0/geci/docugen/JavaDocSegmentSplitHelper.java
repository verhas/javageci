package javax0.geci.docugen;

import javax0.geci.api.SegmentSplitHelper;

import java.util.List;
import java.util.regex.Pattern;

public class JavaDocSegmentSplitHelper extends AbstractXMLSegmentSplitHelper {
    public JavaDocSegmentSplitHelper() {
        super(
            Pattern.compile(
                    "^(\\s*)\\*?\\s*<!--\\s*snip\\s+(.*)-->\\s*$"
            ),
            Pattern.compile(
                "^(\\s*)```(\\s*)$" +
                    "|" +
                    "^(\\s*)\\[//]:\\s*#\\s*\\(\\s*end\\s+snip\\s*(.*)\\)\\s*$" +
                    "|" +
                    "^(\\s*)<!--\\s*end\\s+snip\\s*(.*)-->\\s*$"
            ),
            Pattern.compile("DEFAULT SEGMENT") // probably will not match ever, if yes, c'mon!

        );
    }

    protected String stripLine(String line) {
        return line.stripLeading().replaceAll("^\\*","");
    }

    protected String stripContinuationLine(String line) {
        return line.replaceAll("^\\s*\\*","");
    }
}
