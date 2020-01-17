package javax0.geci.lexeger;

import javax0.geci.javacomparator.lex.LexicalElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private String toLexicalString(JavaLexed javaLexed) {
        StringBuilder sb = new StringBuilder();
        for (final var le : javaLexed.lexicalElements()) {
            sb.append(le.getType().name()).append("[")
                .append(le.getFullLexeme()).append("]").append("\n");
        }
        return sb.toString();
    }

    @Test
    void testEmptyNullTransformation() {
        testNullTransformation(Collections.singletonList(""));
    }

    @Test
    void testOneLinerNullTransformation() {
        testNullTransformation(Collections.singletonList("private final var apple= \"appleee\";"));
    }

    @Test
    void testMultiLinerNullTransformation() {
        testNullTransformation(Arrays.asList(
            "private final var apple= \"appleee\";",
            "private final final 13 'aaaaa' "));
    }

    @Test
    void testMultiLinerSpaceNewLineSpaceNullTransformation() {
        testNullTransformation(Arrays.asList(
            "private final var apple= \"appleee\";   ",
            "    private final final 13 'aaaaa' "));
    }


    @Test
    void iterateThrough() {
        final var source = new TestSource(Arrays.asList(
            "private final var apple= \"appleee\";   ",
            "    private final final 13 'aaaaa' "));
        final String lexed;
        try (final var sut = new JavaLexed(source)) {
            lexed = toLexicalString(sut);
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
        final var source = new TestSource("");
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
        final var source = new TestSource("");
        try(final var sut = new JavaLexed(source)) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> sut.removeRange(2, 1));
        }
    }

    @Test
    @DisplayName("Can't remove range if end is out of bounds.")
    void removeRangeThrowsExceptionWhenEndOutOfBounds() {
        final var source = new TestSource("public final");
        try(final var sut = new JavaLexed(source)) {
            Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sut.removeRange(1, 5));
        }
    }

    @Test
    @DisplayName("Can't remove range if start is out of bounds.")
    void removeRangeThrowsExceptionWhenStartOutOfBounds() {
        final var source = new TestSource("public final");
        try(final var sut = new JavaLexed(source)) {
            Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sut.removeRange(-1, 2));
        }
    }

    @Test
    @DisplayName("Can remove a single lexical element.")
    void removesElement() {
        final var source = new TestSource("public static final");
        final String lexed;
        try(final var sut = new JavaLexed(source)) {
            sut.remove(2);
            lexed = toLexicalString(sut);
        }
        Assertions.assertEquals("IDENTIFIER[public]\n" +
            "SPACING[ ]\n" +
            "SPACING[ ]\n" +
            "IDENTIFIER[final]\n", lexed);
    }

    @Test
    @DisplayName("Can't remove element from out of bounds.")
    void removeElementThrowsExceptionOutOfBounds() {
        final var source = new TestSource("public final");
        try(final var sut = new JavaLexed(source)) {
            Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sut.remove(-1));
            Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sut.remove(5));
        }
    }

    @Test
    @DisplayName("Can get a single lexical element by position.")
    void getsElement() {
        final var source = new TestSource("public static final");
        try(final var sut = new JavaLexed(source)) {
            final var lexicalElement = sut.get(2);
            Assertions.assertEquals(javax0.geci.javacomparator.LexicalElement.Type.IDENTIFIER, lexicalElement.getType());
            Assertions.assertEquals("static", lexicalElement.getFullLexeme());
        }
    }

    @Test
    @DisplayName("Can remove a range of lexical elements.")
    void removesRange() {
        final var source = new TestSource("final static private");
        final String lexed;
        try(final var sut = new JavaLexed(source)) {
            sut.removeRange(1, 3);
            lexed = toLexicalString(sut);
        }
        Assertions.assertEquals("IDENTIFIER[final]\n" +
            "SPACING[ ]\n" +
            "IDENTIFIER[private]\n", lexed);
    }

    @Test
    @DisplayName("Can replace a range of lexical elements at the start.")
    void replacesRangeStart() {
        final var source = new TestSource("final private");
        final var source3 = new TestSource("private var");
        final String lexed;
        try(final var sut = new JavaLexed(source)) {
            sut.replace(0, 2, Arrays.asList(new LexicalElement.Identifier("static"), new LexicalElement.Spacing("   ")));
            lexed = toLexicalString(sut);
        }
        Assertions.assertEquals("IDENTIFIER[static]\n" +
            "SPACING[   ]\n" +
            "IDENTIFIER[private]\n", lexed);
    }

    @Test
    @DisplayName("Can replace a range of lexical elements between the start and the end.")
    void replacesRangeMiddle() {
        final var source = new TestSource("public final static var");
        final String lexed;
        try(final var sut = new JavaLexed(source)) {
            sut.replace(2, 4, Arrays.asList(new LexicalElement.Identifier("static"), new LexicalElement.Spacing("   ")));
            lexed = toLexicalString(sut);
        }
        Assertions.assertEquals("IDENTIFIER[public]\n" +
            "SPACING[ ]\n" +
            "IDENTIFIER[static]\n" +
            "SPACING[   ]\n" +
            "IDENTIFIER[static]\n" +
            "SPACING[ ]\n" +
            "IDENTIFIER[var]\n", lexed);
    }

    @Test
    @DisplayName("Can replace a range of lexical elements at the end.")
    void replacesRangeEnd() {
        final var source = new TestSource("public final static");
        final String lexed;
        try(final var sut = new JavaLexed(source)) {
            sut.replace(2, 5, Arrays.asList(new LexicalElement.Identifier("static"), new LexicalElement.Spacing("   ")));
            lexed = toLexicalString(sut);
        }
        Assertions.assertEquals("IDENTIFIER[public]\n" +
            "SPACING[ ]\n" +
            "IDENTIFIER[static]\n" +
            "SPACING[   ]\n", lexed);
    }

    @Test
    @DisplayName("Can replace a range of lexical elements with the Lex utility class.")
    void replacesRangeWithLex() {
        final var source = new TestSource("final private");
        final String lexed;
        try(final var sut = new JavaLexed(source)) {
            sut.replace(0, 2, Lex.of("static   "));
            lexed = toLexicalString(sut);
        }
        Assertions.assertEquals("IDENTIFIER[static]\n" +
            "SPACING[   ]\n" +
            "IDENTIFIER[private]\n", lexed);
    }
}
