package javax0.geci.jamal_test.util;

import javax0.geci.jamal.util.EntityStringer;
import javax0.jamal.api.BadSyntax;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

public class TestEntityStringer {

    private void argless() {
    }

    private void arglesse() throws BadSyntax {
    }

    private void arg1pri(int[][] a) {
    }

    private void arg1obj(Integer a) {
    }

    private void arg1objarr(Map a, Set... s) {
    }

    private void arg1objarra(Map a, Set[] s) {
    }

    final String mformat = "$class|$name|$args|$throws|";

    @Test
    @DisplayName("Creates the method for the argument less method that does not throw exceptions")
    void testArglessMethod() throws NoSuchMethodException {
        Assertions.assertEquals("javax0.geci.jamal_test.util.TestEntityStringer|argless|||",
            EntityStringer.method2Fingerprint(TestEntityStringer.class.getDeclaredMethod("argless"), mformat, ":", ":",null));
    }

    @Test
    @DisplayName("Creates the method for the argument less method that throws exceptions")
    void testArglessMethodWithExceptions() throws NoSuchMethodException {
        Assertions.assertEquals("javax0.geci.jamal_test.util.TestEntityStringer|arglesse||throw javax0.jamal.api.BadSyntax|",
            EntityStringer.method2Fingerprint(TestEntityStringer.class.getDeclaredMethod("arglesse"), mformat, ":", ":",null));
    }

    @Test
    @DisplayName("Create the fingerprint for the method that has one argument")
    void testOneargMethod() throws NoSuchMethodException {
        final var format = "$class|$name|$args|";
        Assertions.assertEquals("javax0.geci.jamal_test.util.TestEntityStringer|arg1pri|int[][]|",
            EntityStringer.method2Fingerprint(TestEntityStringer.class.getDeclaredMethod("arg1pri", int[][].class), format, ":", ":",null));
    }

    @Test
    @DisplayName("create the fingerprint for the methopd that has primitive argument")
    void testPrimitiveArgumentMethod() throws NoSuchMethodException {
        final var format = "$class|$name|$args|";
        Assertions.assertEquals("javax0.geci.jamal_test.util.TestEntityStringer|arg1obj|java.lang.Integer|",
            EntityStringer.method2Fingerprint(TestEntityStringer.class.getDeclaredMethod("arg1obj", Integer.class), format, ":", ":",null));
    }

    @Test
    @DisplayName("create the fingerprint for the methopd that has two arguments")
    void testTwoArgumentMethod() throws NoSuchMethodException {
        final var format = "$class|$name|$args|";
        Assertions.assertEquals("javax0.geci.jamal_test.util.TestEntityStringer|arg1objarr|java.util.Map:java.util.Set...|",
            EntityStringer.method2Fingerprint(TestEntityStringer.class.getDeclaredMethod("arg1objarr", Map.class, Set[].class), format, ":", ":",null));
    }

    @Test
    @DisplayName("create the fingerprint for the methopd that has two arguments, one array")
    void testTwoArgumentArrayMethod() throws NoSuchMethodException {
        final var format = "$class|$name|$args|";
        Assertions.assertEquals("javax0.geci.jamal_test.util.TestEntityStringer|arg1objarra|java.util.Map:java.util.Set[]|",
            EntityStringer.method2Fingerprint(TestEntityStringer.class.getDeclaredMethod("arg1objarra", Map.class, Set[].class), format, ":", ":",null));
    }
}
