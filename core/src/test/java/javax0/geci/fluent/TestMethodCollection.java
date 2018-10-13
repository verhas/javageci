package javax0.geci.fluent;

import javax0.geci.api.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestMethodCollection {

    @Test
    public void testThatAllTypesAndMethodsAreCollectedFromObject() {
        Assertions.assertEquals("{\n" +
            "  \"equals(Object) \" -> equals\n" +
            "  \"getClass() \" -> getClass\n" +
            "  \"hashCode() \" -> hashCode\n" +
            "  \"notify() \" -> notify\n" +
            "  \"notifyAll() \" -> notifyAll\n" +
            "  \"toString() \" -> toString\n" +
            "  \"wait()  throws java.lang.InterruptedException\" -> wait\n" +
            "  \"wait(long)  throws java.lang.InterruptedException\" -> wait\n" +
            "  \"wait(long,int)  throws java.lang.InterruptedException\" -> wait\n" +
            "}", new MethodCollection(Object.class).toString());
    }

    @Test
    public void testThatAllTypesAndMethodsAreCollectedFromTestClass() {
        Assertions.assertEquals("{\n" +
            "  \"m1(String,TestMethodCollection) \" -> m1\n" +
            "  \"m1(javax0.geci.api.Geci,javax0.geci.annotations.Geci) \" -> m1\n" +
            "}", new MethodCollection(TestClass.class).toString());
    }

    private static class TestClass {
        public void m1(Geci a, javax0.geci.annotations.Geci b) {
        }

        public void m1(java.lang.String a, TestMethodCollection b) {
        }
    }
}
