package javax0.geci.docugen;

import javax0.geci.api.CompoundParams;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.engine.RegexBasedSegmentSplitHelper;

import java.util.List;
import java.util.regex.Pattern;

import static javax0.geci.tools.JDK8Tools.stripLeading;
import static javax0.geci.tools.JDK8Tools.stripTrailing;

public abstract class AbstractXMLSegmentSplitHelper extends RegexBasedSegmentSplitHelper {
    public AbstractXMLSegmentSplitHelper(Pattern startPattern, Pattern endPattern, Pattern defaultPattern) {
        super(startPattern, endPattern, defaultPattern);
    }

    public AbstractXMLSegmentSplitHelper() {
        super(
            Pattern.compile(
                "^(\\s*)<!--\\s*snip\\s+(.*)-->\\s*$"
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
        setSegmentPreface("");
        setSegmentPostface("");
        defaultOffset = 4;
    }

    /**
     * If the line starts with {@code <!-- } then the lines following it are appended to the line until one line is
     * finished using {@code -->}. This makes the use of multi-line segment start possible where the snippets are used.
     * In any other case the call is just falling over to the {@code super.match()}
     *
     * @param lines the list of lines
     * @param i     the index of the current line
     * @return a new matcher just like {@link #match(String)}.
     */
    @Override
    public SegmentSplitHelper.Matcher match(List<String> lines, int i) {
        final var line = lines.get(i);
        if (stripLine(line).startsWith("<!--")) {
            final var sb = new StringBuilder();
            int j;
            for (j = i; j < lines.size(); j++) {
                sb.append(stripContinuationLine(lines.get(j)));
                if (stripTrailing(lines.get(j)).endsWith("-->")) {
                    break;
                }
            }
            return new Matcher(match(sb.toString()), j - i + 1);
        } else {
            return match(line);
        }
    }

    protected String stripLine(String line) {
        return stripLeading(line);
    }

    protected String stripContinuationLine(String line) {
        return line;
    }

    protected static class Matcher implements SegmentSplitHelper.Matcher {

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
