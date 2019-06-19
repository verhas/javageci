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
            if (nextLex != null) {
                lexes.add(nextLex);
            }
        }
        return lexes.toArray(new LexicalElement[0]);
    }

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
        int lenAfter, lenBefore;
        do {
            lenBefore = sb.length();
            for (final var lexEater : lexEaters) {
                final var nlextElement = lexEater.consume(sb);
                if (nlextElement != null) {
                    return nlextElement;
                }
            }
            lenAfter = sb.length();
        } while (lenAfter < lenBefore);
        if (sb.length() > 0) {
            throw new IllegalArgumentException("Cannot analyze Java source code at " + sb.toString());
        }
        return null;
    }

}
