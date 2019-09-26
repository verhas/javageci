package javax0.geci.lexeger;

public class OneOfLexMatcher extends LexMatcher {
    private final LexMatcher[] matchers;

    public OneOfLexMatcher(LexExpression factory, JavaLexed javaLexed, LexMatcher[] matchers) {
        super(factory,javaLexed);
        this.matchers = matchers;
    }

    @Override
    public MatchResult match(int i) {
        int j = skipSpacesAndComments(i);
        for (LexMatcher matcher : matchers) {
            final var result = matcher.match(j);
            if (result.matches) {
                return result;
            }
        }
        return MatchResult.NO_MATCH;
    }
}
