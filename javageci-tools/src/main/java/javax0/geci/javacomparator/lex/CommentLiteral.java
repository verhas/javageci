package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;

/**
 * Comment eating lexer. In case it could recognize a comment and could
 * remove it from the start of the input the it will return a lexical
 * element containing the comment.
 */
public class CommentLiteral implements LexEater {

    @Override
    public LexicalElement apply(StringBuilder sb) {
        if (sb.length() < 2 || sb.charAt(0) != '/' || (sb.charAt(1) != '/' && sb.charAt(1) != '*')) {
            return null;
        }
        if (sb.charAt(1) == '/') {
            return singleLineComment(sb);
        }
        return multiLineComment(sb);
    }

    private LexicalElement multiLineComment(final StringBuilder sb) {
        final var output = new StringBuilder("/*");
        sb.delete(0, 2);
        while (sb.length() >= 2 && (sb.charAt(0) != '*' || sb.charAt(1) != '/')) {
            output.append(sb.charAt(0));
            sb.deleteCharAt(0);
        }
        if (sb.length() >= 2) {
            sb.delete(0, 2);
            output.append("*/");
            return new LexicalElement.Comment(output.toString());
        }
        throw new GeciException("Comment is not terminated till end of file");
    }

    private LexicalElement singleLineComment(final StringBuilder sb) {
        final var output = new StringBuilder("//");
        sb.delete(0, 2);
        while (sb.length() > 0 && sb.charAt(0) != '\n' && sb.charAt(0) != '\r') {
            output.append(sb.charAt(0));
            sb.deleteCharAt(0);
        }
        return new LexicalElement.Comment(output.toString());
    }
}
