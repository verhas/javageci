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

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> keyword(String id) {
        return (jLex, e) -> e.keyword(id);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.oneOf(X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> zeroOrMore(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.zeroOrMore(X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> zeroOrMore(String string) {
        return (jLex, e) -> e.zeroOrMore(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> optional(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.optional(X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> optional(String string) {
        return (jLex, e) -> e.optional(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOrMore(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.oneOrMore(X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOrMore(String string) {
        return (jLex, e) -> e.oneOrMore(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int times) {
        return (jLex, e) -> e.repeat(X(matcher, jLex, e), times);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int min, int max) {
        return (jLex, e) -> e.repeat(X(matcher, jLex, e), min, max);
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

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(String... strings) {
        return (jLex, e) -> e.list(strings);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.list(X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> match(String string) {
        return (jLex, e) -> e.match(string);
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

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> not(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.not(X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> not(LexicalElement... elements) {
        return (jLex, e) -> e.not(elements);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> not(String string) {
        return (jLex, e) -> e.not(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> anyTill(BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.anyTill(X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> anyTill(LexicalElement... elements) {
        return (jLex, e) -> e.anyTill(elements);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> anyTill(String string) {
        return (jLex, e) -> e.anyTill(string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> modifier(GroupNameWrapper nameWrapper, int mask) {
        return (jLex, e) -> e.modifier(nameWrapper, mask);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> keyword(GroupNameWrapper nameWrapper, String id) {
        return (jLex, e) -> e.keyword(nameWrapper, id);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(GroupNameWrapper nameWrapper, BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.oneOf(nameWrapper, X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> zeroOrMore(GroupNameWrapper nameWrapper, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.zeroOrMore(nameWrapper, X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> zeroOrMore(GroupNameWrapper nameWrapper, String string) {
        return (jLex, e) -> e.zeroOrMore(nameWrapper, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> optional(GroupNameWrapper nameWrapper, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.optional(nameWrapper, X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> optional(GroupNameWrapper nameWrapper, String string) {
        return (jLex, e) -> e.optional(nameWrapper, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOrMore(GroupNameWrapper nameWrapper, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.oneOrMore(nameWrapper, X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOrMore(GroupNameWrapper nameWrapper, String string) {
        return (jLex, e) -> e.oneOrMore(nameWrapper, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(GroupNameWrapper nameWrapper, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int times) {
        return (jLex, e) -> e.repeat(nameWrapper, X(matcher, jLex, e), times);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> repeat(GroupNameWrapper nameWrapper, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher, int min, int max) {
        return (jLex, e) -> e.repeat(nameWrapper, X(matcher, jLex, e), min, max);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> integerNumber(GroupNameWrapper nameWrapper) {
        return (jLex, e) -> e.integerNumber(nameWrapper);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> integerNumber(GroupNameWrapper nameWrapper, Predicate<Long> predicate) {
        return (jLex, e) -> e.integerNumber(nameWrapper, predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> number(GroupNameWrapper nameWrapper) {
        return (jLex, e) -> e.number(nameWrapper);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> number(GroupNameWrapper nameWrapper, Predicate<Number> predicate) {
        return (jLex, e) -> e.number(nameWrapper, predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> floatNumber(GroupNameWrapper nameWrapper) {
        return (jLex, e) -> e.floatNumber(nameWrapper);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> floatNumber(GroupNameWrapper nameWrapper, Predicate<Double> predicate) {
        return (jLex, e) -> e.floatNumber(nameWrapper, predicate);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(GroupNameWrapper nameWrapper, String... strings) {
        return (jLex, e) -> e.list(nameWrapper, strings);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> list(GroupNameWrapper nameWrapper, BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.list(nameWrapper, X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> match(GroupNameWrapper nameWrapper, String string) {
        return (jLex, e) -> e.match(nameWrapper, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(GroupNameWrapper nameWrapper, BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.unordered(nameWrapper, X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(GroupNameWrapper nameWrapper, LexicalElement... elements) {
        return (jLex, e) -> e.unordered(nameWrapper, elements);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> unordered(GroupNameWrapper nameWrapper, String string) {
        return (jLex, e) -> e.unordered(nameWrapper, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> group(GroupNameWrapper nameWrapper, String name, BiFunction<JavaLexed, Lexpression, LexMatcher> matcher) {
        return (jLex, e) -> e.group(nameWrapper, name, X(matcher, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> oneOf(GroupNameWrapper nameWrapper, String... strings) {
        return (jLex, e) -> e.oneOf(nameWrapper, strings);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> not(GroupNameWrapper nameWrapper, BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.not(nameWrapper, X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> not(GroupNameWrapper nameWrapper, LexicalElement... elements) {
        return (jLex, e) -> e.not(nameWrapper, elements);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> not(GroupNameWrapper nameWrapper, String string) {
        return (jLex, e) -> e.not(nameWrapper, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> anyTill(GroupNameWrapper nameWrapper, BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers) {
        return (jLex, e) -> e.anyTill(nameWrapper, X(matchers, jLex, e));
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> anyTill(GroupNameWrapper nameWrapper, LexicalElement... elements) {
        return (jLex, e) -> e.anyTill(nameWrapper, elements);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> anyTill(GroupNameWrapper nameWrapper, String string) {
        return (jLex, e) -> e.anyTill(nameWrapper, string);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(GroupNameWrapper nameWrapper) {
        return (jLex, e) -> e.identifier(nameWrapper);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(GroupNameWrapper nameWrapper, String text) {
        return (jLex, e) -> e.identifier(nameWrapper, text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(GroupNameWrapper nameWrapper, Pattern pattern) {
        return (jLex, e) -> e.identifier(nameWrapper, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(GroupNameWrapper nameWrapper, String name, Pattern pattern) {
        return (jLex, e) -> e.identifier(nameWrapper, name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(GroupNameWrapper nameWrapper) {
        return (jLex, e) -> e.character(nameWrapper);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(GroupNameWrapper nameWrapper, String text) {
        return (jLex, e) -> e.character(nameWrapper, text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(GroupNameWrapper nameWrapper, Pattern pattern) {
        return (jLex, e) -> e.character(nameWrapper, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character(GroupNameWrapper nameWrapper, String name, Pattern pattern) {
        return (jLex, e) -> e.character(nameWrapper, name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string(GroupNameWrapper nameWrapper) {
        return (jLex, e) -> e.string(nameWrapper);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string(GroupNameWrapper nameWrapper, String text) {
        return (jLex, e) -> e.string(nameWrapper, text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string(GroupNameWrapper nameWrapper, Pattern pattern) {
        return (jLex, e) -> e.string(nameWrapper, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string(GroupNameWrapper nameWrapper, String name, Pattern pattern) {
        return (jLex, e) -> e.string(nameWrapper, name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(GroupNameWrapper nameWrapper) {
        return (jLex, e) -> e.type(nameWrapper);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(GroupNameWrapper nameWrapper, String text) {
        return (jLex, e) -> e.type(nameWrapper, text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(GroupNameWrapper nameWrapper, Pattern pattern) {
        return (jLex, e) -> e.type(nameWrapper, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type(GroupNameWrapper nameWrapper, String name, Pattern pattern) {
        return (jLex, e) -> e.type(nameWrapper, name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment(GroupNameWrapper nameWrapper) {
        return (jLex, e) -> e.comment(nameWrapper);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment(GroupNameWrapper nameWrapper, String text) {
        return (jLex, e) -> e.comment(nameWrapper, text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment(GroupNameWrapper nameWrapper, Pattern pattern) {
        return (jLex, e) -> e.comment(nameWrapper, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment(GroupNameWrapper nameWrapper, String name, Pattern pattern) {
        return (jLex, e) -> e.comment(nameWrapper, name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier() {
        return (jLex, e) -> e.identifier();
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(String text) {
        return (jLex, e) -> e.identifier(text);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(Pattern pattern) {
        return (jLex, e) -> e.identifier(pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> identifier(String name, Pattern pattern) {
        return (jLex, e) -> e.identifier(name, pattern);
    }

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> character() {
        return (jLex, e) -> e.character();
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

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> string() {
        return (jLex, e) -> e.string();
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

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> type() {
        return (jLex, e) -> e.type();
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

    public static BiFunction<JavaLexed, Lexpression, LexMatcher> comment() {
        return (jLex, e) -> e.comment();
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

    //</editor-fold>


}
