package javax0.geci.javacomparator;

import javax0.geci.api.GeciException;
import javax0.geci.javacomparator.lex.CharacterLiteral;
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
        Assertions.assertEquals("",sut.consume(character("")).lexeme);
        Assertions.assertEquals("a",sut.consume(character("a")).lexeme);
        Assertions.assertEquals("\r",sut.consume(character("\\r")).lexeme);
        Assertions.assertEquals("\n",sut.consume(character("\\n")).lexeme);
        Assertions.assertEquals("\b",sut.consume(character("\\b")).lexeme);
        Assertions.assertEquals("\t",sut.consume(character("\\t")).lexeme);
        Assertions.assertEquals("\f",sut.consume(character("\\f")).lexeme);
        Assertions.assertEquals("\'",sut.consume(character("\\'")).lexeme);
        Assertions.assertEquals("\"",sut.consume(character("\\\"")).lexeme);
        Assertions.assertEquals("\77",sut.consume(character("\\77")).lexeme);
        Assertions.assertEquals("\073",sut.consume(character("\\073")).lexeme);
        Assertions.assertEquals("\07",sut.consume(character("\\07")).lexeme);
    }

    @Test
    @DisplayName("Test character literals that are syntactically incorrect")
    void testBadStringLiterals(){
        final var sut = new CharacterLiteral();
        Assertions.assertThrows(GeciException.class, () -> sut.consume(character("\n")));
        Assertions.assertThrows(GeciException.class, () -> sut.consume(character("\r")));
        Assertions.assertThrows(GeciException.class, () -> sut.consume(character("\\z")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(character("\\")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(new StringBuilder("\'")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(new StringBuilder("\'\\")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(new StringBuilder("\'b")));
    }
}
