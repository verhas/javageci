package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestEscape {
    @Test
    @DisplayName("Method escape replaces the usual escape sequences to their corresponding character")
    void testUsualEscape() {
        assertThat(Escape.escape("new line\\nline feed\\rtab-character\\t" +
                "backspace\\bform-feed\\fbackslash\\\\" +
                "apostrophe\\'quote\\\""))
                .isEqualTo("new line\nline feed\rtab-character\t" +
                        "backspace\bform-feed\fbackslash\\" +
                        "apostrophe'quote\"");
    }

    @Test
    @DisplayName("Method escape replaces the ocatal escape sequences to their corresponding character")
    void testOctalEscape() {
        for (int i = 1; i < 256; i++) {
            int k = i;
            String oct = "";
            while (k > 0) {
                oct = "" + k % 8 + oct;
                k /= 8;
            }
            assertThat(Escape.escape("this is a \\" + oct + " octal char"))
                    .isEqualTo("this is a " + Character.toString((char)i) + " octal char");
        }
    }

    @Test
    @DisplayName("Method escape throws an exception when escape sequence is invalid")
    void testInvalidEscape() {
        assertThatThrownBy(() -> Escape.escape("wuff \\k invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Method escape throws an exception when escape char is at the last position")
    void testEscapeAtLastPosition() {
        assertThatThrownBy(() -> Escape.escape("wuff \\"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("handleNormalCharacter handles normal characters fine")
    void handleNormalCharacter() {
        final StringBuilder sb = new StringBuilder("a");
        final StringBuilder output = new StringBuilder();
        final StringBuilder original = new StringBuilder();
        Escape.handleNormalCharacter(sb, output, original);
        assertThat(sb.length()).isEqualTo(0);
        assertThat(output.toString()).isEqualTo("a");
        assertThat(original.toString()).isEqualTo("a");
    }

    @Test
    @DisplayName("handleNormalCharacter throws exception in case of \n")
    void handleNormalCharacterThrowsUpOnNewLine() {
        final StringBuilder sb = new StringBuilder("\n");
        final StringBuilder output = new StringBuilder();
        final StringBuilder original = new StringBuilder();
        assertThatThrownBy(() -> Escape.handleNormalCharacter(sb, output, original))
                .isInstanceOf(GeciException.class);
    }

    @Test
    @DisplayName("handleNormalCharacter throws exception in case of \r")
    void handleNormalCharacterThrowsUpOnLineFeed() {
        final StringBuilder sb = new StringBuilder("\r");
        final StringBuilder output = new StringBuilder();
        final StringBuilder original = new StringBuilder();
        assertThatThrownBy(() -> Escape.handleNormalCharacter(sb, output, original))
                .isInstanceOf(GeciException.class);
    }

    @Test
    @DisplayName("handleNormalMultiLineStringCharacter functions as needed")
    void handleNormalMultiLineStringCharacterWorks() {
        final StringBuilder sb = new StringBuilder("a\n\r\nb\r\rc\rd");
        final StringBuilder output = new StringBuilder();
        final StringBuilder original = new StringBuilder();
        while (sb.length() > 0)
            Escape.handleNormalMultiLineStringCharacter(sb, output, original);
        assertThat(sb.length()).isEqualTo(0);
        assertThat(output.toString()).isEqualTo("a\n\nb\nc\nd");
        assertThat(original.toString()).isEqualTo("a\n\r\nb\r\rc\rd");
    }

    @Test
    @DisplayName("Create output creates a StringBuilder when the string is long enough")
    void createOutputCreatesOutput() {
        assertThat(Escape.createOutput(new StringBuilder("aa"), "aa").length())
                .isEqualTo(0);
    }

    @Test
    @DisplayName("Create output creates throws up if string is short")
    void createOutputThrows() {
        assertThatThrownBy(() -> Escape.createOutput(new StringBuilder("a"), "a"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
