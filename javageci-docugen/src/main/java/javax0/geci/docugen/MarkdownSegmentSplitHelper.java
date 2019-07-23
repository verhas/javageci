package javax0.geci.docugen;

import javax0.geci.api.CompoundParams;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.engine.RegexBasedSegmentSplitHelper;

import java.util.List;
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

    @Override
    public SegmentSplitHelper.Matcher match(List<String> lines, int i) {
        final var line = lines.get(i);
        if (line.stripLeading().startsWith("<!--")) {
            final var sb = new StringBuilder();
            int j;
            for (j = i; j < lines.size(); j++) {
                sb.append(lines.get(j));
                if (lines.get(j).stripTrailing().endsWith("-->")) {
                    break;
                }
            }
            return new Matcher(match(sb.toString()), j - i + 1);
        } else {
            return match(line);
        }
    }

    private static class Matcher implements SegmentSplitHelper.Matcher {

        private final SegmentSplitHelper.Matcher matcher;
        private final int headerLength;

        protected Matcher(SegmentSplitHelper.Matcher matcher, int headerLength) {
            this.matcher = matcher;
            this.headerLength = headerLength;
        }

        @Override
        public int headerLength() {
            return headerLength;
        }

        @Override
        public boolean isSegmentStart() {
            return matcher.isSegmentStart();
        }

        @Override
        public boolean isSegmentEnd() {
            return matcher.isSegmentEnd();
        }

        @Override
        public boolean isDefaultSegmentEnd() {
            return matcher.isDefaultSegmentEnd();
        }

        @Override
        public int tabbing() {
            return matcher.tabbing();
        }

        @Override
        public CompoundParams attributes() {
            return matcher.attributes();
        }
    }
}
