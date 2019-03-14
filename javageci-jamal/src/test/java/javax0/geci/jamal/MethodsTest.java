package javax0.geci.jamal;

import javax0.geci.jamal.reflection.Methods;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.testsupport.TestThat;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class MethodsTest {

    @Test
    void testC() throws InvocationTargetException, NoSuchMethodException, InstantiationException, BadSyntax, IllegalAccessException {
        TestThat.forMacro(Methods.class).fromInput("javax0.geci.jamal.MethodsTest").results(
                "java.lang.Object|toString|" +
                        ",java.lang.Object|equals|java.lang.Object" +
                        ",java.lang.Object|getClass|" +
                        ",java.lang.Object|notify|" +
                        ",java.lang.Object|notifyAll|" +
                        ",java.lang.Object|wait|" +
                        ",java.lang.Object|wait|long" +
                        ",java.lang.Object|wait|long|int" +
                        ",java.lang.Object|hashCode|" +
                        ",javax0.geci.jamal.MethodsTest|testC|" +
                        ",javax0.geci.jamal.MethodsTest|testD|"
        );
    }
        @Test
        void testD() throws InvocationTargetException, NoSuchMethodException, InstantiationException, BadSyntax, IllegalAccessException {
            TestThat.forMacro(Methods.class).fromInput("javax0.geci.jamal.MethodsTest/package").results(
                        "javax0.geci.jamal.MethodsTest|testC|" +
                            ",javax0.geci.jamal.MethodsTest|testD|"
            );
    }
}
