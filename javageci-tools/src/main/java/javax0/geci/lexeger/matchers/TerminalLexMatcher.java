package javax0.geci.lexeger.matchers;

import javax0.geci.javacomparator.lex.LexicalElement;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

public class TerminalLexMatcher extends LexMatcher {
    private final LexicalElement le;

    public TerminalLexMatcher(Lexpression lexpression, JavaLexed javaLexed, LexicalElement le) {
        super(lexpression, javaLexed);
        this.le = le;
    }

    @Override
    public MatchResult matchesAt(int i) {
        if( consumed()){
            return MatchResult.NO_MATCH;
        }
        int j = skipSpacesAndComments(i);
        if (j < javaLexed.size()) {
            final var matches = super.javaLexed.get(j).equals(le);
            if (matches) {
                return matching( j, j + 1);
            }
        }
        return MatchResult.NO_MATCH;
    }
}
