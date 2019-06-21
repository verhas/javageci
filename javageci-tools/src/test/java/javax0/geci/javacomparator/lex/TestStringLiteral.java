package javax0.geci.javacomparator.lex;

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
        Assertions.assertEquals("",sut.apply(string("")).lexeme);
        Assertions.assertEquals("a",sut.apply(string("a")).lexeme);
        Assertions.assertEquals("\r",sut.apply(string("\\r")).lexeme);
        Assertions.assertEquals("\n",sut.apply(string("\\n")).lexeme);
        Assertions.assertEquals("\b",sut.apply(string("\\b")).lexeme);
        Assertions.assertEquals("\t",sut.apply(string("\\t")).lexeme);
        Assertions.assertEquals("\f",sut.apply(string("\\f")).lexeme);
        Assertions.assertEquals("\'",sut.apply(string("\\'")).lexeme);
        Assertions.assertEquals("\"",sut.apply(string("\\\"")).lexeme);
        Assertions.assertEquals("\773",sut.apply(string("\\773")).lexeme);
        Assertions.assertEquals("\073",sut.apply(string("\\073")).lexeme);
        Assertions.assertEquals("\079",sut.apply(string("\\079")).lexeme);
    }

    @Test
    @DisplayName("Test string literals that are syntactically incorrect")
    void testBadStringLiterals(){
        final var sut = new StringLiteral();
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(string("\n")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(string("\r")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(string("\\z")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(string("\\")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\"")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\"\\")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.apply(new StringBuilder("\"bababa")));
    }
}
