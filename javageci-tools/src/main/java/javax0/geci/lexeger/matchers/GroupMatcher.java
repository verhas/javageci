package javax0.geci.lexeger.matchers;

import javax0.geci.javacomparator.LexicalElement;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

import java.util.ArrayList;

public class GroupMatcher extends LexMatcher {
    private final LexMatcher matcher;
    private final String name;

    public GroupMatcher(Lexpression expr, JavaLexed javaLexed, String name, LexMatcher matcher) {
        super(expr, javaLexed);
        this.matcher = matcher;
        this.name = name;
    }

    @Override
    void reset() {
        super.reset();
        matcher.reset();
    }

    @Override
    public MatchResult matchesAt(final int i) {
        if( matcher.isConsumed()){
            remove(name);
            return MatchResult.NO_MATCH;
        }
        final var result = matcher.matchesAt(i);
        if (result.matches) {
            final var elements = new ArrayList<LexicalElement>(result.end-result.start);
            for( int j = result.start ; j < result.end ; j++ ){
                elements.add(javaLexed.get(j));
            }
            store(name,elements);
        }
        return result;
    }
}
