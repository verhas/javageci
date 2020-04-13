package javax0.geci.jdocify;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.javacomparator.LexicalElement;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@AnnotationBuilder
public class Jdocify extends AbstractJavaGenerator {
    final Map<String, String> defines = new HashMap<>();
    final int CODE_LENGTH = "{@code".length();
    private static final String HTML_COMMENT_START = "<!--";
    private static final int HTML_COMMENT_START_LEN = HTML_COMMENT_START.length();
    private static final String HTML_COMMENT_END = "-->";
    private static final int HTML_COMMENT_END_LEN = "-->".length();
    private static final String VARIABLE_REGEX = "[A-Z_][A-Z0-9_]*";
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(VARIABLE_REGEX);
    private static final Pattern DEFINE_PATTERN = Pattern.compile("DEFINE\\s+(" + VARIABLE_REGEX + ")\\s*=(.*)");
    private static final String COMMENT_CODE_START = HTML_COMMENT_START + "CODE";
    private static final String REF_END = HTML_COMMENT_START + "/" + HTML_COMMENT_END;
    private static final int REF_END_LEN = REF_END.length();

    private static class Config {
        private boolean processAllClasses = false;
    }

    @Override
    protected boolean processAllClasses() {
        return config.processAllClasses;
    }


    /**
     * The state, mainly indexes in side the comment StringBuilder during a single replace.
     */
    private static class State {
        int pos;
        int lenCODEStart;
        int lenCODEEnd;
        int commentStart;
        int fieldNameStart;
        int fieldNameEnd;
        int templateStart;
        int contentStart;
        int contentEnd;
        int commentEnd;
        String template;
        String fieldName;
        String fieldValue;
        String newContent;
        boolean changed;
        StringBuilder comment;
    }

    private boolean modifyDefinesInComment(StringBuilder comment) {
        final State state = new State();
        state.pos = 0;
        state.comment = comment;
        while ((state.commentStart = state.comment.indexOf(HTML_COMMENT_START, state.pos)) > -1) {
            state.commentEnd = state.comment.indexOf(HTML_COMMENT_END, state.commentStart);
            findCommentEnd(state);
            final var variable = state.comment.substring(state.commentStart + HTML_COMMENT_START_LEN, state.commentEnd);
            if (VARIABLE_PATTERN.matcher(variable).matches()) {
                if (defines.containsKey(variable)) {
                    state.contentStart = state.commentEnd + HTML_COMMENT_END_LEN;
                    state.contentEnd = state.comment.indexOf(REF_END, state.contentStart);
                    state.newContent = defines.get(variable);
                    if (contentNeedsReplacement(state)) {
                        replaceOldContentWithNew(state);
                    }
                    state.pos = state.contentEnd + REF_END_LEN;
                } else {
                    throw new GeciException("The variable '" +
                        variable + "' was referenced in JavaDoc HTML comment, but was not DEFINEd");
                }
            } else {
                state.pos = state.commentEnd + HTML_COMMENT_END_LEN;
            }
        }
        return state.changed;
    }

    /**
     * Modify the comment if there is any inconsistency in the code.
     *
     * @param comment the content of the comment to be modified
     * @param klass   the klass to fetch the reference values from via reflection
     * @return {@code true} if the comment was modified.
     */
    private boolean modifyCodesInComment(final StringBuilder comment, final Class<?> klass) {
        final State state = new State();
        state.comment = comment;
        state.changed = false;
        state.pos = 0;

        state.lenCODEStart = COMMENT_CODE_START.length();
        state.lenCODEEnd = HTML_COMMENT_END.length();

        while ((state.commentStart = state.comment.indexOf(COMMENT_CODE_START, state.pos)) > -1) {
            findCommentEnd(state);
            assertCodeAndCommentSyntax(state);
            getFieldNameAndValue(klass, state);
            getTemplate(state);
            getCurrentContentPositions(state);
            getNewContent(state);
            if (contentNeedsReplacement(state)) {
                replaceOldContentWithNew(state);
            }
            state.pos = state.contentEnd + 1;
        }
        return state.changed;
    }

    /**
     * Set the field {@code commentEnd} to the position where the comment ends finding the {@code -->} string following
     * the last found {@code <!--CODE}.
     *
     * @param state <!--STATE-->the state of the processing, see {@link State}<!--/-->
     */
    private void findCommentEnd(State state) {
        state.commentEnd = state.comment.indexOf(HTML_COMMENT_END, state.commentStart);
    }

