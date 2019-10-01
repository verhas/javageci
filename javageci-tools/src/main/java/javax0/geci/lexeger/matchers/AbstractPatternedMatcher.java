package javax0.geci.lexeger.matchers;

import javax0.geci.javacomparator.lex.LexicalElement;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

import java.util.regex.Pattern;

abstract class AbstractPatternedMatcher extends LexMatcher {
    private final String text;
    private final Pattern pattern;
    private final String name;

    AbstractPatternedMatcher(Lexpression expression, JavaLexed javaLexed, Pattern pattern) {
        this(expression, javaLexed, null, pattern);
    }

    AbstractPatternedMatcher(Lexpression expression, JavaLexed javaLexed, Pattern pattern, String name) {
        this(expression, javaLexed, null, pattern, name);
    }

    AbstractPatternedMatcher(Lexpression expression, JavaLexed javaLexed, String text) {
        this(expression, javaLexed, text, null);
    }

    AbstractPatternedMatcher(Lexpression factory, JavaLexed javaLexed) {
        this(factory, javaLexed, (String)null, null);
    }

    AbstractPatternedMatcher(Lexpression expression, JavaLexed javaLexed, String text, Pattern pattern) {
        this(expression, javaLexed, text, pattern, null);
    }

    private AbstractPatternedMatcher(Lexpression expression, JavaLexed javaLexed, String text, Pattern pattern, String name) {
        super(expression, javaLexed);
        this.text = text;
        this.pattern = pattern;
        this.name = name;
    }

    public MatchResult match(int i, LexicalElement.Type type) {
        if (consumed()) {
            return MatchResult.NO_MATCH;
        }
        int start = skipSpacesAndComments(i);
        if (javaLexed.get(start).getType() != type) {
            return MatchResult.NO_MATCH;
        }
        if (text != null) {
            if (text.equals(javaLexed.get(start).getLexeme())) {
                return matching( start, start + 1);
            } else {
                return MatchResult.NO_MATCH;
            }
        } else {
            if (pattern != null) {
                final var regex = pattern.matcher(javaLexed.get(start).getLexeme());
                if (regex.find()) {
                    store(name,regex);
                    return matching( start, start + 1);
                } else {
                    remove(name);
                    return MatchResult.NO_MATCH;
                }
            } else {
                return matching( start, start + 1);
            }
        }
    }
}
