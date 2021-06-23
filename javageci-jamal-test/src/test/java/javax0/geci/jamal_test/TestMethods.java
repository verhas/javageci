package javax0.geci.jamal_test;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.testsupport.TestThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class TestMethods {

    @Test
    @DisplayName("Test that Set collects and creates good formatting user defined macros")
    void testQ()throws Exception {
        TestThat.theInput("{@set class=javax0.geci.jamal_test.TestMethods methods id=\"methodsOfTestMethods\"}" +
            "{!@for $m from methodsOfTestMethods=" +
            "{$m type}|{$m name}|{$m throws}\n" +
            "}"
        ).results(
            "Object|clone|throw CloneNotSupportedException\n" +
                "void|finalize|throw Throwable\n" +
                "String|toString|\n" +
                "boolean|equals|\n" +
                "Class|getClass|\n" +
                "void|notify|\n" +
                "void|notifyAll|\n" +
                "void|wait|throw InterruptedException\n" +
                "void|wait|throw InterruptedException\n" +
                "void|wait|throw InterruptedException\n" +
                "int|hashCode|\n" +
                "void|testC|throw reflect.InvocationTargetException,NoSuchMethodException,InstantiationException,javax0.jamal.api.BadSyntax,IllegalAccessException\n" +
                "void|testD|throw reflect.InvocationTargetException,NoSuchMethodException,InstantiationException,javax0.jamal.api.BadSyntax,IllegalAccessException\n" +
                "void|testQ|throw Exception\n"
        );
    }

    @Test
    @DisplayName("Macro methods return all methods for TestMethods")
    void testC() throws InvocationTargetException, NoSuchMethodException, InstantiationException, BadSyntax, IllegalAccessException {
        TestThat.theInput("{#methods (class=javax0.geci.jamal_test.TestMethods)}").results(
            "Object|clone|," +
                "Object|finalize|," +
                "Object|toString|," +
                "Object|equals|java.lang.Object," +
                "Object|getClass|," +
                "Object|notify|," +
                "Object|notifyAll|," +
                "Object|wait|," +
                "Object|wait|long," +
                "Object|wait|long:int," +
                "Object|hashCode|," +
                "javax0.geci.jamal_test.TestMethods|testC|," +
                "javax0.geci.jamal_test.TestMethods|testD|," +
                "javax0.geci.jamal_test.TestMethods|testQ|"
        );
    }

    @Test
    void testD() throws InvocationTargetException, NoSuchMethodException, InstantiationException, BadSyntax, IllegalAccessException {
        TestThat.theInput("{#methods {@define $class=javax0.geci.jamal_test.TestMethods}{@define $selector=package}}").results(
            "javax0.geci.jamal_test.TestMethods|testC|" +
                ",javax0.geci.jamal_test.TestMethods|testD|" +
                ",javax0.geci.jamal_test.TestMethods|testQ|"
        );
    }
}
