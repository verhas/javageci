package javax0.geci.docugen;

import javax0.geci.api.CompoundParams;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.engine.RegexBasedSegmentSplitHelper;

import java.util.Map;
import java.util.regex.Pattern;

public class MarkdownSegmentSplitHelper  extends RegexBasedSegmentSplitHelper {
    public MarkdownSegmentSplitHelper() {
        super(Pattern.compile(
                "^(\\s*)\\[//]:\\s*\\(\\s*snip\\s+(.*)\\)\\s*$"),
                Pattern.compile("^\\s*```\\s*$|^(\\s*)\\[//]:\\s*\\(\\s*end\\s+snip\\s*(.*)\\)\\s*$"),
                Pattern.compile("DEFAULT SEGMENT")); // probably will not match ever, if yes, c'mon!
        setSegmentPreface("");
        setSegmentPostface("");
        defaultOffset = 4;
    }
}
