package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.function.Predicate;

public class FloatMatcher extends LexMatcher {
    private final Predicate<Double> predicate;

    FloatMatcher(LexExpression factory, JavaLexed javaLexed, Predicate<Double> predicate) {
        super(factory, javaLexed);
        this.predicate = predicate;
    }

    FloatMatcher(LexExpression factory, JavaLexed javaLexed) {
        this(factory, javaLexed, null);
    }

    public MatchResult match(int i) {
        int start = skipSpacesAndComments(i);
        if (javaLexed.get(start).type != LexicalElement.Type.INTEGER) {
            return MatchResult.NO_MATCH;
        }
        if (predicate != null) {
            if (predicate.test(((LexicalElement.FloatLiteral) javaLexed.get(start)).value)) {
                return new MatchResult(true, start, start + 1);
            } else {
                return MatchResult.NO_MATCH;
            }
        } else {
            return new MatchResult(true, start, start + 1);
        }
    }
}