    /**
     * @param state the state of the processing, see {@link State}
     * @return {@code true} if the old content is not the same as the new content
     */
    private boolean contentNeedsReplacement(State state) {
        return !areEqual(state.comment.substring(state.contentStart, state.contentEnd), state.newContent);
    }

    private void replaceOldContentWithNew(State state) {
        state.comment.delete(state.contentStart, state.contentEnd).insert(state.contentStart, state.newContent);
        state.changed = true;
    }

    private void getCurrentContentPositions(State state) {
        state.contentStart = findPosition(state.comment, state.commentEnd + state.lenCODEEnd + CODE_LENGTH,
            state.comment.length(), Jdocify::separatorCharacter);
        asserts(state.contentStart == state.comment.length(),
            "{@code is not finished before the end of the comment.");

        state.contentEnd = findCodeEnd(state.comment, state.contentStart, state.comment.length());
        asserts(state.contentEnd == state.comment.length(),
            "{@code is not closed in a JavaDoc comment " +
                "following a <!--CODE ...-->");
    }

    private void getTemplate(State state) {
        state.templateStart = findPosition(state.comment, state.fieldNameEnd, state.commentEnd, Jdocify::separatorCharacter);
        if (state.templateStart < state.commentEnd) {
            state.template = state.comment.substring(state.templateStart, state.commentEnd);
        } else {
            state.template = state.fieldName;
        }
    }

    private void assertCodeAndCommentSyntax(State state) {
        asserts(state.commentEnd == -1,
            "There is no --> after the <!--CODE");
        asserts(state.commentEnd + state.lenCODEEnd + CODE_LENGTH >= state.comment.length(),
            "There is no {@code ... following the <!--CODE ...-->");
    }

    /**
     * Calculate the file name and the value.
     *
     * @param klass the class we are processing
     * @param state the state of the processing, see {@link State}
     */
    private void getFieldNameAndValue(Class<?> klass, State state) {
        state.fieldNameStart = findPosition(state.comment, state.commentStart + state.lenCODEStart, state.commentEnd, Jdocify::separatorCharacter);
        state.fieldNameEnd = findPosition(state.comment, state.fieldNameStart + 1, state.commentEnd, ch -> !separatorCharacter(ch));
        state.fieldName = state.comment.substring(state.fieldNameStart, state.fieldNameEnd);
        state.fieldValue = fetchFieldValue(klass, state.fieldName);
        asserts(state.fieldValue == null,
            "There is a " + COMMENT_CODE_START + state.fieldName + HTML_COMMENT_END +
                " reference, but the field cannot be found, is not static or not final.");
    }

    /**
     * @param flag    if this flag is {@code true} then throw a new {@link GeciException} with the message.
     * @param message the text in the exception
     */
    private static void asserts(boolean flag, String message) {
        if (flag) {
            throw new GeciException(message);
        }
    }

    /**
     * Replace the name with the value in the template and add an extra space in front of them in case there is no space
     * before the current value. This can only happen when the code looks like { @ {@code code} } without any spaces in
     * between.
     *
     * @param state the state of the processing, see {@link State}
     */
    private void getNewContent(State state) {
        final String separator;
        if (state.contentStart > 0 && !Character.isWhitespace(state.comment.charAt(state.contentStart - 1))) {
            separator = " ";
        } else {
            separator = "";
        }
        state.newContent = separator + state.template.replace(state.fieldName, state.fieldValue);
        asserts(!isBalanced(state.newContent),
            "The value to be inserted after {@code is not balanced, " +
                "has different number of { and } characters. The actual value is: '" + state.newContent + "'");
    }

    /**
     * <p>Compare the code and the replacement. If the string in the code, that comes from the source file as it is now
     * between the {@code code} keyword, and the closing brace is the same as the replacement then we do not need to
     * replace the code, and the source may remain intact.</p>
     *
     * <p>The trick in the comparison is that the code as it is now and formatted may contain {@code ' \n * '} parts that
     * can match a single space in the replacement.</p>
     *
     * @param code        the text in the JavaDoc code
     * @param replacement the text it should be
     * @return {@code true} if the two texts match
     */
    private boolean areEqual(final String code, final String replacement) {
        int ci = 0;
        int ri = 0;
        while (ci < code.length() && ri < replacement.length()) {
            if (code.substring(ci).startsWith("\n * ")) {
                if (!Character.isWhitespace(replacement.charAt(ri))) {
                    return false;
                }
                ri++;
                ci += 4;
                continue;
            }
            if (Character.isWhitespace(code.charAt(ci)) && Character.isWhitespace(replacement.charAt(ri))) {
                ri++;
                ci++;
                continue;
            }
            if (code.charAt(ci) == replacement.charAt(ri)) {
                ci++;
                ri++;
                continue;
            }
            return false;
        }
        return ci == code.length() && ri == replacement.length();
    }

