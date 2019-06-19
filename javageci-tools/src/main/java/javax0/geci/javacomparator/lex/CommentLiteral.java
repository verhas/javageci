package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;

public class CommentLiteral implements LexEater {

    @Override
    public LexicalElement consume(StringBuilder sb) {
        if (sb.length() < 2 || sb.charAt(0) != '/' || (sb.charAt(1) != '/' && sb.charAt(1) != '*')) {
            return null;
        }
        if (sb.charAt(1) == '/') {
            sb.delete(0, 2);
            while (sb.length() > 0 && sb.charAt(0) != '\n' && sb.charAt(0) != '\r') {
                sb.deleteCharAt(0);
            }
            return null;
        }
        sb.delete(0, 2);
        while (sb.length() >= 2 && (sb.charAt(0) != '*' || sb.charAt(1) != '/')) {
            sb.deleteCharAt(0);
        }
        if (sb.length() >= 2) {
            sb.delete(0, 2);
            return null;
        }
        throw new GeciException("Comment is not terminated till end of file");
    }
}
