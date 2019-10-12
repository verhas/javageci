package javax0.geci.util;

import javax0.geci.engine.RegexBasedSegmentSplitHelper;

import java.util.Arrays;
import java.util.regex.Pattern;

public class JavaSegmentSplitHelper extends RegexBasedSegmentSplitHelper {
    public JavaSegmentSplitHelper() {
        super(Pattern.compile("^(\\s*)//\\s*<\\s*editor-fold\\s+(.*)>\\s*$"),
                Pattern.compile("^\\s*//\\s*<\\s*/\\s*editor-fold\\s*>\\s*$"),
                Pattern.compile("^(\\s*)}\\s*(//.*)?\\s*$"),
                Arrays.asList("desc"));
        setSegmentPreface("//<editor-fold id=\"{{mnemonic}}\">");
        setSegmentPostface("//</editor-fold>");
        defaultOffset = 4;
    }
}
