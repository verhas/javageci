package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;

public abstract class LexMatcher {
    protected final JavaLexed javaLexed;

    protected LexMatcher(JavaLexed javaLexed) {
        this.javaLexed = javaLexed;
    }

    private boolean isSpaceChecked = false;
    private boolean isCommentChecked = false;

    public static LexMatcherFactory factory(JavaLexed javaLexed) {
        return new LexMatcherFactory(javaLexed);
    }

    public LexMatcher spaceSensitive() {
        isSpaceChecked = true;
        return this;
    }

    public LexMatcher commentSensitive() {
        isCommentChecked = true;
        return this;
    }

    protected void spaceSensitive(LexMatcher lm) {
        isSpaceChecked = lm.isSpaceChecked;
    }

    public abstract MatchResult match(int i);

    public MatchResult find(int i) {
        int j = i;
        while (i < javaLexed.size()) {
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
        if (!isSpaceChecked || !isCommentChecked) {
            while (j < javaLexed.size() &&
                       ((!isSpaceChecked && javaLexed.get(j).type == LexicalElement.Type.SPACING)
                            || (!isCommentChecked && javaLexed.get(j).type == LexicalElement.Type.COMMENT))
            ) {
                j++;
            }
        }
        return j;
    }

}
