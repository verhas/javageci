package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;
import javax0.geci.javacomparator.lex.StringLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestStringLiteral {

    private static StringBuilder string(String s){
        return new StringBuilder("\""+s+"\"");
    }

    @Test
    @DisplayName("Test string literals that are syntactically correct")
    void testStringLiterals(){
        final var sut = new StringLiteral();
        Assertions.assertEquals("",sut.consume(string("")).lexeme);
        Assertions.assertEquals("a",sut.consume(string("a")).lexeme);
        Assertions.assertEquals("\r",sut.consume(string("\\r")).lexeme);
        Assertions.assertEquals("\n",sut.consume(string("\\n")).lexeme);
        Assertions.assertEquals("\b",sut.consume(string("\\b")).lexeme);
        Assertions.assertEquals("\t",sut.consume(string("\\t")).lexeme);
        Assertions.assertEquals("\f",sut.consume(string("\\f")).lexeme);
        Assertions.assertEquals("\'",sut.consume(string("\\'")).lexeme);
        Assertions.assertEquals("\"",sut.consume(string("\\\"")).lexeme);
        Assertions.assertEquals("\773",sut.consume(string("\\773")).lexeme);
        Assertions.assertEquals("\073",sut.consume(string("\\073")).lexeme);
        Assertions.assertEquals("\079",sut.consume(string("\\079")).lexeme);
    }

    @Test
    @DisplayName("Test string literals that are syntactically incorrect")
    void testBadStringLiterals(){
        final var sut = new StringLiteral();
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(string("\n")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(string("\r")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(string("\\z")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(string("\\")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(new StringBuilder("\"")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(new StringBuilder("\"\\")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.consume(new StringBuilder("\"bababa")));
    }
}
