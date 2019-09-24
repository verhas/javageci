package javax0.geci.lexeger;

public class ListMatcher extends LexMatcher {
    private final LexMatcher[] matchers;

    public ListMatcher(JavaLexed javaLexed, LexMatcher[] matchers) {
        super(javaLexed);
        this.matchers = matchers;
    }

    @Override
    public MatchResult match(int i) {
        int start = skipSpacesAndComments(i);
        int j = start;
        for (LexMatcher matcher : matchers) {
            j = skipSpacesAndComments(j);
            matcher.spaceSensitive(this);
            final var result = matcher.match(j++);
            if (!result.matches) {
                return MatchResult.NO_MATCH;
            }
        }
        return new MatchResult(true, start, j);
    }
}
