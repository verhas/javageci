package javax0.geci.lexeger.matchers;

import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

public class NotMatcher extends LexMatcher {
    private final LexMatcher[] matchers;

    public NotMatcher(Lexpression expr, JavaLexed javaLexed, LexMatcher... matchers) {
        super(expr, javaLexed);
        this.matchers = matchers;
    }

    @Override
    public MatchResult matchesAt(final int i) {
        if (consumed()) {
            return MatchResult.NO_MATCH;
        }
        for (final var matcher : matchers) {
            matcher.reset();
            final var result = matcher.matchesAt(i);
            if (!result.matches) {
                return matching(i, i + 1);
            }
        }
        return MatchResult.NO_MATCH;
    }
}
