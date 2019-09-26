package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.List;

public abstract class LexMatcher {
    protected final JavaLexed javaLexed;
    private final LexExpression expression;

    protected LexMatcher(LexExpression expression, JavaLexed javaLexed) {
        this.javaLexed = javaLexed;
        this.expression = expression;
    }

    void store(String name, List<LexicalElement> elements) {
        if (name != null) {
            expression.store(name, elements);
        }
    }

    protected void store(String name, java.util.regex.MatchResult patternMatchResult) {
        if (name != null) {
            expression.store(name, patternMatchResult);
        }
    }


    public static LexExpression of(JavaLexed javaLexed, int sensitivity) {
        final var lexer = new Lexer();
        if ((sensitivity & LexExpression.SPACE_SENSITIVE) > 0) {
            lexer.spaceSensitive();
        }
        if ((sensitivity & LexExpression.COMMENT_SENSITIVE) > 0) {
            lexer.commentSensitive();
        }
        return new LexExpression(javaLexed, lexer);
    }

    public static LexExpression of(JavaLexed javaLexed) {
        return of(javaLexed, LexExpression.NO_SENSITIVITY);
    }

    public abstract MatchResult match(int i);

    public MatchResult find(int i) {
        int j = i;
        while (j < javaLexed.size()) {
            expression.clean();
            final var result = match(j);
            if (result.matches) {
                return result;
            }
            j++;
        }
        return MatchResult.NO_MATCH;
    }

    public MatchResult find() {
        return find(0);
    }

    protected int skipSpacesAndComments(int i) {
        int j = i;
        if (!expression.isSpaceSensitive() || !expression.isCommentSensitive()) {
            while (j < javaLexed.size() &&
                ((!expression.isSpaceSensitive() && javaLexed.get(j).type == LexicalElement.Type.SPACING)
                    || (!expression.isCommentSensitive() && javaLexed.get(j).type == LexicalElement.Type.COMMENT))
            ) {
                j++;
            }
        }
        return j;
    }

}
