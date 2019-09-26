package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.regex.Pattern;

public abstract class AbstractPatternedMatcher extends LexMatcher {
    private final String text;
    private final Pattern pattern;
    private final String name;

    AbstractPatternedMatcher(LexExpression expression, JavaLexed javaLexed, Pattern pattern) {
        this(expression, javaLexed, null, pattern);
    }

    AbstractPatternedMatcher(LexExpression expression, JavaLexed javaLexed, Pattern pattern, String name) {
        this(expression, javaLexed, null, pattern, name);
    }

    AbstractPatternedMatcher(LexExpression expression, JavaLexed javaLexed, String text) {
        this(expression, javaLexed, text, null);
    }

    AbstractPatternedMatcher(LexExpression factory, JavaLexed javaLexed) {
        this(factory, javaLexed, (String)null, null);
    }

    AbstractPatternedMatcher(LexExpression expression, JavaLexed javaLexed, String text, Pattern pattern) {
        this(expression, javaLexed, text, pattern, null);
    }

    private AbstractPatternedMatcher(LexExpression expression, JavaLexed javaLexed, String text, Pattern pattern, String name) {
        super(expression, javaLexed);
        this.text = text;
        this.pattern = pattern;
        this.name = name;
    }

    public MatchResult match(int i, LexicalElement.Type type) {
        int start = skipSpacesAndComments(i);
        if (javaLexed.get(start).type != type) {
            return MatchResult.NO_MATCH;
        }
        if (text != null) {
            if (text.equals(javaLexed.get(start).lexeme)) {
                return new MatchResult(true, start, start + 1);
            } else {
                return MatchResult.NO_MATCH;
            }
        } else {
            if (pattern != null) {
                final var regex = pattern.matcher(javaLexed.get(start).lexeme);
                if (regex.find()) {
                    store(name,regex);
                    return new MatchResult(true, start, start + 1);
                } else {
                    return MatchResult.NO_MATCH;
                }
            } else {
                return new MatchResult(true, start, start + 1);
            }
        }
    }
}