    private int findPosition(StringBuilder comment, int start, int commentEnd, Predicate<Character> isSepa) {
        int pos = start;
        while (pos < commentEnd && isSepa.test(comment.charAt(pos))) {
            pos++;
        }
        return pos;
    }

    /**
     * Returns true if there are the same number of { character in the string as } characters.
     *
     * @param newContent the text to check
     * @return {@code true} if the { and } characters are balanced in the string.
     */
    private boolean isBalanced(final String newContent) {
        int open = 0;
        int close = 0;
        for (char ch : newContent.toCharArray()) {
            if (ch == '{') open++;
            if (ch == '}') close++;
        }
        return open == close;
    }

    /**
     * Find the end of the started {@code {@code ...}} sequence. This is essentially finding the next } character with
     * counting the opening and closing { and } characters between.
     *
     * @param comment    the comment that contains the code javadoc directive
     * @param start      where the {@code XXX} starts in the {@code {@code XXX}} structure
     * @param commentEnd the length of the whole comment not run out of the buffer
     * @return the position of the closing }
     */
    private int findCodeEnd(StringBuilder comment, int start, int commentEnd) {
        int braceCounter = 0;
        int codeEnd = start;
        while (codeEnd < commentEnd && (braceCounter > 0 || comment.charAt(codeEnd) != '}')) {
            if (comment.charAt(codeEnd) == '}') {
                braceCounter--;
            }
            if (comment.charAt(codeEnd) == '{') {
                braceCounter++;
            }
            codeEnd++;
        }
        return codeEnd;
    }

    /**
     * <p>Fetch the value of the field if it is a declared or inherited {@code final static} field.</p>
     *
     *  <p>If there is no such field then we return null.</p>
     *
     * @param klass     the class in which we are looking for the field
     * @param fieldName the name of the field we need the value of
     * @return the value of the field converted to String or {@code null} in case there in no appropriate field
     */
    private static String fetchFieldValue(Class<?> klass, String fieldName) {
        try {
            final var field = GeciReflectionTools.getField(klass, fieldName);
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                field.setAccessible(true);
                return "" + field.get(null);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Any space character is a separator and also {@code *} because in JavaDoc the lines start with a {@code *}
     * character and the
     *
     * <pre>{@code
     *    * <!--CODE
     *    * .... -->{@code
     *    * ...}
     *
     * }</pre>
     * <p>
     * sequence may be split into several lines.
     *
     * @param ch the character to judge
     * @return {@code true} if it is a whitespace or {@code *}
     */
    private static boolean separatorCharacter(char ch) {
        return Character.isWhitespace(ch) || ch == '*';
    }

    private void define(String lexeme) {
        final var m = DEFINE_PATTERN.matcher(lexeme.substring(2));
        if (m.matches()) {
            defines.put(m.group(1), m.group(2));
        }
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) {
        defines.clear();
        try (final var lexed = new JavaLexed(source)) {
            for (final var lex : lexed.lexicalElements()) {
                if (lex.getType() == LexicalElement.Type.COMMENT) {
                    if (lex.getLexeme().startsWith("//")) {
                        define(lex.getLexeme());
                    } else {
                        final var comment = new StringBuilder(lex.getLexeme());
                        final var code = modifyCodesInComment(comment, klass);
                        final var def = modifyDefinesInComment(comment);
                        if (code || def) {
                            lex.setLexeme(comment.toString());
                        }
                    }
                }
            }
        }
    }


    //<editor-fold id="configBuilder" configurableMnemonic="jdocify">
    private String configuredMnemonic = "jdocify";

    @Override
    public String mnemonic() {
        return configuredMnemonic;
    }

    private final Config config = new Config();

    public static Jdocify.Builder builder() {
        return new Jdocify().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }

    public class Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder processAllClasses(boolean processAllClasses) {
            config.processAllClasses = processAllClasses;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public Jdocify build() {
            return Jdocify.this;
        }
    }

    private Config localConfig(CompoundParams params) {
        final var local = new Config();
        local.processAllClasses = config.processAllClasses;
        return local;
    }
    //</editor-fold>
}
