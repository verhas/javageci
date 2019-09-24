package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.List;

public class LexMatcherFactory {
    private final JavaLexed javaLexed;

    LexMatcherFactory(JavaLexed javaLexed) {
        this.javaLexed = javaLexed;
    }

    public LexMatcher identifier(String id) {
        return new TerminalLexMatcher(javaLexed, new LexicalElement.Identifier(id));
    }

    private LexMatcher terminal(LexicalElement le) {
        return new TerminalLexMatcher(javaLexed, le);
    }

    public LexMatcher kw(String id) {
        return identifier(id);
    }

    public LexMatcher oneOf(LexMatcher... matchers) {
        return new OneOfLexMatcher(javaLexed, matchers);
    }

    public LexMatcher zeroOrMore(LexMatcher matcher) {
        return new Repeat(javaLexed, matcher, 0, Integer.MAX_VALUE);
    }

    public LexMatcher optional(LexMatcher matcher) {
        return new Repeat(javaLexed, matcher, 0, 1);
    }

    public LexMatcher oneOrMore(LexMatcher matcher) {
        return new Repeat(javaLexed, matcher, 1, Integer.MAX_VALUE);
    }

    public LexMatcher repeat(LexMatcher matcher, int min) {
        return new Repeat(javaLexed, matcher, min, min);
    }

    public LexMatcher repeat(LexMatcher matcher, int min, int max) {
        return new Repeat(javaLexed, matcher, min, max);
    }

    public LexMatcher list(LexMatcher... matchers) {
        return new ListMatcher(javaLexed, matchers);
    }

    public LexMatcher match(String string) {
        return list(new Lexer().apply(List.of(string)));
    }

    public LexMatcher list(LexicalElement... elements) {
        final var matchers = new LexMatcher[elements.length];
        for (int i = 0; i < elements.length; i++) {
            matchers[i] = terminal(elements[i]);
        }
        return new ListMatcher(javaLexed, matchers);
    }

    public LexMatcher oneOf(String... strings) {
        final var lexer = new Lexer();
        final var matchers = new LexMatcher[strings.length];
        int i = 0;
        for (final var string : strings) {
            final var lexicalElements = lexer.apply(List.of(string));
            if (lexicalElements.length == 1) {
                matchers[i++] = terminal(lexicalElements[0]);
            } else {
                matchers[i++] = list(lexicalElements);
            }
        }
        return oneOf(matchers);
    }

}
