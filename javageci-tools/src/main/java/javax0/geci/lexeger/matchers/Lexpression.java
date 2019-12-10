package javax0.geci.lexeger.matchers;

import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.LexpressionBuilder.GroupNameWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Lexpression {
    public static final int NO_SENSITIVITY = 0x00;
    public static final int SPACE_SENSITIVE = 0x01;
    public static final int COMMENT_SENSITIVE = 0x02;
    private final Lexer lexer;
    private final JavaLexed javaLexed;
    private final Map<String, List<javax0.geci.javacomparator.LexicalElement>> groups = new HashMap<>();
    private final Map<String, MatchResult> regexResults = new HashMap<>();

    public Lexpression(JavaLexed javaLexed, Lexer lexer) {
        this.javaLexed = javaLexed;
        this.lexer = lexer;
    }

    /**
     * @return {@code true} if the underlying lexical analyzer is space
     * sensitive.
     */
    public boolean isSpaceSensitive() {
        return lexer.isSpaceSensitive();
    }

    /**
     * @return {@code true} if the underlying lexical analyzer is
     * comment sensitive.
     */
    public boolean isCommentSensitive() {
        return lexer.isCommentSensitive();
    }

    /**
     * Cleans the state of the matching analysis. If there was any group
     * collected or regular expression match they are deleted.
     */
    void clean() {
        groups.clear();
        regexResults.clear();
    }

    /**
     * Delete the group and the regular expression match that was
     * assigned to this name if there was any.
     *
     * @param name is the name of the group and/or the regular
     *             expression match result
     */
    void remove(String name) {
        groups.remove(name);
        regexResults.remove(name);
    }

    /**
     * Store the elements of the list of the lexical elements and assign
     * it to the name given as first argument. The list is stored and
     * not copied.
     *
     * @param name     to assign the list to
     * @param elements the elements to store
     */
    void store(String name, List<javax0.geci.javacomparator.LexicalElement> elements) {
        if (regexResults.containsKey(name)) {
            throw new IllegalArgumentException(name + " cannot be used to identify both a lex group and regex result");
        }
        groups.put(name, elements);
    }

    /**
     * Store the regular expression match result and assign it to the
     * name given as first argument.
     *
     * @param name               to assign the result to
     * @param patternMatchResult the pattern match result we store
     */
    void store(String name, java.util.regex.MatchResult patternMatchResult) {
        if (groups.containsKey(name)) {
            throw new IllegalArgumentException(name + " cannot be used to identify both a lex group and regex result");
        }
        regexResults.put(name, patternMatchResult);
    }

    /**
     * Get the list of lexical expressions that were stored in the group
     * named.
     *
     * @param name the name of the group
     * @return the list of lexical expressions that was stored with the
     * name or an empty list in case there is no list stored for the
     * given name.
     */
    public List<javax0.geci.javacomparator.LexicalElement> group(final String name) {
        if (groups.containsKey(name)) {
            return groups.get(name);
        }
        return Collections.emptyList();
    }

    public Optional<MatchResult> regexGroups(final String name) {
        if (regexResults.containsKey(name)) {
            return Optional.of(regexResults.get(name));
        }
        return Optional.empty();
    }

    private LexMatcher terminal(LexicalElement le) {
        return new TerminalLexMatcher(this, javaLexed, le);
    }

    /**
     *  Creates a matcher that matches a modifier.
     *
     * @param mask the bit value of the modifier that is accepted by the
     *             matcher. The value should be taken from the class
     *             {@link java.lang.reflect.Modifier}. For example if
     *             only {@code private} should be accepted as a modifier
     *             then the {@code mask} argument has to be {@link
     *             java.lang.reflect.Modifier#PRIVATE}. If there are
     *             more than one modifiers accepted, for example {@code
     *             private} as a modifier is accepted just as well as
     *             {@code protected} then the value {@code
     *             Modifier.PRIVATE|Modifier.PROTECTED} has to be used.
     * @return the new matcher
     */
    public LexMatcher modifier(int mask) {
        return new ModifierMatcher(this, javaLexed, mask);
    }

    /**
     * Create a matcher that matches a keyword.
     *
     * @param id the string representation of the keyword
     * @return the new matcher
     */
    public LexMatcher keyword(String id) {
        return identifier(id);
    }

    /**
     * Creates a matcher that matches when any of the underlying
     * matchers match.
     *
     * @param matchers the underlying matchers at least one of which
     *                 should match for the returned matcher to be
     *                 successfully matching
     * @return the new matcher
     */
    public LexMatcher oneOf(LexMatcher... matchers) {
        return new OneOfLexMatcher(this, javaLexed, matchers);
    }

    /**
     * Create a matcher that will match zero or more of the underlying matcher.
     *
     * @param matcher the underlying matcher that should match zero or more times
     * @return the new matcher
     */
    public LexMatcher zeroOrMore(LexMatcher matcher) {
        return new Repeat(this, javaLexed, matcher, 0, Integer.MAX_VALUE);
    }

    /**
     * Create a matcher that will match zero or more of the underlying
     * matcher.
     *
     * @param string representing one or more lexical element, each may
     *              match exactly the same lexical element
     * @return the new matcher
     */
    public LexMatcher zeroOrMore(String string) {
        return zeroOrMore(getMatcher(string));
    }

    /**
     * Creates a new matcher that matches zero or one time the underlying matcher
     *
     * @param matcher the underlying matcher
     * @return the new matcher
     */
    public LexMatcher optional(LexMatcher matcher) {
        return new Repeat(this, javaLexed, matcher, 0, 1);
    }

    /**
     * Creates a new matcher that matches zero or one time the underlying matcher
     *
     * @param string representing one or more lexical element, each may match exactly the same lexical element
     * @return the new matcher
     */
    public LexMatcher optional(String string) {
        return optional(getMatcher(string));
    }

    /**
     * Creates a new matcher that matches one or more time the underlying matcher
     *
     * @param matcher the underlying matcher
     * @return the new matcher
     */
    public LexMatcher oneOrMore(LexMatcher matcher) {
        return new Repeat(this, javaLexed, matcher, 1, Integer.MAX_VALUE);
    }

    /**
     * Creates a new matcher that matches one or more time the underlying matcher
     *
     * @param string representing one or more lexical element, each may match exactly the same lexical element
     * @return the new matcher
     */
    public LexMatcher oneOrMore(String string) {
        return oneOrMore(getMatcher(string));
    }

    /**
     * Creates a new matcher that matches {@code times} times the underlying matcher
     *
     * @param matcher the underlying matcher
     * @param times the number, how many times the underlying matcher must match
     * @return the new matcher
     */
    public LexMatcher repeat(LexMatcher matcher, int times) {
        return new Repeat(this, javaLexed, matcher, times, times);
    }

    /**
     * Creates a new matcher that matches {@code times} times the underlying matcher
     *
     * @param matcher the underlying matcher
     * @param min the number, how many times the underlying matcher must match at least
     * @param max the number, how many times the underlying matcher must match at most
     * @return the new matcher
     */
    public LexMatcher repeat(LexMatcher matcher, int min, int max) {
        return new Repeat(this, javaLexed, matcher, min, max);
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

    private LexMatcher list(GroupNameWrapper name, LexicalElement... elements) {
        return group(name.toString(), list(elements));
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
        return list(lexer.apply(Collections.singletonList(string)));
    }

    public LexMatcher unordered(LexMatcher... matchers) {
        return new SetMatcher(this, javaLexed, matchers);
    }

    public LexMatcher unordered(LexicalElement... elements) {
        final var matchers = getLexMatchers(elements);
        return unordered(matchers);
    }

    public LexMatcher unordered(String string) {
        return unordered(lexer.apply(Collections.singletonList(string)));
    }

    public LexMatcher group(String name, LexMatcher matcher) {
        return new GroupMatcher(this, javaLexed, name, matcher);
    }

    public LexMatcher oneOf(String... strings) {
        return oneOf(getMatchers(strings));
    }

    public LexMatcher not(LexMatcher... matchers) {
        return new NotMatcher(this, javaLexed, matchers);
    }

    public LexMatcher not(LexicalElement... elements) {
        final var matchers = getLexMatchers(elements);
        return not(matchers);
    }

    public LexMatcher not(String string) {
        return not(lexer.apply(Collections.singletonList(string)));
    }

    public LexMatcher anyTill(LexMatcher... matchers) {
        return zeroOrMore(not(matchers));
    }

    public LexMatcher anyTill(LexicalElement... elements) {
        final var matchers = getLexMatchers(elements);
        return anyTill(matchers);
    }

    public LexMatcher anyTill(String string) {
        return anyTill(lexer.apply(Collections.singletonList(string)));
    }

    //<editor-fold id="methods">
    public LexMatcher modifier(GroupNameWrapper nameWrapper, int mask) {
        return group(nameWrapper.toString(),modifier(mask));
    }
    public LexMatcher keyword(GroupNameWrapper nameWrapper, String id) {
        return group(nameWrapper.toString(),keyword(id));
    }
    public LexMatcher oneOf(GroupNameWrapper nameWrapper, LexMatcher... matchers) {
        return group(nameWrapper.toString(),oneOf(matchers));
    }
    public LexMatcher zeroOrMore(GroupNameWrapper nameWrapper, LexMatcher matcher) {
        return group(nameWrapper.toString(),zeroOrMore(matcher));
    }
    public LexMatcher zeroOrMore(GroupNameWrapper nameWrapper, String string) {
        return group(nameWrapper.toString(),zeroOrMore(string));
    }
    public LexMatcher optional(GroupNameWrapper nameWrapper, LexMatcher matcher) {
        return group(nameWrapper.toString(),optional(matcher));
    }
    public LexMatcher optional(GroupNameWrapper nameWrapper, String string) {
        return group(nameWrapper.toString(),optional(string));
    }
    public LexMatcher oneOrMore(GroupNameWrapper nameWrapper, LexMatcher matcher) {
        return group(nameWrapper.toString(),oneOrMore(matcher));
    }
    public LexMatcher oneOrMore(GroupNameWrapper nameWrapper, String string) {
        return group(nameWrapper.toString(),oneOrMore(string));
    }
    public LexMatcher repeat(GroupNameWrapper nameWrapper, LexMatcher matcher, int times) {
        return group(nameWrapper.toString(),repeat(matcher, times));
    }
    public LexMatcher repeat(GroupNameWrapper nameWrapper, LexMatcher matcher, int min, int max) {
        return group(nameWrapper.toString(),repeat(matcher, min, max));
    }
    public LexMatcher integerNumber(GroupNameWrapper nameWrapper) {
        return group(nameWrapper.toString(),integerNumber());
    }
    public LexMatcher integerNumber(GroupNameWrapper nameWrapper, Predicate<Long> predicate) {
        return group(nameWrapper.toString(),integerNumber(predicate));
    }
    public LexMatcher number(GroupNameWrapper nameWrapper) {
        return group(nameWrapper.toString(),number());
    }
    public LexMatcher number(GroupNameWrapper nameWrapper, Predicate<Number> predicate) {
        return group(nameWrapper.toString(),number(predicate));
    }
    public LexMatcher floatNumber(GroupNameWrapper nameWrapper) {
        return group(nameWrapper.toString(),floatNumber());
    }
    public LexMatcher floatNumber(GroupNameWrapper nameWrapper, Predicate<Double> predicate) {
        return group(nameWrapper.toString(),floatNumber(predicate));
    }
    public LexMatcher list(GroupNameWrapper nameWrapper, String... strings) {
        return group(nameWrapper.toString(),list(strings));
    }
    public LexMatcher list(GroupNameWrapper nameWrapper, LexMatcher... matchers) {
        return group(nameWrapper.toString(),list(matchers));
    }
    public LexMatcher match(GroupNameWrapper nameWrapper, String string) {
        return group(nameWrapper.toString(),match(string));
    }
    public LexMatcher unordered(GroupNameWrapper nameWrapper, LexMatcher... matchers) {
        return group(nameWrapper.toString(),unordered(matchers));
    }
    public LexMatcher unordered(GroupNameWrapper nameWrapper, LexicalElement... elements) {
        return group(nameWrapper.toString(),unordered(elements));
    }
    public LexMatcher unordered(GroupNameWrapper nameWrapper, String string) {
        return group(nameWrapper.toString(),unordered(string));
    }
    public LexMatcher group(GroupNameWrapper nameWrapper, String name, LexMatcher matcher) {
        return group(nameWrapper.toString(),group(name, matcher));
    }
    public LexMatcher oneOf(GroupNameWrapper nameWrapper, String... strings) {
        return group(nameWrapper.toString(),oneOf(strings));
    }
    public LexMatcher not(GroupNameWrapper nameWrapper, LexMatcher... matchers) {
        return group(nameWrapper.toString(),not(matchers));
    }
    public LexMatcher not(GroupNameWrapper nameWrapper, LexicalElement... elements) {
        return group(nameWrapper.toString(),not(elements));
    }
    public LexMatcher not(GroupNameWrapper nameWrapper, String string) {
        return group(nameWrapper.toString(),not(string));
    }
    public LexMatcher anyTill(GroupNameWrapper nameWrapper, LexMatcher... matchers) {
        return group(nameWrapper.toString(),anyTill(matchers));
    }
    public LexMatcher anyTill(GroupNameWrapper nameWrapper, LexicalElement... elements) {
        return group(nameWrapper.toString(),anyTill(elements));
    }
    public LexMatcher anyTill(GroupNameWrapper nameWrapper, String string) {
        return group(nameWrapper.toString(),anyTill(string));
    }
    public LexMatcher identifier(GroupNameWrapper nameWrapper) {
        return group(nameWrapper.toString(),identifier());
    }
    public LexMatcher identifier(GroupNameWrapper nameWrapper, String text) {
        return group(nameWrapper.toString(),identifier(text));
    }
    public LexMatcher identifier(GroupNameWrapper nameWrapper, Pattern pattern) {
        return group(nameWrapper.toString(),identifier(pattern));
    }
    public LexMatcher identifier(GroupNameWrapper nameWrapper, String name, Pattern pattern) {
        return group(nameWrapper.toString(),identifier(name, pattern));
    }
    public LexMatcher character(GroupNameWrapper nameWrapper) {
        return group(nameWrapper.toString(),character());
    }
    public LexMatcher character(GroupNameWrapper nameWrapper, String text) {
        return group(nameWrapper.toString(),character(text));
    }
    public LexMatcher character(GroupNameWrapper nameWrapper, Pattern pattern) {
        return group(nameWrapper.toString(),character(pattern));
    }
    public LexMatcher character(GroupNameWrapper nameWrapper, String name, Pattern pattern) {
        return group(nameWrapper.toString(),character(name, pattern));
    }
    public LexMatcher string(GroupNameWrapper nameWrapper) {
        return group(nameWrapper.toString(),string());
    }
    public LexMatcher string(GroupNameWrapper nameWrapper, String text) {
        return group(nameWrapper.toString(),string(text));
    }
    public LexMatcher string(GroupNameWrapper nameWrapper, Pattern pattern) {
        return group(nameWrapper.toString(),string(pattern));
    }
    public LexMatcher string(GroupNameWrapper nameWrapper, String name, Pattern pattern) {
        return group(nameWrapper.toString(),string(name, pattern));
    }
    public LexMatcher type(GroupNameWrapper nameWrapper) {
        return group(nameWrapper.toString(),type());
    }
    public LexMatcher type(GroupNameWrapper nameWrapper, String text) {
        return group(nameWrapper.toString(),type(text));
    }
    public LexMatcher type(GroupNameWrapper nameWrapper, Pattern pattern) {
        return group(nameWrapper.toString(),type(pattern));
    }
    public LexMatcher type(GroupNameWrapper nameWrapper, String name, Pattern pattern) {
        return group(nameWrapper.toString(),type(name, pattern));
    }
    public LexMatcher comment(GroupNameWrapper nameWrapper) {
        return group(nameWrapper.toString(),comment());
    }
    public LexMatcher comment(GroupNameWrapper nameWrapper, String text) {
        return group(nameWrapper.toString(),comment(text));
    }
    public LexMatcher comment(GroupNameWrapper nameWrapper, Pattern pattern) {
        return group(nameWrapper.toString(),comment(pattern));
    }
    public LexMatcher comment(GroupNameWrapper nameWrapper, String name, Pattern pattern) {
        return group(nameWrapper.toString(),comment(name, pattern));
    }
    public LexMatcher identifier() {
        return new IdentifierMatcher(this, javaLexed);
    }

    public LexMatcher identifier(String text) {
        return new IdentifierMatcher(this, javaLexed, text);
    }

    public LexMatcher identifier(Pattern pattern) {
        return new IdentifierMatcher(this, javaLexed, pattern);
    }

    public LexMatcher identifier(String name, Pattern pattern) {
        return new IdentifierMatcher(this, javaLexed, pattern, name);
    }

    public LexMatcher character() {
        return new CharacterMatcher(this, javaLexed);
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

    public LexMatcher string() {
        return new StringMatcher(this, javaLexed);
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

    public LexMatcher type() {
        return new TypeMatcher(this, javaLexed);
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

    public LexMatcher comment() {
        return new CommentMatcher(this, javaLexed);
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

    //</editor-fold>

    private LexMatcher getMatcher(String string) {
        final var lexicalElements = lexer.apply(Collections.singletonList(string));
        if (lexicalElements.length == 1) {
            return terminal(lexicalElements[0]);
        } else {
            return list(lexicalElements);
        }
    }

    private LexMatcher[] getLexMatchers(LexicalElement[] elements) {
        final var matchers = new LexMatcher[elements.length];
        for (int i = 0; i < elements.length; i++) {
            matchers[i] = terminal(elements[i]);
        }
        return matchers;
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
