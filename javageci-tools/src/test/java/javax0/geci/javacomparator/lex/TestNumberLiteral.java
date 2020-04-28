package javax0.geci.javacomparator.lex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestNumberLiteral {


    void testNumber(String input, String expected, LexicalElement.Type expectedType, String remaining) {
        final var sut = new NumberLiteral();
        var sb = new StringBuilder(input);
        var lex = sut.apply(sb);
        Assertions.assertEquals(expected, lex.lexeme);
        Assertions.assertEquals(expectedType, lex.type);
        Assertions.assertEquals(remaining, sb.toString());
    }

    @Test
    void testIntegerNumbers() {
        testNumber("0", "0", javax0.geci.javacomparator.LexicalElement.Type.INTEGER, "");
        testNumber("0abbab", "0", javax0.geci.javacomparator.LexicalElement.Type.INTEGER, "abbab");
        testNumber("00abbab", "00", javax0.geci.javacomparator.LexicalElement.Type.INTEGER, "abbab");
        testNumber("0x0abbab", "0x0abbab", javax0.geci.javacomparator.LexicalElement.Type.INTEGER, "");
        testNumber("0xF0abbab", "0xF0abbab", javax0.geci.javacomparator.LexicalElement.Type.INTEGER, "");
        testNumber("0x7EFEFEFEFEFEFEFEL", "0x7EFEFEFEFEFEFEFEL", javax0.geci.javacomparator.LexicalElement.Type.INTEGER, "");
    }
    @Test
    void testFloatNumbers() {
        testNumber("0.", "0.", javax0.geci.javacomparator.LexicalElement.Type.FLOAT, "");
        testNumber("0.0", "0.0", javax0.geci.javacomparator.LexicalElement.Type.FLOAT, "");
        testNumber("0.01e13", "0.01e13", javax0.geci.javacomparator.LexicalElement.Type.FLOAT, "");
        testNumber("0.01e-13z", "0.01e-13", javax0.geci.javacomparator.LexicalElement.Type.FLOAT, "z");
        testNumber("0x.01p-13z", "0x.01p-13", javax0.geci.javacomparator.LexicalElement.Type.FLOAT, "z");
    }
}
