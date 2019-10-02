package javax0.geci.lexeger;

import java.util.List;
import javax0.geci.javacomparator.lex.LexicalElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestJavaLexed {

    void testNullTransformation(List<String> lines) {
        final var source = new TestSource(lines);
        final var expected = source.toString();
        try (final var sut = new JavaLexed(source)) {
            // do nothing, no transformation on lexical level
        }
        final var result = source.toString();
        Assertions.assertEquals(expected, result);
    }

    private String buildLexemeTree(JavaLexed sut) {
        StringBuilder sb = new StringBuilder();
        for (final var le : sut.lexicalElements()) {
            sb.append(le.getType().name()).append("[")
                .append(le.getFullLexeme()).append("]").append("\n");
        }
        return sb.toString();
    }

    @Test
    void testEmptyNullTransformation() {
        testNullTransformation(List.of(""));
    }

    @Test
    void testOneLinerNullTransformation() {
        testNullTransformation(List.of("private final var apple= \"appleee\";"));
    }

    @Test
    void testMultiLinerNullTransformation() {
        testNullTransformation(List.of(
            "private final var apple= \"appleee\";",
            "private final final 13 'aaaaa' "));
    }

    @Test
    void testMultiLinerSpaceNewLineSpaceNullTransformation() {
        testNullTransformation(List.of(
            "private final var apple= \"appleee\";   ",
            "    private final final 13 'aaaaa' "));
    }


    @Test
    void iterateThrough() {
        final var source = new TestSource(List.of(
            "private final var apple= \"appleee\";   ",
            "    private final final 13 'aaaaa' "));
        String lexed;
        try (final var sut = new JavaLexed(source)) {
            lexed = buildLexemeTree(sut);
        }
        Assertions.assertEquals("IDENTIFIER[private]\n" +
                                    "SPACING[ ]\n" +
                                    "IDENTIFIER[final]\n" +
                                    "SPACING[ ]\n" +
                                    "IDENTIFIER[var]\n" +
                                    "SPACING[ ]\n" +
                                    "IDENTIFIER[apple]\n" +
                                    "SYMBOL[=]\n" +
                                    "SPACING[ ]\n" +
                                    "STRING[\"appleee\"]\n" +
                                    "SYMBOL[;]\n" +
                                    "SPACING[   \n" +
                                    "    ]\n" +
                                    "IDENTIFIER[private]\n" +
                                    "SPACING[ ]\n" +
                                    "IDENTIFIER[final]\n" +
                                    "SPACING[ ]\n" +
                                    "IDENTIFIER[final]\n" +
                                    "SPACING[ ]\n" +
                                    "INTEGER[13]\n" +
                                    "SPACING[ ]\n" +
                                    "CHARACTER['aaaaa']\n" +
                                    "SPACING[ ]\n", lexed);
    }

    @Test
    @DisplayName("JavaLexed can't be used after being closed.")
    void cantBeUsedOutOfScope() {
        final var source = new TestSource(List.of(""));
        final var sut = new JavaLexed(source);
        try {
        } finally {
            sut.close();
        }
        Assertions.assertThrows(IllegalArgumentException.class, sut::lexicalElements);
    }

    @Test
    @DisplayName("Can't remove range with bigger start than end.")
    void removeRangeThrowsExceptionWhenWrongStartEndOrder() {
        final var source = new TestSource(List.of(""));
        try(final var sut = new JavaLexed(source)) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> sut.removeRange(2, 1));
        }
    }

    @Test
    @DisplayName("Can remove a single lexical element.")
    void removesElement() {
        final var source = new TestSource(List.of("public static final"));
        String lexed;
        try(final var sut = new JavaLexed(source)) {
            sut.remove(4);
            lexed = buildLexemeTree(sut);
        }
        Assertions.assertEquals("IDENTIFIER[public]\n" +
            "SPACING[ ]\n" +
            "IDENTIFIER[static]\n" +
            "SPACING[ ]\n", lexed);
    }

    @Test
    @DisplayName("Can get a single lexical element by id.")
    void getsElement() {
        final var source = new TestSource(List.of("public static final"));
        try(final var sut = new JavaLexed(source)) {
            final javax0.geci.javacomparator.LexicalElement lexicalElement = sut.get(2);
            Assertions.assertEquals(javax0.geci.javacomparator.LexicalElement.Type.IDENTIFIER, lexicalElement.getType());
            Assertions.assertEquals("static", lexicalElement.getFullLexeme());
        }
    }

    @Test
    @DisplayName("Can remove a range of lexical elements.")
    void removesRange() {
        final var source = new TestSource(List.of("final private"));
        String lexed;
        try(final var sut = new JavaLexed(source)) {
            sut.removeRange(0, 2);
            lexed = buildLexemeTree(sut);
        }
        Assertions.assertEquals("IDENTIFIER[private]\n", lexed);
    }

    @Test
    @DisplayName("Can replace a range of lexical elements.")
    void replacesRange() {
        final var source = new TestSource(List.of("final private"));
        String lexed;
        try(final var sut = new JavaLexed(source)) {
            sut.replace(0, 2, List.of(new LexicalElement.Identifier("static"), new LexicalElement.Spacing("   ")));
            lexed = buildLexemeTree(sut);
        }
        Assertions.assertEquals("IDENTIFIER[static]\n" +
            "SPACING[   ]\n" +
            "IDENTIFIER[private]\n", lexed);
    }

    @Test
    @DisplayName("Can replace a range of lexical elements with the Lex utility class.")
    void replacesRangeWithLex() {
        final var source = new TestSource(List.of("final private"));
        String lexed;
        try(final var sut = new JavaLexed(source)) {
            sut.replace(0, 2, Lex.of("static   "));
            lexed = buildLexemeTree(sut);
        }
        Assertions.assertEquals("IDENTIFIER[static]\n" +
            "SPACING[   ]\n" +
            "IDENTIFIER[private]\n", lexed);
    }
}
