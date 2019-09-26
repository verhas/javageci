package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class LexExpression {
    private final JavaLexed javaLexed;
    private final Lexer lexer;
    public static final int NO_SENSITIVITY = 0x00;
    public static final int SPACE_SENSITIVE = 0x01;
    public static final int COMMENT_SENSITIVE = 0x02;


    public boolean isSpaceSensitive() {
        return lexer.isSpaceSensitive();
    }

    public boolean isCommentSensitive() {
        return lexer.isCommentSensitive();
    }

    LexExpression(JavaLexed javaLexed, Lexer lexer) {
        this.javaLexed = javaLexed;
        this.lexer = lexer;
    }

    public LexMatcher identifier(String id) {
        return new TerminalLexMatcher(this, javaLexed, new LexicalElement.Identifier(id));
    }

    void clean() {
        groups.clear();
        regexResults.clear();
        ;
    }

    private final Map<String, List<List<LexicalElement>>> groups = new HashMap<>();

    void store(String name, List<LexicalElement> elements) {
        if (!groups.containsKey(name)) {
            groups.put(name, new ArrayList<>());
        }
        groups.get(name).add(elements);
    }

    public List<List<LexicalElement>> group(final String name) {
        if (groups.containsKey(name)) {
            return groups.get(name);
        }
        return List.of();
    }

    private final Map<String, List<MatchResult>> regexResults = new HashMap<>();

    void store(String name, java.util.regex.MatchResult patternMatchResult) {
        if (!regexResults.containsKey(name)) {
            regexResults.put(name, new ArrayList<>());
        }
        regexResults.get(name).add(patternMatchResult);
    }

    public List<MatchResult> get(final String name) {
        if (regexResults.containsKey(name)) {
            return regexResults.get(name);
        }
        return List.of();
    }

    private LexMatcher terminal(LexicalElement le) {
        return new TerminalLexMatcher(this, javaLexed, le);
    }

    public LexMatcher keyword(String id) {
        return identifier(id);
    }

    public LexMatcher oneOf(LexMatcher... matchers) {
        return new OneOfLexMatcher(this, javaLexed, matchers);
    }

    public LexMatcher zeroOrMore(LexMatcher matcher) {
        return new Repeat(this, javaLexed, matcher, 0, Integer.MAX_VALUE);
    }

    public LexMatcher optional(LexMatcher matcher) {
        return new Repeat(this, javaLexed, matcher, 0, 1);
    }

    public LexMatcher oneOrMore(LexMatcher matcher) {
        return new Repeat(this, javaLexed, matcher, 1, Integer.MAX_VALUE);
    }

    public LexMatcher repeat(LexMatcher matcher, int min) {
        return new Repeat(this, javaLexed, matcher, min, min);
    }

    public LexMatcher repeat(LexMatcher matcher, int min, int max) {
        return new Repeat(this, javaLexed, matcher, min, max);
    }

    public LexMatcher identifier(Pattern pattern) {
        return new IdentifierMatcher(this, javaLexed, pattern);
    }

    public LexMatcher identifier(String name, Pattern pattern) {
        return new IdentifierMatcher(this, javaLexed, pattern, name);
    }

    public LexMatcher identifier() {
        return new IdentifierMatcher(this, javaLexed);
    }

    public LexMatcher string(String text) {
        return new StringMatcher(this, javaLexed, text);
    }

    public LexMatcher string(Pattern pattern) {
        return new StringMatcher(this, javaLexed, pattern);
    }

    public LexMatcher string(String name, Pattern pattern) {
        return new StringMatcher(this, javaLexed, pattern, name);
    }

    public LexMatcher string() {
        return new StringMatcher(this, javaLexed);
    }

    public LexMatcher comment(String text) {
        return new CommentMatcher(this, javaLexed, text);
    }

    public LexMatcher comment(Pattern pattern) {
        return new CommentMatcher(this, javaLexed, pattern);
    }

    public LexMatcher comment(String name, Pattern pattern) {
        return new CommentMatcher(this, javaLexed, pattern, name);
    }

    public LexMatcher comment() {
        return new CommentMatcher(this, javaLexed);
    }

    public LexMatcher integer() {
        return new IntegerMatcher(this, javaLexed);
    }

    public LexMatcher integer(Predicate<Long> predicate) {
        return new IntegerMatcher(this, javaLexed, predicate);
    }

    public LexMatcher floatNumber() {
        return new FloatMatcher(this, javaLexed);
    }

    public LexMatcher floatNumber(Predicate<Double> predicate) {
        return new FloatMatcher(this, javaLexed, predicate);
    }

    public LexMatcher list(LexMatcher... matchers) {
        return new ListMatcher(this, javaLexed, matchers);
    }

    public LexMatcher match(String string) {
        return list(lexer.apply(List.of(string)));
    }

    public LexMatcher list(LexicalElement... elements) {
        final var matchers = getLexMatchers(elements);
        return new ListMatcher(this, javaLexed, matchers);
    }

    public LexMatcher unordered(LexMatcher... matchers) {
        return new SetMatcher(this, javaLexed, matchers);
    }

    public LexMatcher unordered(LexicalElement... elements) {
        final var matchers = getLexMatchers(elements);
        return new SetMatcher(this, javaLexed, matchers);
    }

    public LexMatcher unordered(String string) {
        return unordered(lexer.apply(List.of(string)));
    }

    public LexMatcher group(String name, LexMatcher matcher) {
        return new GroupMatcher(this, javaLexed, name, matcher);
    }

    private LexMatcher[] getLexMatchers(LexicalElement[] elements) {
        final var matchers = new LexMatcher[elements.length];
        for (int i = 0; i < elements.length; i++) {
            matchers[i] = terminal(elements[i]);
        }
        return matchers;
    }

    public LexMatcher oneOf(String... strings) {
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
