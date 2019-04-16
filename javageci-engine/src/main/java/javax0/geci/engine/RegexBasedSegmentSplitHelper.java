package javax0.geci.engine;

import javax0.geci.api.SegmentSplitHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Segment split helper that defines the start and the end of the
 * segment using regular expressions.
 */
public class RegexBasedSegmentSplitHelper implements SegmentSplitHelper {

    private static final Pattern attributePattern = Pattern.compile("([\\w\\d_$]+)\\s*=\\s*\"(.*?)\"");
    final Pattern startPattern;
    final Pattern endPattern;

    /**
     * Create a SegmentSplitHelper using two regular expressions.
     * @param startPattern should match the start line of a segment.
     *                     The regular expression MUST define two
     *                     capture groups that can be queried via {@link
     *                     java.util.regex.Matcher#group(int)}. The
     *                     first one has to match the spaces at the
     *                     start of the line. The length of it will
     *                     define the tabbing of the segment. The second
     *                     should capture the attributes.
     *
     * @param endPattern   should match the end line of a segment.
     *                     No capture groups need to be defined int this
     *                     pattern.
     */
    public RegexBasedSegmentSplitHelper(Pattern startPattern, Pattern endPattern) {
        this.startPattern = startPattern;
        this.endPattern = endPattern;
    }

    @Override
    public SegmentSplitHelper.Matcher match(String line) {
        final var matcher = startPattern.matcher(line);
        final var segmentStart = matcher.matches();
        final Map<String, String> attrs;
        final int tabs;
        if (segmentStart) {
            attrs = parseParametersString(matcher.group(2));
            tabs = matcher.group(1).length();
        } else {
            tabs = 0;
            attrs = null;
        }
        final var segmentEnd = endPattern.matcher(line).matches();
        return new Matcher(segmentStart, segmentEnd, attrs, tabs);
    }

    /**
     * Parses the parameters on the line that contains the {@code // <editor-fold...>} line. For example
     * if the line is
     * <pre>
     *     // <editor-fold id="aa" desc="sample description" other_param="other">
     * </pre>
     * <p>
     * then the map will contain the values:
     * <pre>
     *     Map.of("id","aa","desc","sample description","other_param","other")
     * </pre>
     *
     * @param attributes the string containing the part of the line that is after the {@code editor-fold} and
     *                   before the closing {@code >}
     * @return the attributes map
     */
    private Map<String, String> parseParametersString(String attributes) {
        var attributeMatcher = attributePattern.matcher(attributes);
        var attr = new HashMap<String, String>();
        while (attributeMatcher.find()) {
            var key = attributeMatcher.group(1);
            var value = attributeMatcher.group(2);
            attr.put(key, value);
        }
        return attr;
    }

    class Matcher implements SegmentSplitHelper.Matcher {

        private final boolean segmentStart;
        private final boolean segmentEnd;
        private final Map<String, String> attrs;
        private final int tabs;

        Matcher(boolean segmentStart, boolean segmentEnd, Map<String, String> attrs, int tabs) {
            this.segmentStart = segmentStart;
            this.segmentEnd = segmentEnd;
            this.attrs = attrs;
            this.tabs = tabs;
        }


        @Override
        public boolean isSegmentStart() {
            return segmentStart;
        }

        @Override
        public boolean isSegmentEnd() {
            return segmentEnd;
        }

        @Override
        public int tabbing() {
            if (!segmentStart) {
                throw new IllegalArgumentException("tabbing on " +
                        SegmentSplitHelper.class.getSimpleName() + "." +
                        SegmentSplitHelper.Matcher.class.getSimpleName() +
                        " is not defined when the it is not a segment start.");
            }
            return tabs;
        }

        @Override
        public Map<String, String> attributes() {
            if (!segmentStart) {
                throw new IllegalArgumentException("attributes on " +
                        SegmentSplitHelper.class.getSimpleName() + "." +
                        SegmentSplitHelper.Matcher.class.getSimpleName() +
                        " are not defined when the it is not a segment start.");
            }
            return attrs;
        }
    }
}
