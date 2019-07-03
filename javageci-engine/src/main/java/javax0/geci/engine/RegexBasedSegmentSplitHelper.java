package javax0.geci.engine;

import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.tools.CompoundParamsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Segment split helper that defines the start and the end of the
 * segment using regular expressions.
 */
public class RegexBasedSegmentSplitHelper implements SegmentSplitHelper {
    final Pattern startPattern;
    final Pattern endPattern;
    final Pattern defaultPattern;
    protected int defaultOffset = 0;
    private String[] segmentPreface = new String[]{""};
    private String[] segmentPostface = new String[]{""};

    /**
     * Set the preface of a segment. These lines will be used as segment
     * preface in a {@link javax0.geci.api.Segment} when a segment is
     * inserted as a default.
     *
     * @param segmentPreface the lines that contain the preface
     */
    protected void setSegmentPreface(String ... segmentPreface) {
        this.segmentPreface = segmentPreface;
    }
    /**
     * Set the postface of a segment. These lines will be used as
     * segment postface in a {@link javax0.geci.api.Segment} when a
     * segment is inserted as a default.
     *
     * @param segmentPostface the lines that contain the postface
     */
    protected void setSegmentPostface(String ... segmentPostface) {
        this.segmentPostface = segmentPostface;
    }

    public String[] getSegmentPreface() {
        return segmentPreface;
    }

    public String[] getSegmentPostface() {
        return segmentPostface;
    }

    /**
     * Create a SegmentSplitHelper using three regular expressions.
     *
     * @param startPattern   should match the start line of a segment.
     *                       The regular expression MUST define two
     *                       capture groups that can be queried via {@link
     *                       java.util.regex.Matcher#group(int)}. The
     *                       first one has to match the spaces at the
     *                       start of the line. The length of it will
     *                       define the tabbing of the segment. The second
     *                       should capture the name of the segment and
     *                       the attributes.
     * @param endPattern     should match the end line of a segment.
     *                       No capture groups need to be defined int this
     *
     * @param defaultPattern pattern to find the default location of a
     *                       segment. When a segment cannot be found
     *                       using the start and the end pattern then
     *                       this pattern is used. When a line matches
     *                       this pattern then the segment will be
     *                       inserted before this line. The insertion
     *                       will include the segment start and end line
     *                       so any consecutive execution will already
     *                       find the segment.
     */
    public RegexBasedSegmentSplitHelper(Pattern startPattern, Pattern endPattern, Pattern defaultPattern) {
        this.startPattern = startPattern;
        this.endPattern = endPattern;
        this.defaultPattern = defaultPattern;
    }

    @Override
    public SegmentSplitHelper.Matcher match(String line) {
        final var startMatcher = startPattern.matcher(line);
        final var segmentStart = startMatcher.matches();
        final Map<String, String> attrs;
        int tabs = 0;
        if (segmentStart) {
            attrs = parseParametersString(startMatcher.group(2));
            tabs = startMatcher.group(1).length();
        } else {
            attrs = null;
        }
        final var segmentEnd = endPattern.matcher(line).matches();
        final var defaultMatcher = defaultPattern.matcher(line);
        final var segmentDefault = defaultMatcher.matches();
        if( segmentDefault ){
            tabs = defaultMatcher.group(1).length() + defaultOffset;
        }
        return new Matcher(segmentStart, segmentEnd, segmentDefault, attrs, tabs);
    }

    /**
     * Parses the parameters on the line that contains the
     * {@code // <editor-fold...>} line. For example if the line is
     * <pre>{@code
     *     // <editor-fold id="aa" desc="sample description" other_param="other">
     *  }
     * </pre>
     * <p>
     * then the map will contain the values:
     * <pre>{@code
     *     "id" -> "aa"
     *     "desc" -> "sample description"
     *     "other_param" -> "other"
     * }
     * </pre>
     *
     * @param attributes the string containing the part of the line that is after the {@code editor-fold} and
     *                   before the closing {@code >}
     * @return the attributes map
     */
    public static Map<String, String> parseParametersString(String attributes) {
        var params = new CompoundParamsBuilder(attributes).redefineId().build();
        var attr = new HashMap<String, String>();
        for ( final var key : params.keySet()) {
            var value = params.get(key);
            attr.put(key, value);
        }
        attr.put("id",params.id());
        return attr;
    }

    protected class Matcher implements SegmentSplitHelper.Matcher {

        private final boolean segmentStart;
        private final boolean segmentEnd;
        private final boolean segmentDefault;
        private final Map<String, String> attrs;
        private final int tabs;

        protected Matcher(boolean segmentStart, boolean segmentEnd, boolean segmentDefault, Map<String, String> attrs, int tabs) {
            this.segmentStart = segmentStart;
            this.segmentEnd = segmentEnd;
            this.segmentDefault = segmentDefault;
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
        public boolean isDefaultSegmentEnd() {
            return segmentDefault;
        }

        @Override
        public int tabbing() {
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
