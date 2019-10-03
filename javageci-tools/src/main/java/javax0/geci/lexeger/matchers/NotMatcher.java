package javax0.geci.lexeger.matchers;

import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

public class NotMatcher extends LexMatcher {
    private final LexMatcher matcher;

    public NotMatcher(Lexpression expr, JavaLexed javaLexed, LexMatcher matcher) {
        super(expr, javaLexed);
        this.matcher = matcher;
    }

    @Override
    public MatchResult matchesAt(final int i) {
        if (consumed()) {
            return MatchResult.NO_MATCH;
        }
        matcher.reset();
        final var result = matcher.matchesAt(i);
        if (!result.matches) {
            return matching(i, i + 1);
        } else {
            return MatchResult.NO_MATCH;
        }
    }
}
