package javax0.geci.api;

import java.util.Map;

/**
 * A SegmentSplitHelper helps to identify the start and end of the
 * segments in a source file and that way it helps to split up the
 * source code.
 */
public interface SegmentSplitHelper {

    /**
     * Create a new matcher using the line to match against the segment
     * start and end criteria.
     *
     * @param line which is to be checked if it is a segment start or
     *             end line
     * @return a new matcher (not a Regex matcher!)
     */
    Matcher match(String line);

    /**
     * A matcher that can be used to decide if a line starts a segment
     * or ends a segment. In case the lin starts a segment then this
     * matcher can also be queried for the parsed (key,value)
     * parameters.
     */
    interface Matcher {

        /**
         * @return {@code true} if this line starts a segment.
         */
        boolean isSegmentStart();

        /**
         * @return {@code true} if this line ends a segment.
         */
        boolean isSegmentEnd();

        /**
         * @return the number of spaces that this segment should use as
         *         tabulation. If this line is not a segment starting
         *         line then calling this method is meaningless.
         *          Implementing classes may throw {@link
         *          IllegalArgumentException} in such situations.
         */
        int tabbing();

        /**
         * @return the attributes scanned from the line. If this line is
         *         not a segment starting line then calling this method
         *         is meaningless. Implementing classes may throw
         *         {@link IllegalArgumentException} in such situations.
         */
        Map<String, String> attributes();
    }
}
