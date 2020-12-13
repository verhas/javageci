package javax0.geci.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestClassLine {

    private final Pattern CLASS_LINE;

    TestClassLine() throws NoSuchFieldException, IllegalAccessException {
        Field classLineField = AbstractJavaGenerator.class.getDeclaredField("CLASS_LINE");
        classLineField.setAccessible(true);
        CLASS_LINE = (Pattern) classLineField.get(null);
    }

    @Test
    @DisplayName("Pattern matches sample class lines")
    void test(){
        assertTrue(CLASS_LINE.matcher("class A{").find());
        assertTrue(CLASS_LINE.matcher("class A {").find());
        assertTrue(CLASS_LINE.matcher("public class A {").find());
        assertTrue(CLASS_LINE.matcher("protected class A {").find());
        assertTrue(CLASS_LINE.matcher("public final class A {").find());
        assertTrue(CLASS_LINE.matcher("public abstract class A {").find());
        assertTrue(CLASS_LINE.matcher("public abstract class _A {").find());
        assertTrue(CLASS_LINE.matcher("public class A1 implements B{").find());
        assertTrue(CLASS_LINE.matcher("public class A extends C$Z implements B{").find());

        assertFalse(CLASS_LINE.matcher("blabla").find());
        assertFalse(CLASS_LINE.matcher("class 13Z {").find());
        assertFalse(CLASS_LINE.matcher("class A { //comment").find());
    }

}
