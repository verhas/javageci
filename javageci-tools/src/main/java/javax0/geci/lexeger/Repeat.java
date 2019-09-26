package javax0.geci.lexeger;

public class Repeat extends LexMatcher {
    private final LexMatcher matcher;
    private final int min;
    private final int max;

    public Repeat(LexExpression factory, JavaLexed javaLexed, LexMatcher matcher, int min, int max) {
        super(factory, javaLexed);
        this.matcher = matcher;
        this.min = min;
        this.max = max;
    }

    @Override
    public MatchResult match(int i) {
        int start = skipSpacesAndComments(i);
        int j = start;
        int counter = 0;
        while (counter < max) {
            j = skipSpacesAndComments(j);
            final var result = matcher.match(j);
            counter++;
            if (!result.matches && counter < min) {
                return MatchResult.NO_MATCH;
            }
            if (result.matches) {
                j = result.end;
            } else {
                return new MatchResult(true, start, j);
            }
        }
        return new MatchResult(true, start, j);
    }
}
