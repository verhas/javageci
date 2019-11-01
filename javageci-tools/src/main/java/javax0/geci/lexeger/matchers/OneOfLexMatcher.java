package javax0.geci.lexeger.matchers;

import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

public class OneOfLexMatcher extends LexMatcher {
    private final LexMatcher[] matchers;

    public OneOfLexMatcher(Lexpression factory, JavaLexed javaLexed, LexMatcher[] matchers) {
        super(factory,javaLexed);
        this.matchers = matchers;
    }

    private int next = 0;

    @Override
    void reset() {
        super.reset();
        next = 0;
    }

    @Override
    boolean isConsumed() {
        return next >= matchers.length;
    }

    @Override
    boolean consumed() {
        return isConsumed();
    }

    @Override
    public MatchResult matchesAt(int i) {
        int j = skipSpacesAndComments(i);
        for (; next < matchers.length ; next++ ) {
            LexMatcher matcher = matchers[next];
            matcher.reset();
            final var result = matcher.matchesAt(j);
            if (result.matches) {
                next++;
                return result;
            }
        }
        return MatchResult.NO_MATCH;
    }
}
