package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;
import javax0.geci.lexeger.matchers.CharacterMatcher;
import javax0.geci.lexeger.matchers.CommentMatcher;
import javax0.geci.lexeger.matchers.FloatMatcher;
import javax0.geci.lexeger.matchers.GroupMatcher;
import javax0.geci.lexeger.matchers.IdentifierMatcher;
import javax0.geci.lexeger.matchers.IntegerMatcher;
import javax0.geci.lexeger.matchers.Lexpression;
import javax0.geci.lexeger.matchers.ListMatcher;
import javax0.geci.lexeger.matchers.NumberMatcher;
import javax0.geci.lexeger.matchers.OneOfLexMatcher;
import javax0.geci.lexeger.matchers.Repeat;
import javax0.geci.lexeger.matchers.SetMatcher;
import javax0.geci.lexeger.matchers.StringMatcher;
import javax0.geci.lexeger.matchers.TerminalLexMatcher;
import javax0.geci.lexeger.matchers.TypeMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class LexpressionBuilder {
    public static LexpressionBuilder builder() {
        return new LexpressionBuilder();
    }


    private static javax0.geci.lexeger.matchers.LexMatcher X(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, JavaLexed javaLexed, Lexpression expression) {
        return (javax0.geci.lexeger.matchers.LexMatcher) matcher.apply(javaLexed, expression);
    }

    private static javax0.geci.lexeger.matchers.LexMatcher[] X(BiFunction<JavaLexed, Lexpression, LexMatcher>[] matchers, JavaLexed javaLexed, Lexpression expression) {
        return
            Arrays.stream(matchers).map(matcher -> X(matcher, javaLexed, expression)).toArray(javax0.geci.lexeger.matchers.LexMatcher[]::new);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> terminal(LexicalElement le) {
        return (javaLexed, expression) -> new TerminalLexMatcher(expression, javaLexed, le);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> keyword(String id) {
        return identifier(id);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(LexMatcher... matchers) {
        return (javaLexed, expression) -> new OneOfLexMatcher(expression, javaLexed, (javax0.geci.lexeger.matchers.LexMatcher[]) matchers);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> zeroOrMore(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (javaLexed, expression) -> new Repeat(expression, javaLexed, X(matcher, javaLexed, expression), 0, Integer.MAX_VALUE);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> optional(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (javaLexed, expression) -> new Repeat(expression, javaLexed, X(matcher, javaLexed, expression), 0, 1);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOrMore(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (javaLexed, expression) -> new Repeat(expression, javaLexed, X(matcher, javaLexed, expression), 1, Integer.MAX_VALUE);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int min) {
        return (javaLexed, expression) -> new Repeat(expression, javaLexed, X(matcher, javaLexed, expression), min, min);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int min, int max) {
        return (javaLexed, expression) -> new Repeat(expression, javaLexed, X(matcher, javaLexed, expression), min, max);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(String id) {
        return (javaLexed, expression) -> new TerminalLexMatcher(expression, javaLexed, new LexicalElement.Identifier(id));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(Pattern pattern) {
        return (javaLexed, expression) -> new IdentifierMatcher(expression, javaLexed, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(String name, Pattern pattern) {
        return (javaLexed, expression) -> new IdentifierMatcher(expression, javaLexed, pattern, name);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier() {
        return (javaLexed, expression) -> new IdentifierMatcher(expression, javaLexed);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(String text) {
        return (javaLexed, expression) -> new CharacterMatcher(expression, javaLexed, text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(Pattern pattern) {
        return (javaLexed, expression) -> new CharacterMatcher(expression, javaLexed, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(String name, Pattern pattern) {
        return (javaLexed, expression) -> new CharacterMatcher(expression, javaLexed, pattern, name);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character() {
        return (javaLexed, expression) -> new CharacterMatcher(expression, javaLexed);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string(String text) {
        return (javaLexed, expression) -> new StringMatcher(expression, javaLexed, text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string(Pattern pattern) {
        return (javaLexed, expression) -> new StringMatcher(expression, javaLexed, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string(String name, Pattern pattern) {
        return (javaLexed, expression) -> new StringMatcher(expression, javaLexed, pattern, name);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string() {
        return (javaLexed, expression) -> new StringMatcher(expression, javaLexed);
    }


    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(String text) {
        return (javaLexed, expression) -> new TypeMatcher(expression, javaLexed, text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(Pattern pattern) {
        return (javaLexed, expression) -> new TypeMatcher(expression, javaLexed, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(String name, Pattern pattern) {
        return (javaLexed, expression) -> new TypeMatcher(expression, javaLexed, pattern, name);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type() {
        return (javaLexed, expression) -> new TypeMatcher(expression, javaLexed);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment(String text) {
        return (javaLexed, expression) -> new CommentMatcher(expression, javaLexed, text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment(Pattern pattern) {
        return (javaLexed, expression) -> new CommentMatcher(expression, javaLexed, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment(String name, Pattern pattern) {
        return (javaLexed, expression) -> new CommentMatcher(expression, javaLexed, pattern, name);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment() {
        return (javaLexed, expression) -> new CommentMatcher(expression, javaLexed);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> integerNumber() {
        return (javaLexed, expression) -> new IntegerMatcher(expression, javaLexed);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> integerNumber(Predicate<Long> predicate) {
        return (javaLexed, expression) -> new IntegerMatcher(expression, javaLexed, predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> number() {
        return (javaLexed, expression) -> new NumberMatcher(expression, javaLexed);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> number(Predicate<Number> predicate) {
        return (javaLexed, expression) -> new NumberMatcher(expression, javaLexed, predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> floatNumber() {
        return (javaLexed, expression) -> new FloatMatcher(expression, javaLexed);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> floatNumber(Predicate<Double> predicate) {
        return (javaLexed, expression) -> new FloatMatcher(expression, javaLexed, predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (javaLexed, expression) -> new ListMatcher(expression, javaLexed, X(matchers, javaLexed, expression));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> match(String string) {
        return (javaLexed, expression) -> new ListMatcher(expression, javaLexed, X(getLexMatchers(expression.lexer.apply(List.of(string))), javaLexed, expression));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(LexicalElement... elements) {
        final var matchers = getLexMatchers(elements);
        return (javaLexed, expression) -> new ListMatcher(expression, javaLexed, X(matchers, javaLexed, expression));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (javaLexed, expression) -> new SetMatcher(expression, javaLexed, X(matchers, javaLexed, expression));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(LexicalElement... elements) {
        final var matchers = getLexMatchers(elements);
        return (javaLexed, expression) -> new SetMatcher(expression, javaLexed, X(matchers, javaLexed, expression));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(String string) {
        return (javaLexed, expression) -> new SetMatcher(expression, javaLexed, X(getLexMatchers(expression.lexer.apply(List.of(string))), javaLexed, expression));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> group(String name, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (javaLexed, expression) -> new GroupMatcher(expression, javaLexed, name, X(matcher, javaLexed, expression));
    }

    private static BiFunction<JavaLexed, Lexpression, LexMatcher>[] getLexMatchers(LexicalElement[] elements) {
        final var matchers = new BiFunction[elements.length];
        for (int i = 0; i < elements.length; i++) {
            matchers[i] = terminal(elements[i]);
        }
        return matchers;
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(String... strings) {
        return (javaLexed, expression) -> {
            final var matchers = new LexMatcher[strings.length];
            int i = 0;
            for (final var string : strings) {
                final var lexicalElements = expression.lexer.apply(List.of(string));
                if (lexicalElements.length == 1) {
                    matchers[i++] = expression.terminal(lexicalElements[0]);
                } else {
                    matchers[i++] = expression.list(lexicalElements);
                }
            }
            return new OneOfLexMatcher(expression, javaLexed, (javax0.geci.lexeger.matchers.LexMatcher[]) matchers);
        };
    }


}
