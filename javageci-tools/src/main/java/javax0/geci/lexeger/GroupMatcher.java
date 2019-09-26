package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.ArrayList;

public class GroupMatcher extends LexMatcher {
    private final LexMatcher matcher;
    private final String name;

    public GroupMatcher(LexExpression expr, JavaLexed javaLexed, String name, LexMatcher matcher) {
        super(expr, javaLexed);
        this.matcher = matcher;
        this.name = name;
    }


    @Override
    public MatchResult match(final int i) {
        final var result = matcher.match(i);
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
