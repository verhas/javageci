package javax0.geci.tools;

import java.util.Arrays;

/**
 * A simple tool to write code into a string.
 */
public class JavaSourceBuilder implements AutoCloseable {
    private static final int TAB = 4;
    private final StringBuilder code = new StringBuilder();
    private int tabStop = 0;

    /**
     * Write a string with format placeholders into the underlying string. If the string is multi line (contains \n)
     * then each line will be aligned to the actual tab stop.
     * <p>
     * If the line is terminated with one or more \n characters then they will be removed and the line will be
     * terminated with a single new line character.
     *
     * @param s          the format string
     * @param parameters optional parameters
     */
    public JavaSourceBuilder write(String s, Object... parameters) {
        if (s.trim().length() == 0) {
            newline();
        } else {
            var formatted = String.format(s, parameters);
            if (formatted.contains("\n")) {
                Arrays.stream(formatted.split("\n")).forEach(this::write);
            } else {
                formatted = formatted.replaceAll("\n+$", "");
                code.append(" ".repeat(tabStop) + formatted + "\n");
            }
        }
        return this;
    }

    /**
     * Add a new line to the
     */
    public JavaSourceBuilder newline() {
        code.append("\n");
        return this;

    }

    /**
     * Write the string to the code and then increase the tab stop.
     *
     * @param s
     * @param parameters
     */
    public JavaSourceBuilder write_r(String s, Object... parameters) {
        write(s, parameters);
        tabStop += TAB;
        return this;
    }

    /**
     * Decrease the tab stop and then write the string to the code.
     *
     * @param s
     * @param parameters
     */
    public JavaSourceBuilder write_l(String s, Object... parameters) {
        tabStop -= TAB;
        if (tabStop < 0) {
            tabStop = 0;
        }
        write(s, parameters);
        return this;
    }

    /**
     * Write a line that is pulled left but then again the next to the right. Typically
     * this is {@code else} lines.
     *
     * @param s          the line to be added to the code
     * @param parameters the format parameters
     * @return this
     */
    public JavaSourceBuilder write_lr(String s, Object... parameters) {
        tabStop -= TAB;
        if (tabStop < 0) {
            tabStop = 0;
        }
        write_r(s, parameters);
        return this;
    }

    @Override
    public String toString() {
        return code.toString();
    }

    /**
     * You can use this method in the try-with-resources output
     *
     * @param s          the line that opens the block without the {@code { } at the end of the line. That will automatically
     *                   be appended.
     * @param parameters parameters of the line
     * @return this
     */
    public JavaSourceBuilder open(String s, Object... parameters) {
        s += "{";
        write_r(s, parameters);
        return this;
    }

    public JavaSourceBuilder comment(String s, Object... parameters) {
        s = "// " + s;
        write(s);
        return this;
    }

    public JavaSourceBuilder statement(String s, Object... parameters) {
        s += ";";
        write(s);
        return this;
    }

    public JavaSourceBuilder whileStatement(String s, Object... parameters) {
        s = "while( " + s + " ){";
        write_r(s);
        return this;
    }

    public JavaSourceBuilder forStatement(String s, Object... parameters) {
        s = "for( " + s + " ){";
        write_r(s);
        return this;
    }

    public JavaSourceBuilder ifStatement(String s, Object... parameters) {
        s = "if( " + s + " ){";
        write_r(s);
        return this;
    }

    public JavaSourceBuilder elseStatement() {
        write_lr("}else{");
        return this;
    }

    @Override
    public void close() {
        write_l("}");
    }
}
