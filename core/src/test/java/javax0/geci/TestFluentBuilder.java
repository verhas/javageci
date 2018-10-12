package javax0.geci;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFluentBuilder {

    @Test
    public void testTerminalBuildup() {
        Assertions.assertEquals("once,optional?,(a,b,c,d){OR}",
                FluentBuilder.from(Object.class)
                        .call("once")
                        .optional("optional")
                        .oneOf("a", "b", "c", "d")
                        .toString()
        );
    }

    @Test
    public void testComplexBuildup() {
        var t = FluentBuilder.from(Object.class);
        var s = t.oneOf("y", "x", "w", "z");
        Assertions.assertEquals("once,optional?,((y,x,w,z){OR})?,(a,b,c,d){OR},h*,m+,((y,x,w,z){OR})*,((y,x,w,z){OR})?",
                t.call("once")
                        .optional("optional")
                        .optional(s)
                        .oneOf("a", "b", "c", "d")
                        .zeroOrMore("h")
                        .many("m")
                        .zeroOrMore(s)
                        .optional(s)
                        .toString()
        );
    }

    @Test
    public void testClassMismatch() {
        var t = FluentBuilder.from(Object.class);
        var s = FluentBuilder.from(String.class).oneOf("y", "x", "w", "z");
        Assertions.assertThrows(GeciException.class, () ->
                t.call("once")
                        .optional("optional")
                        .optional(s)
                        .oneOf("a", "b", "c", "d")
                        .zeroOrMore("h")
                        .many("m")
                        .zeroOrMore(s)
                        .optional(s)
                        .toString()
        );
    }

}
