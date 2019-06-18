package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;

import static javax0.geci.javacomparator.lex.Escape.*;

public class StringLiteral {

    private static final String STRING = "String";

    public LexicalElement consume(StringBuilder sb) {
        final StringBuilder output = createOutput(sb,STRING);
        while (sb.length() > 0 && sb.charAt(0) != '"') {
            final char ch = sb.charAt(0);
            if (ch == '\\') {
                handleEscape(sb, output);
            } else {
                handleNormalCharacter(sb, output, ch);
            }
        }
        if( sb.length() == 0 ){
            throw new IllegalArgumentException("String is not terminated before eol");
        }
        sb.deleteCharAt(0);
        return new LexicalElement(output.toString(), LexicalElement.Type.STRING);
    }
}