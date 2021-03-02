package javax0.geci.javacomparator.lex;

import static javax0.geci.javacomparator.lex.Escape.createOutput;
import static javax0.geci.javacomparator.lex.Escape.handleEscape;
import static javax0.geci.javacomparator.lex.Escape.handleNormalCharacter;
import static javax0.geci.javacomparator.lex.Escape.handleNormalMultiLineStringCharacter;

/**
 * String literal eating lexer as per defined in the Java Language
 * Standard JLS13 including the experimental feature of multi-line-string.
 *
 * This implementation will just store the string and does not normalize the
 * spaces, and when compared it does not properly "equals" the same multi and single
 * line strings.
 *
 */
public class StringLiteral implements LexEater {
    public static final String MULTI_LINE_STRING_DELIMITER = "\"\"\"";
    private static final String ENCLOSING = "\"";
    private static final char ENCLOSING_CH = '"';
    private static final String STRING = "String";

    @Override
    public LexicalElement.StringLiteral apply(StringBuilder sb) {
        if (sb.length() == 0 || sb.charAt(0) != ENCLOSING_CH) {
            return null;
        }
        final var output = createOutput(sb, STRING);
        final var original = createOutput(sb, STRING);
        if (sb.length() >= 3 && sb.subSequence(0, 3).equals(MULTI_LINE_STRING_DELIMITER)) {
            return getMultiLineStringLiteral(sb, output,original);
        } else {
            return getSimpleStringLiteral(sb, output,original);
        }
    }

    private LexicalElement.StringLiteral getMultiLineStringLiteral(StringBuilder sb, StringBuilder output, StringBuilder original) {
        deleteMultiLineStringDelimiter(sb);
        while (sb.length() >= 3 && !sb.subSequence(0, 3).equals(MULTI_LINE_STRING_DELIMITER)) {
            final char ch = sb.charAt(0);
            if (ch == '\\') {
                handleEscape(sb, output, original);
            } else {
                handleNormalMultiLineStringCharacter(sb, output, original);
            }
        }
        if (sb.length() < 3) {
            throw new IllegalArgumentException("Multi-line string is not terminated before eof");
        }
        deleteMultiLineStringDelimiter(sb);
        return new LexicalElement.StringLiteral(output.toString(), original.toString(),MULTI_LINE_STRING_DELIMITER);
    }

    private void deleteMultiLineStringDelimiter(StringBuilder sb) {
        sb.deleteCharAt(0);
        sb.deleteCharAt(0);
        sb.deleteCharAt(0);
    }

    private LexicalElement.StringLiteral getSimpleStringLiteral(StringBuilder sb, StringBuilder output, StringBuilder original) {
        sb.deleteCharAt(0);
        while (sb.length() > 0 && sb.charAt(0) != ENCLOSING_CH) {
            final char ch = sb.charAt(0);
            if (ch == '\\') {
                handleEscape(sb, output, original);
            } else {
                handleNormalCharacter(sb, output,original);
            }
        }
        if (sb.length() == 0) {
            throw new IllegalArgumentException("String is not terminated before eol");
        }
        sb.deleteCharAt(0);
        return new LexicalElement.StringLiteral(output.toString(), original.toString(), ENCLOSING);
    }
}