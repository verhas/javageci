package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;

public class TerminalLexMatcher extends LexMatcher {
    private final LexicalElement le;

    public TerminalLexMatcher(LexExpression factory, JavaLexed javaLexed, LexicalElement le) {
        super(factory, javaLexed);
        this.le = le;
    }

    @Override
    public MatchResult match(int i) {
        int j = skipSpacesAndComments(i);
        if (j < javaLexed.size()) {
            final var matches = super.javaLexed.get(j).equals(le);
            if (matches) {
                return new MatchResult(true, j, j + 1);
            }
        }
        return MatchResult.NO_MATCH;
    }
}
