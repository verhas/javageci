package javax0.geci.lexeger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        final var sb = new StringBuilder();
        try (final var sut = new JavaLexed(source)) {
            for (final var le : sut.lexicalElements()) {
                sb.append(le.getType().name()).append("[")
                    .append(le.getFullLexeme()).append("]").append("\n");
            }
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
                                    "SPACING[ ]\n", sb.toString());
    }
}
