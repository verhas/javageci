package javax0.geci.javacomparator.lex;

import static javax0.geci.javacomparator.lex.Escape.createOutput;
import static javax0.geci.javacomparator.lex.Escape.handleEscape;
import static javax0.geci.javacomparator.lex.Escape.handleNormalCharacter;

/**
 * A character literal eating lexer as defined in the Java Language
 * Standard.
 */
public class CharacterLiteral implements LexEater {
    private static final String CHARACTER = "Character";
    private static final char ENCLOSING = '\'';

    @Override
    public LexicalElement.CharacterLiteral apply(StringBuilder sb) {
        if (sb.length() == 0 || sb.charAt(0) != ENCLOSING) {
            return null;
        }
        final StringBuilder output = createOutput(sb, CHARACTER);
        final StringBuilder original = createOutput(sb, CHARACTER);
        sb.deleteCharAt(0);
        while (sb.length() > 0 && sb.charAt(0) != ENCLOSING) {
            final char ch = sb.charAt(0);
            if (ch == '\\') {
                handleEscape(sb, output,original);
            } else {
                handleNormalCharacter(sb, output,original);
            }
        }
        if (sb.length() == 0) {
            throw new IllegalArgumentException("Character is not terminated before eol");
        }
        sb.deleteCharAt(0);
        return new LexicalElement.CharacterLiteral(output.toString(),original.toString());
    }

}