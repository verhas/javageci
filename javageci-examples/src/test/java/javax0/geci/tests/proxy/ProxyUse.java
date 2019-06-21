package javax0.geci.tests.proxy;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class ProxyUse {

    @Test
    void test() {
        OriginalClass oc = ProxyMakerOfOrginalClass.newProxyInstance((Object original, Method method, Object[] args) ->
        {
            System.out.println("BEFORE");
            final var retval = method.invoke(original, args);
            System.out.println("AFTER");
            return retval;
        });
        oc.originalMethod("Hello, World!");
    }
}
