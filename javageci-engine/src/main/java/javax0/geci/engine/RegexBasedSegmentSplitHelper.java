package javax0.geci.engine;

import javax0.geci.api.CompoundParams;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.tools.CompoundParamsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Segment split helper that defines the start and the end of the
 * segment using regular expressions.
 */
public class RegexBasedSegmentSplitHelper implements SegmentSplitHelper {
    private final Pattern startPattern;
    private final Pattern endPattern;
    private final Pattern defaultPattern;
    private final List<String> excludedKeys;
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
    protected void setSegmentPreface(String... segmentPreface) {
        this.segmentPreface = segmentPreface;
    }

    /**
     * Set the postface of a segment. These lines will be used as
     * segment postface in a {@link javax0.geci.api.Segment} when a
     * segment is inserted as a default.
     *
     * @param segmentPostface the lines that contain the postface
     */
    protected void setSegmentPostface(String... segmentPostface) {
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
        this.excludedKeys = Collections.emptyList();
    }

    public RegexBasedSegmentSplitHelper(Pattern startPattern, Pattern endPattern, Pattern defaultPattern, List<String> excludedKeys) {
        this.startPattern = startPattern;
        this.endPattern = endPattern;
        this.defaultPattern = defaultPattern;
        this.excludedKeys = excludedKeys;
    }

    @Override
    public SegmentSplitHelper.Matcher match(String line) {
        final var startMatcher = startPattern.matcher(line);
        final var segmentStart = startMatcher.matches();
        final CompoundParams attrs;
        int tabs = 0;
        if (segmentStart) {
            final var paramsDef = getGroup(2, startMatcher);
            if (paramsDef == null) {
                throw new IllegalArgumentException("Start pattern in "
                    + this.getClass()
                    + "\n"
                    + startMatcher
                    + "\ndoes not give a second matching group. This is probably a coding error in that class.");
            }
            attrs = new CompoundParamsBuilder(paramsDef).exclude(excludedKeys.toArray(new String[0])).redefineId().build();
            final var startSpaces = getGroup(1, startMatcher);
            if (startSpaces == null) {
                throw new IllegalArgumentException("Start pattern in "
                    + this.getClass()
                    + "\n"
                    + startMatcher
                    + "\ndoes not give a first matching group. This is probably a coding error in that class.");
            }

            tabs = startSpaces.length();
        } else {
            attrs = null;
        }
        final var segmentEnd = endPattern.matcher(line).matches();
        final var defaultMatcher = defaultPattern.matcher(line);
        final var segmentDefault = defaultMatcher.matches();
        if (segmentDefault) {
            tabs = defaultMatcher.group(1).length() + defaultOffset;
        }
        return new Matcher(segmentStart, segmentEnd, segmentDefault, attrs, tabs);
    }

    /**
     * Get the {@code count}-th group that is not {@code null}. That way the pattern may define alternative
     * matching lines each with its own groups. For example:
     *
     * <pre>
     *    {@code
     *    zzz(.*)qqq(.*)|(\w*)bbb(.hhh)
     *    }
     * </pre>
     * <p>
     * defines four groups that can be referenced as {@code matcher.group(1)}, {@code matcher.group(2)}, {@code
     * matcher.group(3)} and {@code matcher.group(4)}. When one of the alternatives are matched then the other groups
     * will return {@code null}. What we need is the first or second group that was matched.
     *
     * @param count   the number of group that we need
     * @param matcher the matcher in which the groups are
     * @return the string of the matching group or {@code null} in case there is no non-{@code null} matching group
     */
    private static String getGroup(int count, java.util.regex.Matcher matcher) {
        for (int i = 1; i <= matcher.groupCount(); i++) {
            if (matcher.group(i) != null) {
                count--;
            }
            if (count == 0) {
                return matcher.group(i);
            }
        }
        return null;
    }

    protected static class Matcher implements SegmentSplitHelper.Matcher {

        private final boolean segmentStart;
        private final boolean segmentEnd;
        private final boolean segmentDefault;
        private final CompoundParams attrs;
        private final int tabs;

        protected Matcher(boolean segmentStart, boolean segmentEnd, boolean segmentDefault, CompoundParams attrs, int tabs) {
            this.segmentStart = segmentStart;
            this.segmentEnd = segmentEnd;
            this.segmentDefault = segmentDefault;
            this.attrs = attrs;
            this.tabs = tabs;
        }

        @Override
        public int headerLength(){
            return segmentDefault ? 0 : 1;
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
        public CompoundParams attributes() {
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
