package javax0.geci.lexeger.matchers;

import javax0.geci.javacomparator.LexicalElement;
import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

import java.util.List;

public abstract class LexMatcher implements javax0.geci.lexeger.LexMatcher {
    final JavaLexed javaLexed;
    private final Lexpression expression;

    private boolean isReset = true;

    MatchResult matching(int start, int end) {
        return new MatchResult(true, start, end);
    }

    public static Lexpression when(JavaLexed javaLexed, int sensitivity) {
        final var lexer = new Lexer();
        if ((sensitivity & Lexpression.SPACE_SENSITIVE) > 0) {
            lexer.spaceSensitive();
        }
        if ((sensitivity & Lexpression.COMMENT_SENSITIVE) > 0) {
            lexer.commentSensitive();
        }
        return new Lexpression(javaLexed, lexer);
    }

    void reset() {
        isReset = true;
    }

    boolean consumed() {
        final var isConsumed = !isReset;
        isReset = false;
        return isConsumed;
    }

    boolean isConsumed() {
        return !isReset;
    }

    LexMatcher(Lexpression expression, JavaLexed javaLexed) {
        this.javaLexed = javaLexed;
        this.expression = expression;
    }

    void remove(String name) {
        if (name != null) {
            expression.remove(name);
        }
    }

    void store(String name, List<LexicalElement> elements) {
        if (name != null) {
            expression.store(name, elements);
        }
    }

    void store(String name, java.util.regex.MatchResult patternMatchResult) {
        if (name != null) {
            expression.store(name, patternMatchResult);
        }
    }

    public abstract MatchResult matchesAt(int i);

    public MatchResult find(int i) {
        int j = i;
        while (j < javaLexed.size()) {
            expression.clean();
            reset();
            final var result = matchesAt(j);
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

    int skipSpacesAndComments(int i) {
        int j = i;
        if (!expression.isSpaceSensitive() || !expression.isCommentSensitive()) {
            while (j < javaLexed.size() &&
                ((!expression.isSpaceSensitive() && javaLexed.get(j).getType() == javax0.geci.javacomparator.LexicalElement.Type.SPACING)
                    || (!expression.isCommentSensitive() && javaLexed.get(j).getType() == javax0.geci.javacomparator.LexicalElement.Type.COMMENT))
            ) {
                j++;
            }
        }
        return j;
    }

}
