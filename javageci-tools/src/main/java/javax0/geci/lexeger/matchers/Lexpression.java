package javax0.geci.lexeger.matchers;

import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;
import javax0.geci.lexeger.JavaLexed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Lexpression {
    private final JavaLexed javaLexed;
    public final Lexer lexer;
    public static final int NO_SENSITIVITY = 0x00;
    public static final int SPACE_SENSITIVE = 0x01;
    public static final int COMMENT_SENSITIVE = 0x02;


    public javax0.geci.lexeger.LexMatcher usingExpression(BiFunction<JavaLexed, Lexpression, javax0.geci.lexeger.LexMatcher> function){
        return function.apply(javaLexed,this);
    }

    public boolean isSpaceSensitive() {
        return lexer.isSpaceSensitive();
    }

    public boolean isCommentSensitive() {
        return lexer.isCommentSensitive();
    }

    public Lexpression(JavaLexed javaLexed, Lexer lexer) {
        this.javaLexed = javaLexed;
        this.lexer = lexer;
    }

    void clean() {
        groups.clear();
        regexResults.clear();
        ;
    }

    private final Map<String, List<javax0.geci.javacomparator.LexicalElement>> groups = new HashMap<>();

    void remove(String name) {
        groups.remove(name);
        regexResults.remove(name);
    }
    void store(String name, List<javax0.geci.javacomparator.LexicalElement> elements) {
        if (regexResults.containsKey(name)) {
            throw new IllegalArgumentException(name + " cannot be used to identify both a lex group and regex result");
        }
        groups.put(name, elements);
    }

    void store(String name, java.util.regex.MatchResult patternMatchResult) {
        if (groups.containsKey(name)) {
            throw new IllegalArgumentException(name + " cannot be used to identify both a lex group and regex result");
        }
        regexResults.put(name, patternMatchResult);
    }

    public List<javax0.geci.javacomparator.LexicalElement> group(final String name) {
        if (groups.containsKey(name)) {
            return groups.get(name);
        }
        return List.of();
    }

    private final Map<String, MatchResult> regexResults = new HashMap<>();


    public Optional<MatchResult> matchResult(final String name) {
        if (regexResults.containsKey(name)) {
            return Optional.of(regexResults.get(name));
        }
        return Optional.empty();
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

    public LexMatcher zeroOrMore(String string) {
        return zeroOrMore(getMatcher(string));
    }

    public LexMatcher optional(LexMatcher matcher) {
        return new Repeat(this, javaLexed, matcher, 0, 1);
    }

    public LexMatcher optional(String string) {
        return optional(getMatcher(string));
    }

    public LexMatcher oneOrMore(LexMatcher matcher) {
        return new Repeat(this, javaLexed, matcher, 1, Integer.MAX_VALUE);
    }

    public LexMatcher oneOrMore(String string) {
        return oneOrMore(getMatcher(string));
    }

    public LexMatcher repeat(LexMatcher matcher, int times) {
        return new Repeat(this, javaLexed, matcher, times, times);
    }

    public LexMatcher repeat(LexMatcher matcher, int min, int max) {
        return new Repeat(this, javaLexed, matcher, min, max);
    }

    public LexMatcher identifier(String id) {
        return new TerminalLexMatcher(this, javaLexed, new LexicalElement.Identifier(id));
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

    public LexMatcher character(String text) {
        return new CharacterMatcher(this, javaLexed, text);
    }

    public LexMatcher character(Pattern pattern) {
        return new CharacterMatcher(this, javaLexed, pattern);
    }

    public LexMatcher character(String name, Pattern pattern) {
        return new CharacterMatcher(this, javaLexed, pattern, name);
    }

    public LexMatcher character() {
        return new CharacterMatcher(this, javaLexed);
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


    public LexMatcher type(String text) {
        return new TypeMatcher(this, javaLexed, text);
    }

    public LexMatcher type(Pattern pattern) {
        return new TypeMatcher(this, javaLexed, pattern);
    }

    public LexMatcher type(String name, Pattern pattern) {
        return new TypeMatcher(this, javaLexed, pattern, name);
    }

    public LexMatcher type() {
        return new TypeMatcher(this, javaLexed);
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

    public LexMatcher integerNumber() {
        return new IntegerMatcher(this, javaLexed);
    }

    public LexMatcher integerNumber(Predicate<Long> predicate) {
        return new IntegerMatcher(this, javaLexed, predicate);
    }

    public LexMatcher number() {
        return new NumberMatcher(this, javaLexed);
    }

    public LexMatcher number(Predicate<Number> predicate) {
        return new NumberMatcher(this, javaLexed, predicate);
    }

    public LexMatcher floatNumber() {
        return new FloatMatcher(this, javaLexed);
    }

    public LexMatcher floatNumber(Predicate<Double> predicate) {
        return new FloatMatcher(this, javaLexed, predicate);
    }

    private LexMatcher list(LexicalElement... elements) {
        final var matchers = getLexMatchers(elements);
        return new ListMatcher(this, javaLexed, matchers);
    }

    public LexMatcher list(String... strings) {
        return list(getMatchers(strings));
    }
    public LexMatcher list(LexMatcher... matchers) {
        return new ListMatcher(this, javaLexed, matchers);
    }

    public LexMatcher match(String string) {
        return list(lexer.apply(List.of(string)));
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
        return oneOf(getMatchers(strings));
    }

    private LexMatcher getMatcher(String string) {
        final var lexicalElements = lexer.apply(List.of(string));
        if (lexicalElements.length == 1) {
            return terminal(lexicalElements[0]);
        } else {
            return list(lexicalElements);
        }
    }

    private LexMatcher[] getMatchers(String... strings) {
        final var matchers = new LexMatcher[strings.length];
        int i = 0;
        for (final var string : strings) {
            matchers[i++] = getMatcher(string);
        }
        return matchers;
    }

}
