package javax0.geci.lexeger.matchers;

import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

public class Repeat extends LexMatcher {
    private final LexMatcher matcher;
    private final int min;
    private final int max;
    private int currentMax;

    public Repeat(Lexpression factory, JavaLexed javaLexed, LexMatcher matcher, int min, int max) {
        super(factory, javaLexed);
        this.matcher = matcher;
        this.min = min;
        this.max = max;
        currentMax = max;
    }

    @Override
    public void reset() {
        super.reset();
        currentMax = max;
    }

    @Override
    public MatchResult matchesAt(int i) {
        if( currentMax == 0 && consumed()){
            return MatchResult.NO_MATCH;
        }
        int start = skipSpacesAndComments(i);
        int j = start;
        int counter = 0;
        while (counter < currentMax) {
            j = skipSpacesAndComments(j);
            matcher.reset();
            final var result = matcher.matchesAt(j);
            if (!result.matches && counter < min) {
                return MatchResult.NO_MATCH;
            }
            if (result.matches) {
                counter++;
                j = result.end;
            } else {
                currentMax = counter - 1;
                return matching( start, j);
            }
        }
        currentMax = Math.max(counter - 1, 0);
        if (counter < min) {
            return MatchResult.NO_MATCH;
        } else {
            return matching( start, j);
        }
    }
}
