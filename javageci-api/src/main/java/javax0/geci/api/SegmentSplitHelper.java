package javax0.geci.api;

import java.util.List;

/**
 * A SegmentSplitHelper helps to identify the start and end of the
 * segments in a source file and that way it helps to split up the
 * source code.
 *
 * <p>Different files need different splitting. The way how to split a
 * file is dependent on the format. For example Java files contains
 *
 * <pre>
 *     {@code
 *     //<editor-fold ... >
 *         ...
 *     //</editor-fold>
 *     }
 * </pre>
 *
 * segments. Markdown documents contain
 *
 * <pre>
 *     {@code
 *     ```java
 *      .... snippet content ...
 *     ```
 *     }
 * </pre>
 *
 * segments.
 *
 * An instance of a class, which is the implementation of this interface
 * can be associated with a file name extension and the file collector
 * ({@code javax0.geci.util.FileCollector}) will provide the
 * source object (doing the actual splitting of the source into
 * segments) with the appropriate helper.
 *
 */
public interface SegmentSplitHelper {

    /**
     * Create a new matcher using the line to match against the segment
     * start and end criteria.
     *
     * @param line which is to be checked if it is a segment start or
     *             end line
     * @return a new matcher (not a Regex matcher!) It is a {@code
     * SegmentSplitHelper.Matcher}
     */
    Matcher match(String line);

    /**
     * Create a matcher using the current line and possible reading
     * consecutive or even previous lines. Simple implementations that
     * do not intend to support multi-line segment start do not need
     * to implement this method. The default implementation simply calls
     * {@link #match(String)} with the {@code i}-th line.
     *
     * @param lines the list of lines
     * @param i     the index of the current line
     * @return a new matcher just like {@link #match(String)}.
     */
    default Matcher match(List<String> lines, int i){
        return match(lines.get(i));
    }

    /**
     * In case of default segment creation the strings returned by this
     * method will be inserted before the default segment. These lines
     * usually contain some string(s) that will be recognized by the
     * later executions as a segment start. Also the strings may contain
     * the string {{mnemonic}}, which will be replaced by the actual
     * generator mnemonic.
     *
     * <p>For example the Java segments have a one line preface that is
     *
     * <pre>
     *     {@code
     *     //<editor-fold id="{{mnemonic}}">
     *     }
     * </pre>
     * <p>
     * and another one line
     *
     * <pre>
     *     {@code
     *     "//</editor-fold>"
     *     }
     * </pre>
     * <p>
     * as post face (see {@link #getSegmentPostface()}.
     *
     * @return the array of strings that contain the lines that will be
     * inserted before the generated default segment.
     */
    String[] getSegmentPreface();

    /**
     * In case of default segment creation the strings returned by this
     * method will be inserted after the default segment. These lines
     * usually contain some string(s) that will be recognized by the
     * later executions as a segment end. Also the strings may contain
     * the string {{mnemonic}}, which will be replaced by the actual
     * generator mnemonic.
     *
     * <p>See also {@link #getSegmentPreface()}.
     *
     * @return the array of strings that contain the lines that will be
     * inserted after the generated default segment.
     */
    String[] getSegmentPostface();

    /**
     * A matcher that can be used to decide if a line starts a segment
     * or ends a segment. In case the line starts a segment then this
     * matcher can also be queried for the parsed (key,value)
     * parameters.
     */
    interface Matcher {

        /**
         * @return {@code true} if this line starts a segment.
         */
        boolean isSegmentStart();

        /**
         * The number of the lines that the header of the segment
         * contains. SegmentHelpers that support multi-line segment
         * start should implement this method in their {@link Matcher}
         * implementation. The default implementation simply returns
         * {@code 1}, which means that the segment header is one line.
         *
         * @return the number of the lines in the segment header
         */
        default int headerLength(){
            return 1;
        }

        /**
         * @return {@code true} if this line ends a segment.
         */
        boolean isSegmentEnd();

        /**
         * Checking the line that matches the "default" segment. The
         * default segment is where the segment code will be inserted
         * if there is no segment defined in the source code. In case
         * of Java this is before the last line that contains the class
         * closing '}' character.
         *
         * <p>This method will return {@code true} in case of Java (as
         * an example) when the line contains nothing but a '}'
         * character.
         *
         * @return {@code true} if this line ends the default segment.
         */
        boolean isDefaultSegmentEnd();

        /**
         * @return the number of spaces that this segment should use as
         * tabulation. If this line is not a segment starting
         * line then calling this method is meaningless.
         * Implementing classes may throw {@link
         * IllegalArgumentException} in such situations.
         */
        int tabbing();

        /**
         * @return the attributes scanned from the line. If this line is
         * not a segment starting line then calling this method
         * is meaningless. Implementing classes may throw
         * {@link IllegalArgumentException} in such situations.
         */
        CompoundParams attributes();
    }
}
