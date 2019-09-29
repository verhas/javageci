package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;
import javax0.geci.lexeger.matchers.Lexpression;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class LexpressionBuilder {

    private static javax0.geci.lexeger.matchers.LexMatcher X(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, JavaLexed jLex, Lexpression e) {
        return (javax0.geci.lexeger.matchers.LexMatcher) matcher.apply(jLex, e);
    }

    private static javax0.geci.lexeger.matchers.LexMatcher[] X(BiFunction<JavaLexed, Lexpression, LexMatcher>[] matchers, JavaLexed jLex, Lexpression e) {
        return
            Arrays.stream(matchers).map(matcher -> X(matcher, jLex, e)).toArray(javax0.geci.lexeger.matchers.LexMatcher[]::new);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> terminal(LexicalElement le) {
        return (jLex, e) -> e.terminal(le);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> keyword(String id) {
        return identifier(id);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.oneOf(X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> zeroOrMore(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.zeroOrMore(X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> optional(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.optional(X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOrMore(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.oneOrMore(X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int times) {
        return (jLex, e) -> e.repeat(X(matcher, jLex, e), times);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int min, int max) {
        return (jLex, e) -> e.repeat(X(matcher, jLex, e), min, max);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(String id) {
        return (jLex, e) -> e.identifier(id);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(Pattern pattern) {
        return (jLex, e) -> e.identifier(pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(String name, Pattern pattern) {
        return (jLex, e) -> e.identifier(name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier() {
        return (jLex, e) -> e.identifier();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(String text) {
        return (jLex, e) -> e.character(text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(Pattern pattern) {
        return (jLex, e) -> e.character(pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(String name, Pattern pattern) {
        return (jLex, e) -> e.character(name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character() {
        return (jLex, e) -> e.character();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string(String text) {
        return (jLex, e) -> e.string(text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string(Pattern pattern) {
        return (jLex, e) -> e.string(pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string(String name, Pattern pattern) {
        return (jLex, e) -> e.string(name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string() {
        return (jLex, e) -> e.string();
    }


    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(String text) {
        return (jLex, e) -> e.type(text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(Pattern pattern) {
        return (jLex, e) -> e.type(pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(String name, Pattern pattern) {
        return (jLex, e) -> e.type(name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type() {
        return (jLex, e) -> e.type();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment(String text) {
        return (jLex, e) -> e.comment(text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment(Pattern pattern) {
        return (jLex, e) -> e.comment(pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment(String name, Pattern pattern) {
        return (jLex, e) -> e.comment(name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment() {
        return (jLex, e) -> e.comment();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> integerNumber() {
        return (jLex, e) -> e.integerNumber();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> integerNumber(Predicate<Long> predicate) {
        return (jLex, e) -> e.integerNumber(predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> number() {
        return (jLex, e) -> e.number();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> number(Predicate<Number> predicate) {
        return (jLex, e) -> e.number(predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> floatNumber() {
        return (jLex, e) -> e.floatNumber();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> floatNumber(Predicate<Double> predicate) {
        return (jLex, e) -> e.floatNumber(predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.list(X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> match(String string) {
        return (jLex, e) -> e.match(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(LexicalElement... elements) {
        return (jLex, e) -> e.list(elements);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.unordered(X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(LexicalElement... elements) {
        return (jLex, e) -> e.unordered(elements);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(String string) {
        return (jLex, e) -> e.unordered(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> group(String name, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.group(name, X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(String... strings) {
        return (jLex, e) -> e.oneOf(strings);
    }
}