package javax0.geci.lexeger.matchers;

import javax0.geci.javacomparator.LexicalElement;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

import java.util.regex.Pattern;

public class CharacterMatcher extends AbstractPatternedMatcher {

    public CharacterMatcher(Lexpression factory, JavaLexed javaLexed, Pattern pattern, String name) {
        super(factory, javaLexed, pattern, name);
    }

    public CharacterMatcher(Lexpression factory, JavaLexed javaLexed, Pattern pattern) {
        super(factory,javaLexed,pattern);
    }
    public CharacterMatcher(Lexpression factory, JavaLexed javaLexed, String text) {
        super(factory,javaLexed,text);
    }
    public CharacterMatcher(Lexpression factory, JavaLexed javaLexed) {
        super(factory,javaLexed);
    }

    @Override
    public MatchResult matchesAt(int i) {
        return match(i, LexicalElement.Type.CHARACTER);
    }
}
