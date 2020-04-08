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

@AnnotationBuilder
public class Jdocify extends AbstractJavaGenerator {

    private static class Config {
        private boolean processAllClasses = true;
        private String commentCODEStart = "<!--CODE ";
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
        int pos = 0;
        int start;
        final int lenCODEStart = config.commentCODEStart.length();
        int lenCODEEnd = config.commentCODEEnd.length();
        while ((start = comment.indexOf(config.commentCODEStart, pos)) > -1) {
            int commentEnd = comment.indexOf(config.commentCODEEnd, start + lenCODEStart);
            if (commentEnd > -1) {
                int fieldNameStart = start + lenCODEStart;
                while (fieldNameStart < commentEnd && separatorCharacter(comment.charAt(fieldNameStart))) {
                    fieldNameStart++;
                }
                int filedNameEnd = fieldNameStart + 1;
                while (filedNameEnd < commentEnd && !separatorCharacter(comment.charAt(filedNameEnd))) {
                    filedNameEnd++;
                }
                final String fieldName = comment.substring(fieldNameStart, filedNameEnd);
                String fieldValue = fetchFieldValue(klass, fieldName);
                if (fieldValue == null) {
                    throw new GeciException("In the source '" + source.getAbsoluteFile() + "' there is a " +
                        config.commentCODEStart + fieldName + config.commentCODEEnd +
                        " reference, but the field cannot be found, is not static or not final.");
                }
            } else {
                pos = start + lenCODEStart;
            }
        }
        return false;
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
