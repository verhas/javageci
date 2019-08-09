package javax0.geci.javacomparator.lex;

import static javax0.geci.javacomparator.lex.Escape.*;

/**
 * A character literal eating lexer as defined in the Java Language
 * Standard.
 */
public class CharacterLiteral implements LexEater{
    private static final String CHARACTER = "Character";

    @Override
    public LexicalElement.CHaracter apply(StringBuilder sb) {
        if( sb.length() == 0 || sb.charAt(0) != '\'' ){
            return null;
        }
        final StringBuilder output = createOutput(sb, CHARACTER);
        sb.deleteCharAt(0);
        while (sb.length() > 0 && sb.charAt(0) != '\'') {
            final char ch = sb.charAt(0);
            if (ch == '\\') {
                handleEscape(sb, output);
            } else {
                handleNormalCharacter(sb, output, ch);
            }
        }
        if (sb.length() == 0) {
            throw new IllegalArgumentException(CHARACTER + " is not terminated before eol");
        }
        sb.deleteCharAt(0);
        return new LexicalElement.CHaracter(output.toString());
    }

}