package javax0.geci.lexeger.matchers;

import javax0.geci.javacomparator.lex.LexicalElement;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

import java.util.List;

public abstract class LexMatcher implements javax0.geci.lexeger.LexMatcher {
    protected final JavaLexed javaLexed;
    private final Lexpression expression;

    private boolean isReset = true;

    protected MatchResult matching(int start, int end) {
        return new MatchResult(true, start, end);
    }

    protected void reset() {
        isReset = true;
    }

    protected boolean consumed() {
        final var isConsumed = !isReset;
        isReset = false;
        return isConsumed;
    }

    protected LexMatcher(Lexpression expression, JavaLexed javaLexed) {
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

    public abstract MatchResult match(int i);

    public MatchResult find(int i) {
        int j = i;
        while (j < javaLexed.size()) {
            expression.clean();
            reset();
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
