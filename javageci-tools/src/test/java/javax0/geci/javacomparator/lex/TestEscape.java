package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class TestEscape {
    @Test
    @DisplayName("Method escape replaces the usual escape sequences to their corresponding character")
    void testUsualEscape() {
        Assertions.assertEquals(Escape.escape("new line\\nline feed\\rtab-character\\t" +
                "backspace\\bform-feed\\fbackslash\\\\" +
                "apostrophe\\'quote\\\""), "new line\nline feed\rtab-character\t" +
                "backspace\bform-feed\fbackslash\\" +
                "apostrophe'quote\"");
    }

    @Test
    @DisplayName("Method escape replaces the octal escape sequences to their corresponding character")
    void testOctalEscape() {
        for (int i = 1; i < 256; i++) {
            int k = i;
            String oct = "";
            while (k > 0) {
                oct = "" + k % 8 + oct;
                k /= 8;
            }
            Assertions.assertEquals(Escape.escape("this is a \\" + oct + " octal char"), "this is a " + Character.toString((char) i) + " octal char");
        }
    }

    @Test
    @DisplayName("Method escape throws an exception when escape sequence is invalid")
    void testInvalidEscape() {
        Assertions.assertThrows(IllegalArgumentException.class,() -> Escape.escape("wuff \\k invalid"));
    }

    @Test
    @DisplayName("Method escape throws an exception when escape char is at the last position")
    void testEscapeAtLastPosition() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Escape.escape("wuff \\"));
    }

    @Test
    @DisplayName("handleNormalCharacter handles normal characters fine")
    void handleNormalCharacter() {
        final StringBuilder sb = new StringBuilder("a");
        final StringBuilder output = new StringBuilder();
        final StringBuilder original = new StringBuilder();
        Escape.handleNormalCharacter(sb, output, original);
        Assertions.assertEquals(sb.length(), 0);
        Assertions.assertEquals(output.toString(), "a");
        Assertions.assertEquals(original.toString(), "a");
    }

    @Test
    @DisplayName("handleNormalCharacter throws exception in case of \n")
    void handleNormalCharacterThrowsUpOnNewLine() {
        final StringBuilder sb = new StringBuilder("\n");
        final StringBuilder output = new StringBuilder();
        final StringBuilder original = new StringBuilder();
        Assertions.assertThrows(GeciException.class,() -> Escape.handleNormalCharacter(sb, output, original));
    }

    @Test
    @DisplayName("handleNormalCharacter throws exception in case of \r")
    void handleNormalCharacterThrowsUpOnLineFeed() {
        final StringBuilder sb = new StringBuilder("\r");
        final StringBuilder output = new StringBuilder();
        final StringBuilder original = new StringBuilder();
        Assertions.assertThrows(GeciException.class,() -> Escape.handleNormalCharacter(sb, output, original));
    }

    @Test
    @DisplayName("handleNormalMultiLineStringCharacter functions as needed")
    void handleNormalMultiLineStringCharacterWorks() {
        final StringBuilder sb = new StringBuilder("a\n\r\nb\r\rc\rd");
        final StringBuilder output = new StringBuilder();
        final StringBuilder original = new StringBuilder();
        while (sb.length() > 0)
            Escape.handleNormalMultiLineStringCharacter(sb, output, original);
        Assertions.assertEquals(sb.length(), 0);
        Assertions.assertEquals(output.toString(), "a\n\nb\nc\nd");
        Assertions.assertEquals(original.toString(), "a\n\r\nb\r\rc\rd");
    }

    @Test
    @DisplayName("Create output creates a StringBuilder when the string is long enough")
    void createOutputCreatesOutput() {
        Assertions.assertEquals(Escape.createOutput(new StringBuilder("aa"), "aa").length(), 0);
    }

    @Test
    @DisplayName("Create output creates throws up if string is short")
    void createOutputThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,() -> Escape.createOutput(new StringBuilder("a"), "a"));
    }

}
