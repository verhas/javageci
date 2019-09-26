package javax0.geci.lexeger;

import java.util.HashSet;

public class SetMatcher extends LexMatcher {
    private final LexMatcher[] matchers;

    public SetMatcher(LexExpression factory, JavaLexed javaLexed, LexMatcher[] matchers) {
        super(factory, javaLexed);
        this.matchers = matchers;
    }

    @Override
    public MatchResult match(int i) {
        final var wasMatched = new HashSet<LexMatcher>();
        int start = skipSpacesAndComments(i);
        int j = start;
        while (wasMatched.size() < matchers.length) {
            final var matchedSetSize = wasMatched.size();
            for (LexMatcher matcher : matchers) {
                if (wasMatched.contains(matcher)) {
                    continue;
                }
                j = skipSpacesAndComments(j);
                if (j >= javaLexed.size()) {
                    return MatchResult.NO_MATCH;
                }
                final var result = matcher.match(j);
                if (result.matches) {
                    wasMatched.add(matcher);
                    j = result.end;
                    break;
                }
            }
            if (matchedSetSize == wasMatched.size()) {
                return MatchResult.NO_MATCH;
            }
        }
        return new MatchResult(true, start, j);
    }
}
