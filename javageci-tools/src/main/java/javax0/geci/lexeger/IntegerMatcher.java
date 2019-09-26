package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.function.Predicate;

public class IntegerMatcher extends LexMatcher {
    private final Predicate<Long> predicate;

    IntegerMatcher(LexExpression factory, JavaLexed javaLexed, Predicate<Long> predicate) {
        super(factory, javaLexed);
        this.predicate = predicate;
    }

    IntegerMatcher(LexExpression factory, JavaLexed javaLexed) {
        this(factory, javaLexed, null);
    }

    public MatchResult match(int i) {
        int start = skipSpacesAndComments(i);
        if (javaLexed.get(start).type != LexicalElement.Type.INTEGER) {
            return MatchResult.NO_MATCH;
        }
        if (predicate != null) {
            if (predicate.test(((LexicalElement.IntegerLiteral) javaLexed.get(start)).value)) {
                return new MatchResult(true, start, start + 1);
            } else {
                return MatchResult.NO_MATCH;
            }
        } else {
            return new MatchResult(true, start, start + 1);
        }
    }
}
