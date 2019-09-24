package javax0.geci.javacomparator.lex;

import static javax0.geci.javacomparator.lex.Escape.createOutput;
import static javax0.geci.javacomparator.lex.Escape.handleEscape;
import static javax0.geci.javacomparator.lex.Escape.handleNormalCharacter;

/**
 * String literal eating lexer as per defined in the Java Language
 * Standard.
 *
 * <p>The constructor can also define the border character. In case this
 * is {@code '} then the lexer can be used to fetch a non-Java string
 * which is delimited with apostrophe.
 */
public class StringLiteral implements LexEater {
    private final char enclosing;
    private static final String STRING = "String";

    public StringLiteral(char enclosing) {
        this.enclosing = enclosing;
    }

    public StringLiteral() {
        this.enclosing = '"';
    }

    @Override
    public LexicalElement.StringLiteral apply(StringBuilder sb) {
        if (sb.length() == 0 || sb.charAt(0) != enclosing) {
            return null;
        }
        final var output = createOutput(sb, STRING);
        sb.deleteCharAt(0);
        while (sb.length() > 0 && sb.charAt(0) != enclosing) {
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
        return new LexicalElement.StringLiteral(output.toString(),enclosing);
    }
}