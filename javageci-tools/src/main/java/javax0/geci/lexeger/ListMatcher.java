package javax0.geci.lexeger;

public class ListMatcher extends LexMatcher {
    private final LexMatcher[] matchers;

    public ListMatcher(LexExpression factory, JavaLexed javaLexed, LexMatcher[] matchers) {
        super(factory,javaLexed);
        this.matchers = matchers;
    }

    @Override
    public MatchResult match(int i) {
        int start = skipSpacesAndComments(i);
        int j = start;
        for (LexMatcher matcher : matchers) {
            j = skipSpacesAndComments(j);
            if( j >= javaLexed.size() ){
                return MatchResult.NO_MATCH;
            }
            final var result = matcher.match(j);
            if (!result.matches) {
                return MatchResult.NO_MATCH;
            }
            j = result.end;
        }
        return new MatchResult(true, start, j);
    }
}
