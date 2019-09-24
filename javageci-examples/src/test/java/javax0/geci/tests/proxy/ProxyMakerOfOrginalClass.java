package javax0.geci.tests.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

public class ProxyMakerOfOrginalClass {
    private static class ProxyOfOriginalClass extends OriginalClass {
        private final InvocationHandler handler;
        private final OriginalClass instance;

        private ProxyOfOriginalClass(InvocationHandler handler, OriginalClass instance) {
            Objects.nonNull(handler);
            Objects.nonNull(instance);
            this.handler = handler;
            this.instance = instance;
            try {
                originalMethodMethod = OriginalClass.class.getMethod("originalMethod", String.class);
            } catch (NoSuchMethodException nsme) {
                throw new RuntimeException(nsme);
            }
        }

        private final Method originalMethodMethod;

        public void originalMethod(String s) throws NumberFormatException {
            if (handler != null) {
                try {
                    handler.invoke(instance, originalMethodMethod, new Object[]{s});
                } catch (NumberFormatException nfe) {
                    throw nfe;
                } catch (Throwable ignored) {
                }
            }
        }

    }

    public static OriginalClass newProxyInstance(InvocationHandler handler, OriginalClass instance) {
        return new ProxyOfOriginalClass(handler, instance);
    }

    public static OriginalClass newProxyInstance(InvocationHandler handler) {
        return new ProxyOfOriginalClass(handler, new OriginalClass());
    }
}
