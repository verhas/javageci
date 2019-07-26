package javax0.geci.tools.syntax;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestLexer {

    @Test
    @DisplayName("Test the lexer returning EOF multiple times when input is empty")
    void testNullInput() {
        final var lexer = new Lexer("");
        final var lexeme = lexer.get();
        Assertions.assertEquals(lexeme.type, Lexeme.Type.EOF);
    }

    @Test
    @DisplayName("Test the lexer is returning simple symbols")
    void testSymbol() {
        final var lexer = new Lexer("|+*?");
        testSym(lexer, "|");
        testSym(lexer, "+");
        testSym(lexer, "*");
        testSym(lexer, "?");
    }

    private void testSym(Lexer lexer, String s) {
        final var lexeme = lexer.get();
        Assertions.assertEquals(lexeme.type, Lexeme.Type.SYMBOL);
        Assertions.assertEquals(s,lexeme.string);
    }

    @Test
    @DisplayName("The lexer returns identifiers that may contain . and ( and ) and , ")
    void testIdentifiers() {
        final var lexer = new Lexer("identif module.java me.thod(vara , varb)");
        testWord(lexer, "identif");
        final var space = lexer.get();
        Assertions.assertEquals(Lexeme.Type.SPACE,space.type);
        testWord(lexer, "module.java");
        lexer.get();
        testWord(lexer, "me.thod(vara,varb)");
    }

    private void testWord(Lexer lexer, String s) {
        final var lexeme = lexer.get();
        Assertions.assertEquals(lexeme.type, Lexeme.Type.WORD);
        Assertions.assertEquals(s,lexeme.string);
    }

    @Test
    @DisplayName("The lexer returns regular expressions with / in it")
    void testRegex() {
        final var lexer = new Lexer("/\\s*\\/[a-zA-Z]/");
        final var lexeme = lexer.get();
        Assertions.assertEquals(Lexeme.Type.REGEX,lexeme.type);
        Assertions.assertEquals("\\s*/[a-zA-Z]",lexeme.string);
    }

    @Test
    @DisplayName("The lexer skips spaces when requested to skip")
    void testSkipSpace() {
        final var lexer = new Lexer("/\\s*\\/[a-zA-Z]/ a |",true);
        final var regex = lexer.get();
        Assertions.assertEquals(Lexeme.Type.REGEX,regex.type);
        Assertions.assertEquals("\\s*/[a-zA-Z]",regex.string);
        final var word = lexer.get();
        Assertions.assertEquals(Lexeme.Type.WORD,word.type);
        Assertions.assertEquals("a",word.string);
        final var symb = lexer.get();
        Assertions.assertEquals(Lexeme.Type.SYMBOL,symb.type);
        Assertions.assertEquals("|",symb.string);


    }
}
