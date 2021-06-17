package javax0.geci.api;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <p>A {@code Segment} object represents an editor-fold part in the source
 * file that the code generator can write. A {@code Segment} is
 * retrieved by the code generator calling the {@link
 * Source#open(String)} method of the source and it can write into the
 * segment.</p>
 *
 * <p>When the code generator generates code into something that is not
 * Java code then the segment start and end is not 'editor-fold'. The
 * recognition of the start and the end of the segment is handled by
 * different {@link SegmentSplitHelper} implementations. The implementation
 * of this interface does not need to care about how the source file is
 * split up into a series of [manual] - segment - manual - segment - ... - [manual]
 * parts.</p>
 */
public interface Segment extends AutoCloseable {
    long COMMENT_TOUCH = 0x00000001L;

    /**
     * <p>A {@code Source} object, as implemented in the engine (not in
     * the API though) is touched when a generator has written anything
     * into any segment. It is touched even if the generator was writing
     * the same code as the already existing text in the segment. This
     * is discovered during source consolidation, and it is used to check
     * that the generators have touched at least one source object. If
     * no generator touched any of the source objects then there is no
     * point to run the generators, and this is probably a configuration
     * error. (Unless the generator implements the interface {@link
     * Distant}.)</p>
     *
     * <p>Some generators do certain things that may need "special
     * touch". The generator can call this method on a segment to signal
     * that it was touching the text of the segment in a special
     * way. There are 64bits in a long to signal the different ways.</p>
     *
     * <p>At the introduction of this feature there is only one bit used
     * and there is a constant {@link #COMMENT_TOUCH} defined for this
     * type of touch. The generators may call this method passing this
     * constant to signal that the generator was writing some text into
     * a comment into the Java code.</p>
     *
     * <p>Later, when the framework checks if the source is intact or
     * was modified looks at this bit. If any of the generators
     * signalled that it touched the comments then the comparison will
     * take comment literals into account. If the generators did not
     * touch the comments then the comments are ignored when the change
     * is asserted.</p>
     *
     * <p>When a source object is consolidated (the final set of lines
     * is generated from the original lines and from the contents of the
     * segments) then the source inherits the special touch bits of the
     * segments. Any of the 64bits will be set in the source object if
     * that bit is set in any of the segments in the source object.</p>
     *
     * <p>The generators should use this method to set a certain bit in
     * the segment special touch bits. There is no possibility to reset
     * a bit.</p>
     *
     * @param touchValue is a long value with certain bits set. The
     *                   touch bits will be OR-ed together with the
     *                   already set bits. If you just want to query the
     *                   current bit values then pass {@code 0} as
     *                   argument.
     * @return the new touch bits value.
     */
    long touch(long touchValue);

    /**
     * Get the lines of the segment as it is at the moment. This is the
     * text that was written into the segment by the code generator.
     *
     * @return the textual content of the segment as it is at the moment.
     */
    String getContent();

    /**
     * Get the original lines of the segment as it was in the source
     * code before starting to overwrite it.
     *
     * @return the list of the lines as strings
     */
    List<String> originalLines();

    /**
     * When the generator does not find a segment based on the starting
     * and ending line it may decide to insert the segment towards the
     * end of the file. For example in case of Java the generator (in
     * class {@link Source} can insert the segment before the last
     * closing '}' character.
     *
     * <p>In that case, however, the segment start and segment end lines
     * should also be inserted to the code, as they are not part of the
     * source yet. When such a segment is created {@code preface} should
     * contain the segment start line and {@code postface} should
     * contain the lines that are the end of the segment.
     *
     * <p>When the code generation runs next time these lines will
     * already be recognized. This is to ease the work of the developer
     * inserting the segment start and end when the code generator runs
     * the first time.
     *
     * @param preface the preface lines
     */
    void setPreface(String... preface);

    /**
     * Get the parameters that were defined in the source.
     *
     * @return the compound parameters that were created while scanning
     * the source code
     */
    CompoundParams sourceParams();

    /**
     * See the documentation of {@link #setPreface(String...)}
     *
     * @param postface the postface lines that end a segment
     */
    void setPostface(String... postface);

    /**
     * Set the content of the segment.
     * <p>
     * The intended use of this method along with the getter is to let a code write a segment that contains
     * macro placeholders and in the last step replace the collected and formatted content with the one that
     * resolves the placeholders.
     *
     * @param content the new content of the segment.
     */
    void setContent(String content);

    /**
     * Write a line to the segment after the last line.
     *
     * @param s          the content of the line. Nothing is printed if {@code null}.
     * @param parameters parameters that are used as actual values in the {@code s} format string
     * @return {@code this}
     */
    Segment write(String s, Object... parameters);

    /**
     * Write all the lines of the segment into this segment.
     *
     * @param segment the other segment that contains lines that were created beforehand
     * @return {@code this}
     */
    Segment write(Segment segment);

    /**
     * Insert a new line into the segment.
     *
     * @return {@code this}
     */
    Segment newline();

    /**
     * Define a parameter that can be used in the write methods.
     *
     * @param keyValuePairs the keys and values that can be used in the first parameter of the write method between {{ and }}
     * @return {@code this}
     */
    Segment param(String... keyValuePairs);

    /**
     * Get the value of a segment parameter that was set using the {@link #param(String...)} method.
     *
     * @param key for which the set value is to be returned
     * @return the optional value of the parameter
     */
    Optional<String> getParam(String key);

    /**
     * @return the set of the keys that are defined in this segment for templating.
     */
    Set<String> paramKeySet();

    /**
     * Trace the parameters
     */
    void traceParams();

    /**
     * Trace the lines of the segment.
     */
    void traceLines();

    /**
     * Delete the previously defined parameters.
     */
    void resetParams();

    /**
     * Write a line into the segment after the last line and increase the indenting for the coming lines.
     * Usually you use this method when the line ends with a '{' character.
     *
     * @param s          the content of the line. Nothing is printed if {@code null}.
     * @param parameters parameters that are used as actual values in the {@code s} format string
     * @return {@code this}
     */
    Segment write_r(String s, Object... parameters);

    default Segment _r(String s, Object... parameters) {
        return write_r(s, parameters);
    }

    /**
     * Write a line into the segment after the last line and after decreasing the indenting. Usually you
     * use this method to put the '}' at the end of the code blocks.
     *
     * @param s          the content of the line. Nothing is printed if {@code null}.
     * @param parameters parameters that are used as actual values in the {@code s} format string
     * @return {@code this}
     */
    Segment write_l(String s, Object... parameters);

    default Segment _l(String s, Object... parameters) {
        return write_l(s, parameters);
    }

    /**
     * Usually this method is not implemented separately and is needed for to be auto closeable so that the segment
     * can be used in a try-with-resources block.
     */
    default void close() {
    }
}
