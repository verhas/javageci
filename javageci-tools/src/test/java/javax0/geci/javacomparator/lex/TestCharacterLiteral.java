package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestCharacterLiteral {

    private static StringBuilder character(String s){
        return new StringBuilder("\'"+s+"\'");
    }

    @Test
    @DisplayName("Test character literals that are syntactically correct")
    void testStringLiterals(){
        final var sut = new CharacterLiteral();
        Assertions.assertEquals("",sut.apply(character("")).lexeme);
        Assertions.assertEquals("a",sut.apply(character("a")).lexeme);
        Assertions.assertEquals("\r",sut.apply(character("\\r")).lexeme);
        Assertions.assertEquals("\n",sut.apply(character("\\n")).lexeme);
        Assertions.assertEquals("\b",sut.apply(character("\\b")).lexeme);
        Assertions.assertEquals("\t",sut.apply(character("\\t")).lexeme);
        Assertions.assertEquals("\f",sut.apply(character("\\f")).lexeme);
        Assertions.assertEquals("\'",sut.apply(character("\\'")).lexeme);
        Assertions.assertEquals("\"",sut.apply(character("\\\"")).lexeme);
        Assertions.assertEquals("\77",sut.apply(character("\\77")).lexeme);
        Assertions.assertEquals("\073",sut.apply(character("\\073")).lexeme);
        Assertions.assertEquals("\07",sut.apply(character("\\07")).lexeme);
    }

    @Test
    @DisplayName("Test character literals that are syntactically incorrect")
    void testBadStringLiterals() {
        final var sut = new CharacterLiteral();
        Assertions.assertThrows(GeciException.class, () -> sut.apply(character("\n")));
        Assertions.assertThrows(GeciException.class, () -> sut.apply(character("\r")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(character("\\z")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(character("\\")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\'")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\'\\")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\'b")));
    }
}
