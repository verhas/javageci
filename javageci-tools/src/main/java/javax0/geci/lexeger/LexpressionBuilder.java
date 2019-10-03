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


    public static class GroupNameWrapper {
        private final String name;

        public GroupNameWrapper(String name) {
            this.name = name;
        }

        @Override
        public String toString(){
            return name;
        }
    }

    public static GroupNameWrapper group(String name){
        return new GroupNameWrapper(name);
    }

    //<editor-fold id="testGenerateLexpressionBuilderMethods">
    public static BiFunction<JavaLexed, Lexpression, LexMatcher> modifier(int mask) {
        return (jLex, e) -> e.modifier(mask);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> modifier(GroupNameWrapper name, int mask) {
        return (jLex, e) -> e.modifier(name, mask);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> keyword(String id) {
        return (jLex, e) -> e.keyword(id);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> keyword(GroupNameWrapper name, String id) {
        return (jLex, e) -> e.keyword(name, id);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(GroupNameWrapper name, BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.oneOf(name, X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.oneOf(X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> zeroOrMore(GroupNameWrapper name, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.zeroOrMore(name, X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> zeroOrMore(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.zeroOrMore(X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> zeroOrMore(GroupNameWrapper name, String string) {
        return (jLex, e) -> e.zeroOrMore(name, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> zeroOrMore(String string) {
        return (jLex, e) -> e.zeroOrMore(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> optional(GroupNameWrapper name, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.optional(name, X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> optional(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.optional(X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> optional(GroupNameWrapper name, String string) {
        return (jLex, e) -> e.optional(name, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> optional(String string) {
        return (jLex, e) -> e.optional(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOrMore(GroupNameWrapper name, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.oneOrMore(name, X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOrMore(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.oneOrMore(X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOrMore(GroupNameWrapper name, String string) {
        return (jLex, e) -> e.oneOrMore(name, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOrMore(String string) {
        return (jLex, e) -> e.oneOrMore(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(GroupNameWrapper name, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int times) {
        return (jLex, e) -> e.repeat(name, X(matcher, jLex, e), times);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int times) {
        return (jLex, e) -> e.repeat(X(matcher, jLex, e), times);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(GroupNameWrapper name, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int min, int max) {
        return (jLex, e) -> e.repeat(name, X(matcher, jLex, e), min, max);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int min, int max) {
        return (jLex, e) -> e.repeat(X(matcher, jLex, e), min, max);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(GroupNameWrapper name, String id) {
        return (jLex, e) -> e.identifier(name, id);
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

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(GroupNameWrapper name) {
        return (jLex, e) -> e.identifier(name);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier() {
        return (jLex, e) -> e.identifier();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(GroupNameWrapper name, String text) {
        return (jLex, e) -> e.character(name, text);
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

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(GroupNameWrapper name) {
        return (jLex, e) -> e.character(name);
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

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(GroupNameWrapper name) {
        return (jLex, e) -> e.type(name);
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

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> integerNumber(String name) {
        return (jLex, e) -> e.integerNumber(name);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> integerNumber(String name, Predicate<Long> predicate) {
        return (jLex, e) -> e.integerNumber(name, predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> integerNumber() {
        return (jLex, e) -> e.integerNumber();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> integerNumber(Predicate<Long> predicate) {
        return (jLex, e) -> e.integerNumber(predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> number(String name) {
        return (jLex, e) -> e.number(name);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> number(String name, Predicate<Number> predicate) {
        return (jLex, e) -> e.number(name, predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> number() {
        return (jLex, e) -> e.number();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> number(Predicate<Number> predicate) {
        return (jLex, e) -> e.number(predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> floatNumber(GroupNameWrapper name) {
        return (jLex, e) -> e.floatNumber(name);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> floatNumber(GroupNameWrapper name, Predicate<Double> predicate) {
        return (jLex, e) -> e.floatNumber(name, predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> floatNumber() {
        return (jLex, e) -> e.floatNumber();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> floatNumber(Predicate<Double> predicate) {
        return (jLex, e) -> e.floatNumber(predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(GroupNameWrapper name, String... strings) {
        return (jLex, e) -> e.list(name, strings);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(GroupNameWrapper name, BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.list(name, X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(String... strings) {
        return (jLex, e) -> e.list(strings);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.list(X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> match(GroupNameWrapper name, String string) {
        return (jLex, e) -> e.match(name, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> match(String string) {
        return (jLex, e) -> e.match(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.unordered(X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(GroupNameWrapper name, BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.unordered(name, X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(LexicalElement... elements) {
        return (jLex, e) -> e.unordered(elements);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(GroupNameWrapper name, LexicalElement... elements) {
        return (jLex, e) -> e.unordered(name, elements);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(String string) {
        return (jLex, e) -> e.unordered(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(GroupNameWrapper name, String string) {
        return (jLex, e) -> e.unordered(name, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> group(String name, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.group(name, X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(GroupNameWrapper name, String... strings) {
        return (jLex, e) -> e.oneOf(name, strings);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(String... strings) {
        return (jLex, e) -> e.oneOf(strings);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> not(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.not(X(matcher, jLex, e));
    }

    //</editor-fold>


}
