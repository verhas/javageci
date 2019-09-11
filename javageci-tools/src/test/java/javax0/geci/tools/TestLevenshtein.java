package javax0.geci.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestLevenshtein {

    @Test
    @DisplayName("Different example strings distance is calculated")
    void testZero(){
        // snippet Levenshtein_test
        Assertions.assertEquals(0,Levenshtein.distance("same string","same string"));
        Assertions.assertEquals(1,Levenshtein.distance("same","seme"));
        Assertions.assertEquals(1,Levenshtein.distance("same","shame"));
        Assertions.assertEquals(1,Levenshtein.distance("same","sam"));
        Assertions.assertEquals(2,Levenshtein.distance("same","shama"));
        Assertions.assertEquals(2,Levenshtein.distance("same","sham"));
        Assertions.assertEquals(2,Levenshtein.distance("same","esam"));
        Assertions.assertEquals(3,Levenshtein.distance("same","xmek"));
        Assertions.assertEquals(4,Levenshtein.distance("same","xyzk"));
        // end snippet
    }

}
