package javax0.geci.fluent;

import javax0.geci.api.GeciException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFluentBuilder {

    @Test
    public void testTerminalBuildup() {
        Assertions.assertEquals("once,optional?,(a,b,c,d){OR}",
            FluentBuilder.from(TestClass.class)
                .call("once")
                .optional("optional")
                .oneOf("a", "b", "c", "d")
                .toString()
        );
    }

    @Test
    public void testComplexBuildup() {
        var t = FluentBuilder.from(TestClass.class);
        var s = t.oneOf("y", "x", "w", "z");
        Assertions.assertEquals("once,optional?,((y,x,w,z){OR})?,(a,b,c,d){OR},h*,m,m*,((y,x,w,z){OR})*,((y,x,w,z){OR})?",
            t.call("once")
                .optional("optional")
                .optional(s)
                .oneOf("a", "b", "c", "d")
                .zeroOrMore("h")
                .oneOrMore("m")
                .zeroOrMore(s)
                .optional(s)
                .toString()
        );
    }

    @Test
    public void testClassMismatch() {
        var t = FluentBuilder.from(TestClass.class);
        var s = FluentBuilder.from( new TestClass(){}.getClass()).oneOf("y", "x", "w", "z");
        Assertions.assertThrows(GeciException.class, () ->
            t.call("once")
                .optional("optional")
                .optional(s)
                .oneOf("a", "b", "c", "d")
                .zeroOrMore("h")
                .oneOrMore("m")
                .zeroOrMore(s)
                .optional(s)
        );
    }

    private static class TestClass {
        public void once() {
        }

        public void optional() {
        }

        public void a() {
        }

        public void b() {
        }

        public void c() {
        }

        public void d() {
        }

        public void h() {
        }

        public void m() {
        }

        public void y() {
        }

        public void x() {
        }

        public void w() {
        }

        public void z() {
        }
    }

}
