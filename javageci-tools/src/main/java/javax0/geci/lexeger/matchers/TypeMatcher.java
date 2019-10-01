package javax0.geci.lexeger.matchers;

import javax0.geci.javacomparator.LexicalElement;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

import java.util.regex.Pattern;

public class TypeMatcher extends AbstractPatternedMatcher {

    public TypeMatcher(Lexpression factory, JavaLexed javaLexed, Pattern pattern, String name) {
        super(factory, javaLexed, pattern, name);
    }

    public TypeMatcher(Lexpression factory, JavaLexed javaLexed, Pattern pattern) {
        super(factory, javaLexed, pattern);
    }

    public TypeMatcher(Lexpression factory, JavaLexed javaLexed, String text) {
        super(factory, javaLexed, text);
    }

    public TypeMatcher(Lexpression factory, JavaLexed javaLexed) {
        super(factory, javaLexed);
    }

    @Override
    public MatchResult matchesAt(int i) {
        var result = match(i, LexicalElement.Type.IDENTIFIER);
        if (!result.matches) {
            return result;
        }
        int j = skipSpacesAndComments(result.end);
        if (j + 1 < javaLexed.size() && javaLexed.get(j + 1).getLexeme().equals("<")) {
            int counter = 1;
            while (j < javaLexed.size()) {
                if (javaLexed.get(i).getLexeme().equals("<")) {
                    counter++;
                }
                if (javaLexed.get(j).getLexeme().equals(">")) {
                    counter++;
                }
                j++;
                if (counter == 0) {
                    return matching( j, j);
                }
            }
        }
        return matching( i, j);
    }
}
