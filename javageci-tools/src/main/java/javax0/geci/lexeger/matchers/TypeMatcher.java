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
        final var start = skipSpacesAndComments(i);
        var result = match(start, LexicalElement.Type.IDENTIFIER);
        if (!result.matches) {
            return MatchResult.NO_MATCH;
        }
        int j = skipSpacesAndComments(result.end);
        while (j < javaLexed.size() && javaLexed.get(j).getLexeme().equals(".")) {
            j = skipSpacesAndComments(j + 1);
            if (j < javaLexed.size() && javaLexed.get(j).getType() == LexicalElement.Type.IDENTIFIER) {
                j = skipSpacesAndComments(j + 1);
            } else {
                return MatchResult.NO_MATCH;
            }
        }
        if (j < javaLexed.size() && javaLexed.get(j).getLexeme().equals("<")) {
            int ltCounter = 1;
            j = skipSpacesAndComments(j + 1);
            while (j < javaLexed.size()) {
                var lex = javaLexed.get(j);
                if (lex.getLexeme().equals("<")) {
                    ltCounter++;
                }
                if (lex.getLexeme().equals(">")) {
                    ltCounter--;
                }
                if (lex.getLexeme().equals(">>")) {
                    ltCounter-=2;
                }
                if (lex.getLexeme().equals(">>>")) {
                    ltCounter-=3;
                }
                j = skipSpacesAndComments(j + 1);
                if (ltCounter == 0) {
                    break;
                }
            }
            if (ltCounter > 0) {
                return MatchResult.NO_MATCH;
            }
        }
        while (j < javaLexed.size() && javaLexed.get(j).getLexeme().equals("[")) {
            j = skipSpacesAndComments(j + 1);
            if (j < javaLexed.size() && javaLexed.get(j).getLexeme().equals("]")) {
                j = skipSpacesAndComments(j + 1);
            } else {
                return MatchResult.NO_MATCH;
            }
        }
        return matching(start, j);
    }
}
