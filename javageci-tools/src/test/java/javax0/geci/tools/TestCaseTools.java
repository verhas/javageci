package javax0.geci.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static javax0.geci.tools.CaseTools.camel;
import static javax0.geci.tools.CaseTools.lcase;
import static javax0.geci.tools.CaseTools.snake;
import static javax0.geci.tools.CaseTools.ucase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestCaseTools {

    @Test
    @DisplayName("lcase converts properly normal not empty string that starst with a letter")
    void lcaseConverts() {
        assertEquals("abc", lcase("Abc"));
    }

    @Test
    @DisplayName("ucase converts properly normal not empty string that starst with a letter")
    void ucaseConverts() {
        assertEquals("Abc", ucase("abc"));
    }

    @Test
    @DisplayName("lcase converts properly empty string")
    void lcaseConvertsZeroLengthString() {
        assertEquals("", lcase(""));
    }

    @Test
    @DisplayName("ucase converts properly empty string")
    void ucaseConvertsZeroLengthString() {
        assertEquals("", ucase(""));
    }

    @Test
    @DisplayName("lcase converts properly null string")
    void lcaseConvertsNull() {
        assertNull(null, lcase(null));
    }

    @Test
    @DisplayName("ucase converts properly null string")
    void ucaseConvertsNull() {
        assertNull(ucase(null));
    }

    @Test
    @DisplayName("camel converts a normal non-empty string")
    void camelConverts() {
        assertEquals("ThisIsToBeCameled", camel("THIS_IS_TO_BE_CAMELED"));
    }

    @Test
    @DisplayName("camel converts a normal non-empty string containing no _")
    void camelConvertsNo_() {
        assertEquals("Thisistobecameled", camel("THISISTOBECAMELED"));
    }

    @Test
    @DisplayName("camel converts an empty string")
    void camelConvertsEmpty() {
        assertEquals("", camel(""));
    }

    @Test
    @DisplayName("camel converts a null string")
    void camelConvertsNull() {
        assertNull(camel(null));
    }

    @Test
    @DisplayName("snake converts a normal non-empty string containing no upper case")
    void snakeConvertsNo_() {
        assertEquals("THIS_IS_TO_BE_SNAKED", snake("ThisIsToBeSnaked"));
    }

    @Test
    @DisplayName("snake converts an empty string")
    void snakeConvertsEmpty() {
        assertEquals("", snake(""));
    }

    @Test
    @DisplayName("snake converts a null string")
    void snakeConvertsNull() {
        assertNull(snake(null));
    }

}
