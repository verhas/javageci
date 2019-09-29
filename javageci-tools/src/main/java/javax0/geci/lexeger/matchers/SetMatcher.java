package javax0.geci.lexeger.matchers;

import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

import java.util.HashSet;

public class SetMatcher extends LexMatcher {
    private final LexMatcher[] matchers;

    public SetMatcher(Lexpression factory, JavaLexed javaLexed, LexMatcher[] matchers) {
        super(factory, javaLexed);
        this.matchers = matchers;
    }

    @Override
    public MatchResult matchesAt(int i) {
        if( consumed() ){
            return MatchResult.NO_MATCH;
        }
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
                matcher.reset();
                final var result = matcher.matchesAt(j);
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
        return matching( start, j);
    }
}
