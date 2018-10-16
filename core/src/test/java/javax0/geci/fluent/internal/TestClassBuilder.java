package javax0.geci.fluent.internal;

import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestClassBuilder {

    @Test
    public void testTerminalsBuildup() {
// interface If0{
//   void a() ;
//   void b() ;
//   void c() ;
//   void d() ;
// }
// interface If1 extends If0{
//   If0 b() ;
// }
// interface If2{
//   If1 a() ;
// }
        var fluent = FluentBuilder.from(TestClass.class)
            .one("a")
            .optional("b")
            .oneOf("a", "b", "c", "d");
        Assertions.assertEquals("interface If0{\n" +
                "  void a();\n" +
                "  void b();\n" +
                "  void c();\n" +
                "  void d();\n" +
                "}\n" +
                "interface If1 extends If0{\n" +
                "  If0 b();\n" +
                "}\n" +
                "interface If2{\n" +
                "  If1 a();\n" +
                "}\n",
            new ClassBuilder(fluent).build()
        );
    }

    @Test
    public void testTerminalsBuildupFullSample() {
//        interface If0{
//            void a() ;
//            void b() ;
//        }
//        interface If1 extends If0{
//            If1 d() ;
//        }
//        interface If2 extends If1{
//            If2 c() ;
//        }
//        interface If3{
//            If2 c() ;
//        }
//        interface If4 extends If3{
//            If3 b() ;
//        }
//        interface If5{
//            If4 a() ;
//        }
        var fluent = FluentBuilder.from(TestClass.class)
            .one("a")
            .optional("b")
            .oneOrMore("c")
            .zeroOrMore("d")
            .oneOf("a","b");

        Assertions.assertEquals("interface If0{\n" +
                "  void a();\n" +
                "  void b();\n" +
                "}\n" +
                "interface If1 extends If0{\n" +
                "  If1 d();\n" +
                "}\n" +
                "interface If2 extends If1{\n" +
                "  If2 c();\n" +
                "}\n" +
                "interface If3{\n" +
                "  If2 c();\n" +
                "}\n" +
                "interface If4 extends If3{\n" +
                "  If3 b();\n" +
                "}\n" +
                "interface If5{\n" +
                "  If4 a();\n" +
                "}\n",
            new ClassBuilder(fluent).build()
        );
    }

        @Test
    public void testTreeBuildup() {
        var f = FluentBuilder.from(TestClass.class);
        var aOrB = f.oneOf("a", "b");
        var fluent = f.one("a")
            .optional(aOrB)
            .oneOf("a", "b", "c", "d");
        Assertions.assertEquals("interface If0{\n" +
                "  void a();\n" +
                "  void b();\n" +
                "  void c();\n" +
                "  void d();\n" +
                "}\n" +
                "interface If1 extends If0 {\n" +
                "  If0 a();\n" +
                "  If0 b();\n" +
                "}\n" +
                "interface If2{\n" +
                "  If1 a();\n" +
                "}\n",
            new ClassBuilder(fluent).build()
        );
    }

    @Test
    public void testComplexTreeBuildup() {
        var f = FluentBuilder.from(TestClass.class);
        var aOrB = f.oneOf("a", "b");
        var fluent = f.one("a")
            .optional(aOrB)
            .one("c")
            .zeroOrMore(aOrB)
            .oneOf("a", "b", "c", "d");
        Assertions.assertEquals("interface If0{\n" +
                "  void a();\n" +
                "  void b();\n" +
                "  void c();\n" +
                "  void d();\n" +
                "}\n" +
                "interface If2 extends If0 {\n" +
                "  If1 a();\n" +
                "  If1 b();\n" +
                "}\n" +
                "interface If1 extends If2{}interface If3{\n" +
                "  If1 c();\n" +
                "}\n" +
                "interface If4 extends If3 {\n" +
                "  If3 a();\n" +
                "  If3 b();\n" +
                "}\n" +
                "interface If5{\n" +
                "  If4 a();\n" +
                "}\n",
            new ClassBuilder(fluent).build()
        );
    }

    @Test
    public void testOptionalInOptionalTreeBuildup() {
//        interface If0{
//            void c() ;
//        }
//        interface If1 extends If0{
//            If0 a() ;
//        }
//        interface If2{
//            If0 b() ;
//        }
//        interface If3 extends If2,If1{}
//        interface If4{
//            If3 a() ;
//        }
        var f = FluentBuilder.from(TestClass.class);
        var aOrB = f.oneOf(f.optional("a"), f.one("b"));
        var fluent = f.one("a")
                .optional(aOrB)
                .one("c");
        Assertions.assertEquals("interface If0{\n" +
                        "  void c();\n" +
                        "}\n" +
                        "interface If1 extends If0{\n" +
                        "  If0 a();\n" +
                        "}\n" +
                        "interface If2{\n" +
                        "  If0 b();\n" +
                        "}\n" +
                        "interface If3 extends If2,If1{}\n" +
                        "interface If4{\n" +
                        "  If3 a();\n" +
                        "}\n",
                new ClassBuilder(fluent).build()
        );
    }
    public static class TestClass {
        public void a() {
        }

        public void b() {
        }

        public void c() {
        }

        public void d() {
        }

        public void e() {
        }

        public void f() {
        }

    }
}
