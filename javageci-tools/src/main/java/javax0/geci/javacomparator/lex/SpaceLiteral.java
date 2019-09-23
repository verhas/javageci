package javax0.geci.javacomparator.lex;

/**
 * Space eating lexer. If there are some spaces it removed from the
 * input it will return {@code * LexicalElement.IGNORED}.
 */
public class SpaceLiteral implements LexEater {
    @Override
    public LexicalElement apply(StringBuilder sb) {
        if (sb.length() == 0 || !Character.isWhitespace(sb.charAt(0))) {
            return null;
        }
        final var lexeme = new StringBuilder();
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            lexeme.append(sb.charAt(0));
            sb.deleteCharAt(0);
        }
        return new LexicalElement.Spacing(lexeme.toString());
    }
}
