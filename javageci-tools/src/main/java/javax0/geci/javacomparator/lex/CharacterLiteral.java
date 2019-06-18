package javax0.geci.javacomparator.lex;

import static javax0.geci.javacomparator.lex.Escape.*;

public class CharacterLiteral {
    private static final String CHARACTER = "Character";

    public LexicalElement consume(StringBuilder sb) {
        final StringBuilder output = createOutput(sb, CHARACTER);
        while (sb.length() > 0 && sb.charAt(0) != '\'') {
            final char ch = sb.charAt(0);
            if (ch == '\\') {
                handleEscape(sb, output);
            } else {
                handleNormalCharacter(sb, output, ch);
            }
            if (output.length() > 1) {
                throw new IllegalArgumentException("Character is too long");
            }
        }
        if (sb.length() == 0) {
            throw new IllegalArgumentException(CHARACTER + " is not terminated before eol");
        }
        sb.deleteCharAt(0);
        return new LexicalElement(output.toString(), LexicalElement.Type.CHARACTER);
    }

}