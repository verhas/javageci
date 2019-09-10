package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;

/**
 * Comment eating lexer. In case it could recognize a comment and could
 * remove it from the start of the input the it will return a lexical
 * element containing the core of the comment. Note that a single line
 * comment and a "multi line" comment that is actually a single line and
 * has the same characters will be the same. The comment delimiters are
 * not store.
 */
public class CommentLiteral implements LexEater {

    @Override
    public LexicalElement apply(StringBuilder sb) {
        if (sb.length() < 2 || sb.charAt(0) != '/' || (sb.charAt(1) != '/' && sb.charAt(1) != '*')) {
            return null;
        }
        final var output = new StringBuilder();
        if (sb.charAt(1) == '/') {
            sb.delete(0, 2);
            while (sb.length() > 0 && sb.charAt(0) != '\n' && sb.charAt(0) != '\r') {
                output.append(sb.charAt(0));
                sb.deleteCharAt(0);
            }
            return new LexicalElement(output.toString(), LexicalElement.Type.COMMENT);
        }
        sb.delete(0, 2);
        while (sb.length() >= 2 && (sb.charAt(0) != '*' || sb.charAt(1) != '/')) {
            output.append(sb.charAt(0));
            sb.deleteCharAt(0);
        }
        if (sb.length() >= 2) {
            sb.delete(0, 2);
            return new LexicalElement(output.toString(), LexicalElement.Type.COMMENT);
        }
        throw new GeciException("Comment is not terminated till end of file");
    }
}
