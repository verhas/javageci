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
import java.util.function.Predicate;

@AnnotationBuilder
public class Jdocify extends AbstractJavaGenerator {

    private static class Config {
        private boolean processAllClasses = true;
        private String commentCODEStart = "<!--CODE";
        private String commentCODEEnd = "-->";
    }

    @Override
    protected boolean processAllClasses() {
        return config.processAllClasses;
    }

    /**
     * Modify the comment if there is any inconsistency in the code.
     *
     * @param comment the content of the comment to be modified
     * @param klass   the klass to fetch the reference values from via reflection
     * @return {@code true} if the comment was modified.
     */
    private boolean modifyComment(final StringBuilder comment, final Source source, final Class<?> klass) {
        boolean changed = false;
        int pos = 0;
        int start;
        final int lenCODEStart = config.commentCODEStart.length();
        int lenCODEEnd = config.commentCODEEnd.length();
        while ((start = comment.indexOf(config.commentCODEStart, pos)) > -1) {
            int commentEnd = comment.indexOf(config.commentCODEEnd, start + lenCODEStart);
            if (commentEnd == -1) {
                throw new GeciException("There is no --> after the <!--CODE in the file '" +
                    source.getAbsoluteFile() + "'");
            }
            if (commentEnd + lenCODEEnd + "{@code".length() >= comment.length()) {
                throw new GeciException("There is no {@code ... following the <!--CODE ...--> in the file '" +
                    source.getAbsoluteFile() + "'");
            }
            int fieldNameStart = findPosition(comment, start + lenCODEStart, commentEnd, Jdocify::separatorCharacter);
            int fieldNameEnd = findPosition(comment, fieldNameStart + 1, commentEnd, Predicate.not(Jdocify::separatorCharacter));
            final String fieldName = comment.substring(fieldNameStart, fieldNameEnd);
            String fieldValue = fetchFieldValue(klass, fieldName);
            if (fieldValue == null) {
                throw new GeciException("In the source '" + source.getAbsoluteFile() + "' there is a " +
                    config.commentCODEStart + fieldName + config.commentCODEEnd +
                    " reference, but the field cannot be found, is not static or not final.");
            }
            int templateStart = findPosition(comment, fieldNameEnd, commentEnd, Jdocify::separatorCharacter);
            final String template;
            if (templateStart < commentEnd) {
                template = comment.substring(templateStart, commentEnd);
            } else {
                template = fieldName;
            }
            int codeContentStart = findPosition(comment, commentEnd + lenCODEEnd + "{@code".length(),
                comment.length(), Jdocify::separatorCharacter);
            if (codeContentStart == comment.length()) {
                throw new GeciException("{@code is not finished before the end of the comment in '" +
                    source.getAbsoluteFile() + "'");
            }
            int codeContentEnd = findCodeEnd(comment, codeContentStart, comment.length());
            if (codeContentEnd == comment.length()) {
                throw new GeciException("{@code is not closed in a JavaDoc comment " +
                    "following a <!--CODE ...--> in the file '" + source.getAbsoluteFile() + "'");
            }
            final String newContent = getNewContent(comment, fieldName, fieldValue, template, codeContentStart);
            if (!isBalanced(newContent)) {
                throw new GeciException("The value to be inserted after {@code is not balanced, " +
                    "has different number of { and } characters in the file'" +
                    source.getAbsoluteFile() + "'. The actual value is: '" + newContent + "'");
            }
            if (!areEqual(comment.substring(codeContentStart, codeContentEnd), newContent)) {
                comment.delete(codeContentStart, codeContentEnd).insert(codeContentStart, newContent);
                changed = true;
            }
            pos = codeContentEnd + 1;
        }
        return changed;
    }

    /**
     * Replace the name with the value in the template and add an extra space in front of them in case there is no space
     * before the current value. This can only happen when the code looks like { @ {@code code} } without any spaces in
     * between.
     *
     * @param comment  the whole comment
     * @param name     the name of the parameter to be replaced in the template
     * @param value    the value that has to be used in the template
     * @param template the template
     * @param start    the position in the comment where the old value starts
     * @return the new content that has to replace the old one. Possibly the same as the old one.
     */
    private String getNewContent(StringBuilder comment, String name, String value, String template, int start) {
        final String separator;
        if (start > 0 && !Character.isWhitespace(comment.charAt(start - 1))) {
            separator = " ";
        } else {
            separator = "";
        }
        return separator + template.replace(name, value);
    }

    /**
     * Compare the code and the replacement. If the string in the code, that comes from the source file as it is now
     * berween the {@code code} keyword and the closing brace is the same as the replacement then we do not need to
     * replace the code and the source may remain intact.
     *
     * <p>
     * The trick in the comparison is that the code as it is now and formatted may contain {@code' \n * '} parts that
     * can match a single space in the replacement.
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
     * counting the openign and closing { and } characters between.
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
     * Fetch the value of the field if it is a declared or inherited {@code final static} field.
     *
     * <p>
     * <p>
     * If there is no such field then we return null.
     *
     * @param klass     the class in which we are looking for the field
     * @param fieldName the name of the field we need the value of
     * @return the value of the field converted to String or null in case there in no appropriate field
     */
    private static String fetchFieldValue(Class<?> klass, String fieldName) {
        try {
            final var field = GeciReflectionTools.getField(klass, fieldName);
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                field.setAccessible(true);
                return "" + field.get(null);
            }
        } catch (Exception e) {
        }
        return null;
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

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        try (final var lexed = new JavaLexed(source)) {
            for (final var lex : lexed.lexicalElements()) {
                if (lex.getType() == LexicalElement.Type.COMMENT) {
                    final var comment = new StringBuilder(lex.getLexeme());
                    if (modifyComment(comment, source, klass)) {
                        lex.setLexeme(comment.toString());
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
        "commentCODEEnd",
        "commentCODEStart",
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }

    public class Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder commentCODEEnd(String commentCODEEnd) {
            config.commentCODEEnd = commentCODEEnd;
            return this;
        }

        public Builder commentCODEStart(String commentCODEStart) {
            config.commentCODEStart = commentCODEStart;
            return this;
        }

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
        local.commentCODEEnd = params.get("commentCODEEnd", config.commentCODEEnd);
        local.commentCODEStart = params.get("commentCODEStart", config.commentCODEStart);
        local.processAllClasses = config.processAllClasses;
        return local;
    }
    //</editor-fold>
}
