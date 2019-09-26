package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.regex.Pattern;

public class CommentMatcher extends AbstractPatternedMatcher {

    public CommentMatcher(LexExpression factory, JavaLexed javaLexed, Pattern pattern, String name) {
        super(factory, javaLexed, pattern, name);
    }

    public CommentMatcher(LexExpression factory, JavaLexed javaLexed, Pattern pattern) {
        super(factory, javaLexed, pattern);
    }

    public CommentMatcher(LexExpression factory, JavaLexed javaLexed, String text) {
        super(factory, javaLexed, text);
    }

    public CommentMatcher(LexExpression factory, JavaLexed javaLexed) {
        super(factory, javaLexed);
    }

    @Override
    public MatchResult match(int i) {
        return match(i, LexicalElement.Type.COMMENT);
    }
}
