package javax0.geci.javacomparator;

import javax0.geci.api.GeciException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestDeunicode {

    @Test
    @DisplayName("test unicode escape conversions for cases listed in the standard")
    void testSimpleConversionsFromJLS12() {
        final var sut = new Deunicode();
        Assertions.assertEquals("\\\\u2122=\u2122", sut.apply("\\\\u2122=\\u2122"));
        Assertions.assertEquals("\\\\u2122=\u2122", sut.apply("\\\\u2122=\\uu2122"));
        Assertions.assertEquals("\\u005a", sut.apply("\\u005cu005a"));
        Assertions.assertEquals("\\u005a", sut.apply("\\uuuuu005cu005a"));
    }

    @Test
    @DisplayName("Test strings that are erroneous")
    void testErroneousSequences() {
        final var sut = new Deunicode();
        Assertions.assertThrows(GeciException.class, () -> sut.apply("\\u"));
        Assertions.assertThrows(GeciException.class, () -> sut.apply("\\uFFFG"));
        Assertions.assertThrows(GeciException.class, () -> sut.apply("\\uFFF"));
        Assertions.assertThrows(GeciException.class, () -> sut.apply("\\u\n0000"));
    }

    @Test
    @DisplayName("Test some extra corner cases")
    void tetsCornerCases() {
        final var sut = new Deunicode();
        Assertions.assertEquals("\\", sut.apply("\\"));
        Assertions.assertEquals("\\t", sut.apply("\\t"));

    }
}
