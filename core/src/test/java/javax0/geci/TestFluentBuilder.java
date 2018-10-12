package javax0.geci;

import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFluentBuilder {

    @Test
    public void testTerminalBuildup() {
        Assertions.assertEquals("something,opti_method?,(a,b,c,d){OR}",
                FluentBuilder.from(Object.class)
                        .call("something")
                        .optional("opti_method")
                        .oneOf("a", "b", "c", "d")
                        .toString()
        );
    }

    @Test
    public void testComplexBuildup() {
        var t = FluentBuilder.from(Object.class);
        Assertions.assertEquals("something,opti_method?,((y,x,w,z){OR})?,(a,b,c,d){OR}",
                t.call("something")
                        .optional("opti_method")
                        .optional(t.oneOf("y","x","w","z"))
                        .oneOf("a", "b", "c", "d")
                        .toString()
        );
    }

}
