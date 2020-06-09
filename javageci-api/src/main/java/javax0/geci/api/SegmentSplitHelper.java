package javax0.geci.api;

import java.util.List;

/**
 * <p>A SegmentSplitHelper helps to identify the start and end of the
 * segments in a source file and that way it helps to split up the
 * source code.</p>
 *
 * <p>Different files need different splitting. The way how to split a
 * file is dependent on the format. For example Java files contain</p>
 *
 * <pre>
 *     {@code
 *     //<editor-fold ... >
 *         ...
 *     //</editor-fold>
 *     }
 * </pre>
 *
 * <p>segments. Markdown documents contain</p>
 *
 * <pre>
 *     {@code
 *     ```java
 *      .... snippet content ...
 *     ```
 *     }
 * </pre>
 *
 * <p>segments.</p>
 *
 * <p>An instance of a class, which is the implementation of this interface
 * can be associated with a file name extension and the file collector
 * ({@code javax0.geci.util.FileCollector}) will provide the
 * helper for the {@link Source} object.</p>
 *
 */
public interface SegmentSplitHelper {

    /**
     * <p>Create a new matcher using the line to match against the segment
     * start and end criteria.</p>
     *
     * <p>Note that this method is never used directly by the Geci framework.
     * This method is convenience method to ease the implementation of a
     * segment split helper that can decide that a line is the start or end
     * of a segment without the previous or later lines. The framework calls the
     * {@link #match(List, int)} method that gets all the lines in a list and the
     * index of the current line. The default implementation simply calls this
     * method for the line {@code lines.get(i)}. Thus a file format that can be
     * split in a simple way need to implement only this method.</p>
     *
     * @param line which is to be checked if it is a segment start or
     *             end line
     * @return a new matcher (not a Regex matcher!) It is a {@code
     * SegmentSplitHelper.Matcher}
     */
    Matcher match(String line);

    /**
     * <p>Create a matcher using the current line and possible reading
     * consecutive or even previous lines. Simple implementations that
     * do not intend to support multi-line segment start do not need
     * to implement this method. The default implementation simply calls
     * {@link #match(String)} with the {@code i}-th line.</p>
     *
     * @param lines the list of lines
     * @param i     the index of the current line
     * @return a new matcher just like {@link #match(String)}.
     */
    default Matcher match(List<String> lines, int i){
        return match(lines.get(i));
    }

    /**
     * <p>In case of default segment creation the strings returned by this
     * method will be inserted before the default segment. These lines
     * usually contain some string or strings that will be recognized by the
     * later executions as a segment start. Also the strings may contain
     * the string {{mnemonic}}, which will be replaced by the actual
     * generator mnemonic when inserted into the source file.</p>
     *
     * <p>For example the Java segments have a one line preface that is</p>
     *
     * <pre>
     *     {@code
     *     //<editor-fold id="{{mnemonic}}">
     *     }
     * </pre>
     *
     * <p>and another one line</p>
     *
     * <pre>
     *     {@code
     *     "//</editor-fold>"
     *     }
     * </pre>
     *
     <p>* as post face (see {@link #getSegmentPostface()}.</p>
     *
     * @return the array of strings that contain the lines that will be
     * inserted before the generated default segment.
     */
    String[] getSegmentPreface();

    /**
     * <p>In case of default segment creation the strings returned by this
     * method will be inserted after the default segment. These lines
     * usually contain some string or strings that will be recognized by the
     * later executions as a segment end. Also the strings may contain
     * the string {{mnemonic}}, which will be replaced by the actual
     * generator mnemonic when inserted into the source file.</p>
     *
     * <p>See also {@link #getSegmentPreface()}.</p>
     *
     * @return the array of strings that contain the lines that will be
     * inserted after the generated default segment.
     */
    String[] getSegmentPostface();

    /**
     * <p>A matcher that can be used to decide if a line starts a segment
     * or ends a segment. In case the line starts a segment then this
     * matcher can also be queried for the parsed (key,value)
     * parameters.</p>
     */
    interface Matcher {

        /**
         * @return {@code true} if this line starts a segment.
         */
        boolean isSegmentStart();

        /**
         * <p>The number of the lines that the header of the segment
         * contains. SegmentHelpers that support multi-line segment
         * start should implement this method in their {@link Matcher}
         * implementation. The default implementation simply returns
         * {@code 1}, which means that the segment header is one line.</p>
         *
         * <p>An example of a multi-line header is a typical snippet header. The snippet
         * header may contain a lot of parameters and to avoid excessively long lines
         * in the AsciiDoc or Markdown document the {@link SegmentSplitHelper} recognizes when
         * the segment start contains many lines. In that case the new {@code Matcher}
         * object is created with the count of the lines of the continuations. Later this method
         * returns this value and the code that replaces the content of the segment will know that
         * after the first line of the segment start this many lines should be left intact
         * and only the lines after these must be replaced with the new content.</p>
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
         * <p>Checking the line that matches the location of the "default" segment. The
         * default segment location is where the segment code will be inserted
         * if there is no segment defined in the source code. In case
         * of Java this is before the last line that contains the class
         * closing '}' character. (Not just any line containing a '}' character but the
         * very last line that contains only a '}' character.)</p>
         *
         * <p>This method will return {@code true} in case of Java (as
         * an example) when the line contains nothing but a '}'
         * character.</p>
         *
         * @return {@code true} if this line ends the default segment.
         */
        boolean isDefaultSegmentEnd();

        /**
         * @return the number of spaces that this segment should use as
         * tabulation. If this line is not a segment starting
         * line then calling this method is meaningless.
         * Implementing classes may throw {@link
         * IllegalArgumentException} in such situations. The
         * framework will not call this method unless this matcher matches
         * a segment start, thus throwing the exception is only code quality
         * and consistency.
         */
        int tabbing();

        /**
         * @return the attributes scanned from the line. If this line is
         * not a segment starting line then calling this method
         * is meaningless. Implementing classes may throw
         * {@link IllegalArgumentException} in such situations. The
         * framework will not call this method unless this matcher matches
         * a segment start, thus throwing the exception is only code quality
         * and consistency.
         */
        CompoundParams attributes();
    }
}
