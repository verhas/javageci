package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestStringLiteral {

    private static StringBuilder string(String s) {
        return new StringBuilder("\"" + s + "\"");
    }    private static StringBuilder mlstring(String s) {
        return new StringBuilder("\"\"\"\n" + s + "\"\"\"");
    }

    @Test
    @DisplayName("Test simple string literals that are syntactically correct")
    void testStringLiterals() {
        final var sut = new StringLiteral();
        Assertions.assertEquals("", sut.apply(string("")).lexeme);
        Assertions.assertEquals("a", sut.apply(string("a")).lexeme);
        Assertions.assertEquals("\r", sut.apply(string("\\r")).lexeme);
        Assertions.assertEquals("\n", sut.apply(string("\\n")).lexeme);
        Assertions.assertEquals("\b", sut.apply(string("\\b")).lexeme);
        Assertions.assertEquals("\t", sut.apply(string("\\t")).lexeme);
        Assertions.assertEquals("\f", sut.apply(string("\\f")).lexeme);
        Assertions.assertEquals("'", sut.apply(string("\\'")).lexeme);
        Assertions.assertEquals("\"", sut.apply(string("\\\"")).lexeme);
        Assertions.assertEquals("\773", sut.apply(string("\\773")).lexeme);
        Assertions.assertEquals("\073", sut.apply(string("\\073")).lexeme);
        Assertions.assertEquals("\079", sut.apply(string("\\079")).lexeme);
    }

    @Test
    @DisplayName("Test multi line string literals that are syntactically correct")
    void testMultiLineStringLiterals() {
        final var sut = new StringLiteral();
        Assertions.assertEquals("\n", sut.apply(mlstring("")).lexeme);
        Assertions.assertEquals("\na", sut.apply(mlstring("a")).lexeme);
        Assertions.assertEquals("\n\r", sut.apply(mlstring("\\r")).lexeme);
        Assertions.assertEquals("\n\n", sut.apply(mlstring("\\n")).lexeme);
        Assertions.assertEquals("\n\b", sut.apply(mlstring("\\b")).lexeme);
        Assertions.assertEquals("\n\t", sut.apply(mlstring("\\t")).lexeme);
        Assertions.assertEquals("\n\f", sut.apply(mlstring("\\f")).lexeme);
        Assertions.assertEquals("\n'", sut.apply(mlstring("\\'")).lexeme);
        Assertions.assertEquals("\n\"", sut.apply(mlstring("\\\"")).lexeme);
        Assertions.assertEquals("\n\773", sut.apply(mlstring("\\773")).lexeme);
        Assertions.assertEquals("\n\073", sut.apply(mlstring("\\073")).lexeme);
        Assertions.assertEquals("\n\079", sut.apply(mlstring("\\079")).lexeme);
        Assertions.assertEquals("\na\nb", sut.apply(mlstring("a\n\rb")).lexeme);
        Assertions.assertEquals("\na\nb", sut.apply(mlstring("a\rb")).lexeme);
    }

    @Test
    @DisplayName("Test simple string literals that are syntactically incorrect")
    void testBadStringLiterals() {
        final var sut = new StringLiteral();
        Assertions.assertThrows(GeciException.class, () -> sut.apply(string("\n")));
        Assertions.assertThrows(GeciException.class, () -> sut.apply(string("\r")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(string("\\z")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(string("\\")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\"")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\"\\")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\"bababa")));
    }

    @Test
    @DisplayName("Test multi line string literals that are syntactically incorrect")
    void testBadMultiLineStringLiterals() {
        final var sut = new StringLiteral();
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\"\"\"")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\"\"\"\"")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\"\"\"\"\"")));
    }

}
