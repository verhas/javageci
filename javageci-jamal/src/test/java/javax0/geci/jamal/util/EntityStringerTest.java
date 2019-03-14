package javax0.geci.jamal.util;

import javax0.jamal.api.BadSyntax;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

public class EntityStringerTest {

    private void argless(){}
    private void arg1pri(int [][]a){}
    private void arg1obj(Integer a){}
    private void arg1objarr(Map a, Set ...s){}

    @Test
    @DisplayName("creates the expected fingerprint for the methods")
    void testMethod2Fingerprint() throws NoSuchMethodException {
        Assertions.assertEquals("javax0.geci.jamal.util.EntityStringerTest|argless|", EntityStringer.method2Fingerprint(EntityStringerTest.class.getDeclaredMethod("argless")));
        Assertions.assertEquals("javax0.geci.jamal.util.EntityStringerTest|arg1pri|int[][]", EntityStringer.method2Fingerprint(EntityStringerTest.class.getDeclaredMethod("arg1pri", int[][].class)));
        Assertions.assertEquals("javax0.geci.jamal.util.EntityStringerTest|arg1obj|java.lang.Integer", EntityStringer.method2Fingerprint(EntityStringerTest.class.getDeclaredMethod("arg1obj",Integer.class)));
        Assertions.assertEquals("javax0.geci.jamal.util.EntityStringerTest|arg1objarr|java.util.Map|java.util.Set[]", EntityStringer.method2Fingerprint(EntityStringerTest.class.getDeclaredMethod("arg1objarr",Map.class,Set[].class)));
    }

    @Test
    @DisplayName("creates the expected fingerprint for the methods")
    void testFingerprint2Method() throws NoSuchMethodException, BadSyntax {
        Assertions.assertEquals("argless",EntityStringer.fingerprint2Method("javax0.geci.jamal.util.EntityStringerTest|argless").getName());
        Assertions.assertEquals("arg1pri",EntityStringer.fingerprint2Method("javax0.geci.jamal.util.EntityStringerTest|arg1pri|int[][]").getName());
        Assertions.assertEquals("arg1obj",EntityStringer.fingerprint2Method("javax0.geci.jamal.util.EntityStringerTest|arg1obj|java.lang.Integer").getName());
        Assertions.assertEquals("arg1objarr",EntityStringer.fingerprint2Method("javax0.geci.jamal.util.EntityStringerTest|arg1objarr|java.util.Map|java.util.Set[]").getName());
    }
}
