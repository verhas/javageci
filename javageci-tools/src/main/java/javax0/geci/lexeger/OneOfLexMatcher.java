package javax0.geci.lexeger;

public class OneOfLexMatcher extends LexMatcher {
    private final LexMatcher[] matchers;

    public OneOfLexMatcher(JavaLexed javaLexed, LexMatcher[] matchers) {
        super(javaLexed);
        this.matchers = matchers;
    }

    @Override
    public MatchResult match(int i) {
        int j = skipSpacesAndComments(i);
        for (LexMatcher matcher : matchers) {
            matcher.spaceSensitive(this);
            final var result = matcher.match(j);
            if (result.matches) {
                return result;
            }
        }
        return MatchResult.NO_MATCH;
    }
}
