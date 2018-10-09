package javax0.geci.api;

/**
 * A {@code Segment} object represents an editor-fold part in the source file that the code generator can
 * write. A {@code Segment} is retrieved by the code generator calling the {@link Source#open(String)} method
 * of the source and it can write into the segment.
 *
 */
public interface Segment extends AutoCloseable {

    /**
     * Write a line to the segment after the last line.
     * @param s the content of the line
     */
    void write(String s);

    /**
     * Insert a new line into the segment.
     */
    void newline();

    /**
     * Write a line into the segment after the last line and increase the indenting for the coming lines.
     * Usually you use this method when the line ends with a '{' character.
     * @param s the content of the line
     */
    void write_r(String s);

    /**
     * Write a line into the segment after the last line and after decreasing the indenting. Usually you
     * use this method to put the '}' at the end of the code blocks.
     * @param s the content of the line
     */
    void write_l(String s);

    /**
     * Usually is not implemented separately and is needed for to be auto cloaseable so that the segment
     * can be used in a try-with-resources block.
     *
     */
    default void close() {
    }
}
