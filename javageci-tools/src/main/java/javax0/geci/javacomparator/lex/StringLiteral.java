package javax0.geci.javacomparator.lex;

import static javax0.geci.javacomparator.lex.Escape.*;

public class StringLiteral implements LexEater {

    private static final String STRING = "String";

    @Override
    public LexicalElement.STring consume(StringBuilder sb) {
        if (sb.charAt(0) != '\"') {
            return null;
        }
        final StringBuilder output = createOutput(sb, STRING);
        sb.deleteCharAt(0);
        while (sb.length() > 0 && sb.charAt(0) != '"') {
            final char ch = sb.charAt(0);
            if (ch == '\\') {
                handleEscape(sb, output);
            } else {
                handleNormalCharacter(sb, output, ch);
            }
        }
        if (sb.length() == 0) {
            throw new IllegalArgumentException("String is not terminated before eol");
        }
        sb.deleteCharAt(0);
        return new LexicalElement.STring(output.toString());
    }
}