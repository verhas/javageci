package javax0.geci.docugen;

import javax0.geci.engine.RegexBasedSegmentSplitHelper;

import java.util.regex.Pattern;

public class AdocSegmentSplitHelper extends RegexBasedSegmentSplitHelper {

    public AdocSegmentSplitHelper() {
        super(
            Pattern.compile("^(\\s*)//\\s*snip\\s+(.*)\\s*$"),
            Pattern.compile("^(\\s*)//\\s*end\\s+snip\\s+(.*)\\s*$"),
            Pattern.compile("DEFAULT SEGMENT") // probably will not match ever, if yes, c'mon!
        );
        setSegmentPreface("");
        setSegmentPostface("");
        defaultOffset = 4;
    }
}
