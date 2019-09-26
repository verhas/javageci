package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.function.Predicate;

public class NumberMatcher extends LexMatcher {
    private final Predicate<Number> predicate;

    NumberMatcher(LexExpression factory, JavaLexed javaLexed, Predicate<Number> predicate) {
        super(factory, javaLexed);
        this.predicate = predicate;
    }

    NumberMatcher(LexExpression factory, JavaLexed javaLexed) {
        this(factory, javaLexed, null);
    }

    public MatchResult match(int i) {
        int start = skipSpacesAndComments(i);
        if (javaLexed.get(start).type != LexicalElement.Type.INTEGER && javaLexed.get(start).type != LexicalElement.Type.FLOAT) {
            return MatchResult.NO_MATCH;
        }
        if (predicate != null) {
            if (javaLexed.get(start).type != LexicalElement.Type.INTEGER) {
                if (predicate.test(((LexicalElement.IntegerLiteral) javaLexed.get(start)).value)) {
                    return new MatchResult(true, start, start + 1);
                } else {
                    return MatchResult.NO_MATCH;
                }
            } else {
                if (predicate.test(((LexicalElement.FloatLiteral) javaLexed.get(start)).value)) {
                    return new MatchResult(true, start, start + 1);
                } else {
                    return MatchResult.NO_MATCH;
                }
            }
        } else {
            return new MatchResult(true, start, start + 1);
        }
    }
}
