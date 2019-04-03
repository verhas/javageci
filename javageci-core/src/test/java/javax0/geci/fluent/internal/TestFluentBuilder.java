package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFluentBuilder {

    @Test
    public void testTerminalBuildup() {
        Assertions.assertEquals("once optional? (a|b|c|d)",
            FluentBuilder.from(TestClass.class)
                .one("once")
                .optional("optional")
                .oneOf("a", "b", "c", "d")
                .toString()
        );
    }

    @Test
    public void testComplexBuildup() {
        var t = FluentBuilder.from(TestClass.class);
        var s = t.oneOf("y", "x", "w", "z");
        Assertions.assertEquals("once optional? (y|x|w|z)? (a|b|c|d) h* m m* (y|x|w|z)* (y|x|w|z)?",
            t.cloner("a()").one("once")
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
        var s = FluentBuilder.from(new TestClass() {
        }.getClass()).oneOf("y", "x", "w", "z");
        Assertions.assertThrows(GeciException.class, () ->
            t.one("once")
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

        public TestClass a() {
            return null;
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
