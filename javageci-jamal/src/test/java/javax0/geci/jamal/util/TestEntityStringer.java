package javax0.geci.jamal.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

public class TestEntityStringer {

    private void argless() {
    }

    private void arg1pri(int[][] a) {
    }

    private void arg1obj(Integer a) {
    }

    private void arg1objarr(Map a, Set... s) {
    }

    @Test
    @DisplayName("creates the expected fingerprint for the methods")
    void testMethod2Fingerprint() throws NoSuchMethodException {
        final var format = "$class|$name|$args";
        Assertions.assertEquals("javax0.geci.jamal.util.TestEntityStringer|argless|",
            EntityStringer.method2Fingerprint(TestEntityStringer.class.getDeclaredMethod("argless"), format, ":", ":"));
        Assertions.assertEquals("javax0.geci.jamal.util.TestEntityStringer|arg1pri|int[][]",
            EntityStringer.method2Fingerprint(TestEntityStringer.class.getDeclaredMethod("arg1pri", int[][].class), format, ":", ":"));
        Assertions.assertEquals("javax0.geci.jamal.util.TestEntityStringer|arg1obj|java.lang.Integer",
            EntityStringer.method2Fingerprint(TestEntityStringer.class.getDeclaredMethod("arg1obj", Integer.class), format, ":", ":"));
        Assertions.assertEquals("javax0.geci.jamal.util.TestEntityStringer|arg1objarr|java.util.Map:java.util.Set[]",
            EntityStringer.method2Fingerprint(TestEntityStringer.class.getDeclaredMethod("arg1objarr", Map.class, Set[].class), format, ":", ":"));
    }
}
