package javax0.geci.javacomparator.lex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Lexer implements Function<List<String>, LexicalElement[]> {

    private boolean spaceSensitive = false;
    private boolean commentSensitive = false;

    public boolean isSpaceSensitive() {
        return spaceSensitive;
    }

    public boolean isCommentSensitive() {
        return commentSensitive;
    }

    /**
     * Set the lexer to be space sensitive. In case the lexer is space
     * sensitive then the returned array contains SPACING elements as
     * well. Otherwise it ignores the spacing.
     *
     * @return {@code this}
     */
    public Lexer spaceSensitive() {
        spaceSensitive = true;
        return this;
    }

    /**
     * Set the lexer to be comment sensitive. In case the lexer is
     * comment sensitive then the returned array will contain the
     * COMMENTS elements as well as the other elements. Otherwise it
     * ignores the comments.
     *
     * @return {@code this}
     */
    public Lexer commentSensitive() {
        commentSensitive = true;
        return this;
    }

    @Override
    public LexicalElement[] apply(List<String> strings) {
        final var code = new StringBuilder(String.join("\n", strings));
        final var lexes = new ArrayList<LexicalElement>();
        while (code.length() > 0) {
            final var nextLex = next(code);
            if (nextLex != null) {
                lexes.add(nextLex);
            }
        }
        return lexes.toArray(new LexicalElement[0]);
    }

    /**
     * The array of the {@link LexEater} objects that are used to
     * perform lexical analysis. Since these lex eaters look at the
     * start of the string and do not kow anything about each other thus
     * the {@link SymbolLiteral} should be the last one in the array. If
     * it was earlier, for example before {@link CommentLiteral} then it
     * would recognize the '{@code /}' character at the start of a
     * comment, it would consume that and later the comment would not be
     * recognized.
     */
    private static final LexEater[] lexEaters = {
        new SpaceLiteral(),
        new CharacterLiteral(),
        new StringLiteral(),
        new CommentLiteral(),
        new IdentifierLiteral(),
        new NumberLiteral(),
        new SymbolLiteral(),
    };

    private LexicalElement next(StringBuilder sb) {
        boolean repeat = true;
        while (repeat) {
            repeat = false;
            for (final var lexEater : lexEaters) {
                final var nlextElement = lexEater.apply(sb);
                if (nlextElement != null) {
                    if (!ignore(nlextElement)) {
                        return nlextElement;
                    }
                    repeat = true;
                    break;
                }
            }
        }
        if (sb.length() > 0) {
            throw new IllegalArgumentException("Cannot analyze Java source code at " + sb.toString());
        }
        return null;

    }

    private boolean ignore(LexicalElement nlextElement) {
        return (!spaceSensitive && nlextElement.getType() == javax0.geci.javacomparator.LexicalElement.Type.SPACING) ||
            (!commentSensitive && nlextElement.getType() == javax0.geci.javacomparator.LexicalElement.Type.COMMENT);
    }

}
