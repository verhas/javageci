package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.regex.Pattern;

public class CharacterMatcher extends AbstractPatternedMatcher {

    public CharacterMatcher(LexExpression factory, JavaLexed javaLexed, Pattern pattern) {
        super(factory,javaLexed,pattern);
    }
    public CharacterMatcher(LexExpression factory, JavaLexed javaLexed, String text) {
        super(factory,javaLexed,text);
    }
    public CharacterMatcher(LexExpression factory, JavaLexed javaLexed) {
        super(factory,javaLexed);
    }

    @Override
    public MatchResult match(int i) {
        return match(i, LexicalElement.Type.CHARACTER);
    }
}
