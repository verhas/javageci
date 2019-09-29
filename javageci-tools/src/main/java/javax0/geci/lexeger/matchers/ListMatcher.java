package javax0.geci.lexeger.matchers;

import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

public class ListMatcher extends LexMatcher {
    private final LexMatcher[] matchers;

    public ListMatcher(Lexpression factory, JavaLexed javaLexed, LexMatcher[] matchers) {
        super(factory,javaLexed);
        this.matchers = matchers;
    }

    @Override
    public MatchResult matchesAt(int i) {
        if (consumed()) {
            return MatchResult.NO_MATCH;
        }
        int start = skipSpacesAndComments(i);
        int j;
        int index = -1;
        var result = matching( start, start);
        int[] lastStart = new int[matchers.length];
        while (true) {
            if (result.matches) {
                if (index >= 0) {
                    lastStart[index] = result.start;
                }
                index++;
                if (index >= matchers.length) {
                    return matching( start, result.end);
                }
                matchers[index].reset();
                j = result.end;
            } else {
                if (index > 0) {
                    index--;
                    j = lastStart[index];
                } else {
                    return MatchResult.NO_MATCH;
                }
            }
            result = matchers[index].matchesAt(j);
        }
    }
}
