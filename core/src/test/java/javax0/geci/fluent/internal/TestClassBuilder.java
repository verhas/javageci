package javax0.geci.fluent.internal;

import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestClassBuilder {

    @Test
    public void testTerminalBuildup() {
        /*
interface If0{
  public void a() ;
}
interface If1{
  public void b() ;
}
interface If2{
  public void c() ;
}
interface If3{
  public void d() ;
}
interface If4 extends If0,If2,If1,If3{}
interface If5 extends If4{
  public If4 b() ;
}
interface If6{
  public If5 a() ;
}*/
        var fluent = FluentBuilder.from(TestClass.class)
                .one("a")
                .optional("b")
                .oneOf("a", "b", "c", "d");
        Assertions.assertEquals("interface If0{\n" +
                        "  public void a() ;\n" +
                        "}\n" +
                        "interface If1{\n" +
                        "  public void b() ;\n" +
                        "}\n" +
                        "interface If2{\n" +
                        "  public void c() ;\n" +
                        "}\n" +
                        "interface If3{\n" +
                        "  public void d() ;\n" +
                        "}\n" +
                        "interface If4 extends If0,If2,If1,If3{}\n" +
                        "interface If5 extends If4{\n" +
                        "  public If4 b() ;\n" +
                        "}\n" +
                        "interface If6{\n" +
                        "  public If5 a() ;\n" +
                        "}\n",
                new ClassBuilder(fluent).build()
        );
    }


    public static class TestClass {
        public void a() {
        }

        public void b() {
        }

        public  void c() {
        }

        public void d() {
        }

        public void e() {
        }

        public void f() {
        }

    }
}
