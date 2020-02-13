package javax0.geci.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestArrayTools {

    @Test
    @DisplayName("Test that two arrays are joined together nicely")
    void arraysAreProperlyJoined() {
        Assertions.assertEquals("a,b,c,d,e", String.join(",",
            ArrayTools.join(new String[]{"a", "b"}, new String[]{"c", "d", "e"})));
    }

    @Test
    @DisplayName("When one array is null the other one is returned")
    void nullArrayJoinReturnsTheOther() {
        final var a = new Object[4];
        Assertions.assertSame(a, ArrayTools.join(null, a));
        Assertions.assertSame(a, ArrayTools.join(a, null));
    }

    @Test
    @DisplayName("Joining two nulls return null")
    void nullArraysJoinReturnsNull() {
        Assertions.assertNull(ArrayTools.join(null, null));
    }

    @Test
    @DisplayName("Joining zero length arrays return zero length")
    void joiningZeroLengthArray() {
        final var a = new Object[0];
        final var b = new Object[0];
        Assertions.assertEquals(0, ArrayTools.join(a, b).length);
    }

    @Test
    @DisplayName("Appending zero length arrays returns new array")
    void appendZeroLengthArray() {
        final var a = new String[]{"a", "b", "c"};
        final var b = new String[0];
        final var result = ArrayTools.join(a, b);
        Assertions.assertNotSame(a, result);
        Assertions.assertArrayEquals(a, result);
    }

    @Test
    @DisplayName("Prepending zero length arrays returns new array")
    void prependZeroLengthArray() {
        final var a = new String[0];
        final var b = new String[]{"a", "b", "c"};
        final var result = ArrayTools.join(a, b);
        Assertions.assertNotSame(b, result);
        Assertions.assertArrayEquals(b, result);
    }
}
