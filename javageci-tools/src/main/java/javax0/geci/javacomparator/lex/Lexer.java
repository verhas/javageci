package javax0.geci.javacomparator.lex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Lexer implements Function<List<String>, LexicalElement[]> {
    @Override
    public LexicalElement[] apply(List<String> strings) {
        final var code = new StringBuilder(String.join("\n", strings));
        final var lexes = new ArrayList<LexicalElement>();
        while (code.length() > 0) {
            final var nextLex = next(code);
            if (nextLex != null && nextLex != LexicalElement.IGNORED) {
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

    private static LexicalElement next(StringBuilder sb) {
        for (final var lexEater : lexEaters) {
            final var nlextElement = lexEater.apply(sb);
            if (nlextElement != null) {
                return nlextElement;
            }
        }
        if (sb.length() > 0) {
            throw new IllegalArgumentException("Cannot analyze Java source code at " + sb.toString());
        }
        return null;
    }

}
