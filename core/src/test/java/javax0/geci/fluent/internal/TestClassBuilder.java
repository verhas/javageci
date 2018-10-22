package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestClassBuilder {

    @Test
    public void testIncompleteBuildup() {
        var fluent = FluentBuilder.from(TestClass.class);
        Assertions.assertThrows( GeciException.class, () -> new ClassBuilder((FluentBuilderImpl) fluent).build());
    }

    @Test
    public void testTerminalsBuildup() {
        var fluent = FluentBuilder.from(TestClass.class)
            .one("a")
            .optional("b")
            .oneOf("a", "b", "c", "d");
        Assertions.assertEquals("public static If2 start(){\n" +
                "    return new Wrapper();\n" +
                "}\n" +
                "public static class Wrapper implements If0,If2,If1{\n" +
                "    private final javax0.geci.fluent.internal.TestClassBuilder.TestClass that;\n" +
                "    public Wrapper(){\n" +
                "        this.that = new javax0.geci.fluent.internal.TestClassBuilder.TestClass();\n" +
                "    }\n" +
                "    public void b(){\n" +
                "        return that.b();\n" +
                "    }\n" +
                "    public void a(){\n" +
                "        return that.a();\n" +
                "    }\n" +
                "    public void d(){\n" +
                "        return that.d();\n" +
                "    }\n" +
                "    public void c(){\n" +
                "        return that.c();\n" +
                "    }\n" +
                "    public Wrapper f(){\n" +
                "        that.f();\n" +
                "        return this;\n" +
                "    }\n" +
                "    public Wrapper copy(){\n" +
                "        that.copy();\n" +
                "        return this;\n" +
                "    }\n" +
                "    public Wrapper e(){\n" +
                "        that.e();\n" +
                "        return this;\n" +
                "    }\n" +
                "}\n" +
                "interface If0{\n" +
                "    void a();\n" +
                "    void b();\n" +
                "    void c();\n" +
                "    void d();\n" +
                "}\n" +
                "interface If1  extends If0 {\n" +
                "    If0 b();\n" +
                "}\n" +
                "interface If2  {\n" +
                "    If1 a();\n" +
                "}\n",
            new ClassBuilder((FluentBuilderImpl) fluent).build()
        );
    }

    @Test
    public void testTerminalsBuildupFullSample() {
        var fluent = FluentBuilder.from(TestClass.class)
            .one("a")
            .optional("b")
            .oneOrMore("c")
            .zeroOrMore("d")
            .oneOf("a", "b");

        Assertions.assertEquals("public static If5 start(){\n" +
                "    return new Wrapper();\n" +
                "}\n" +
                "public static class Wrapper implements If0,If2,If1,If4,If3,If5{\n" +
                "    private final javax0.geci.fluent.internal.TestClassBuilder.TestClass that;\n" +
                "    public Wrapper(){\n" +
                "        this.that = new javax0.geci.fluent.internal.TestClassBuilder.TestClass();\n" +
                "    }\n" +
                "    public void b(){\n" +
                "        return that.b();\n" +
                "    }\n" +
                "    public void a(){\n" +
                "        return that.a();\n" +
                "    }\n" +
                "    public Wrapper d(){\n" +
                "        that.d();\n" +
                "        return this;\n" +
                "    }\n" +
                "    public Wrapper c(){\n" +
                "        that.c();\n" +
                "        return this;\n" +
                "    }\n" +
                "    public Wrapper f(){\n" +
                "        that.f();\n" +
                "        return this;\n" +
                "    }\n" +
                "    public Wrapper copy(){\n" +
                "        that.copy();\n" +
                "        return this;\n" +
                "    }\n" +
                "    public Wrapper e(){\n" +
                "        that.e();\n" +
                "        return this;\n" +
                "    }\n" +
                "}\n" +
                "interface If0{\n" +
                "    void a();\n" +
                "    void b();\n" +
                "}\n" +
                "interface If1  extends If0 {\n" +
                "    If1 d();\n" +
                "}\n" +
                "interface If2  extends If1 {\n" +
                "    If2 c();\n" +
                "}\n" +
                "interface If3  {\n" +
                "    If2 c();\n" +
                "}\n" +
                "interface If4  extends If3 {\n" +
                "    If3 b();\n" +
                "}\n" +
                "interface If5  {\n" +
                "    If4 a();\n" +
                "}\n",
            new ClassBuilder((FluentBuilderImpl) fluent).build()
        );
    }

    @Test
    public void testTreeBuildup() {
        var f = FluentBuilder.from(TestClass.class);
        var aOrB = f.oneOf("a", "b");
        var fluent = f.one("a")
            .optional(aOrB)
            .oneOf("a", "b", "c", "d");
        Assertions.assertEquals("public static If3 start(){\n" +
                "    return new Wrapper();\n" +
                "}\n" +
                "public static class Wrapper implements If0,If2,If1,If3{\n" +
                "    private final javax0.geci.fluent.internal.TestClassBuilder.TestClass that;\n" +
                "    public Wrapper(){\n" +
                "        this.that = new javax0.geci.fluent.internal.TestClassBuilder.TestClass();\n" +
                "    }\n" +
                "    public void b(){\n" +
                "        return that.b();\n" +
                "    }\n" +
                "    public void a(){\n" +
                "        return that.a();\n" +
                "    }\n" +
                "    public void d(){\n" +
                "        return that.d();\n" +
                "    }\n" +
                "    public void c(){\n" +
                "        return that.c();\n" +
                "    }\n" +
                "    public Wrapper f(){\n" +
                "        that.f();\n" +
                "        return this;\n" +
                "    }\n" +
                "    public Wrapper copy(){\n" +
                "        that.copy();\n" +
                "        return this;\n" +
                "    }\n" +
                "    public Wrapper e(){\n" +
                "        that.e();\n" +
                "        return this;\n" +
                "    }\n" +
                "}\n" +
                "interface If0{\n" +
                "    void a();\n" +
                "    void b();\n" +
                "    void c();\n" +
                "    void d();\n" +
                "}\n" +
                "interface If2{\n" +
                "    If0 a();\n" +
                "    If0 b();\n" +
                "}\n" +
                "interface If1 extends If0,If2 {};\n" +
                "interface If3  {\n" +
                "    If1 a();\n" +
                "}\n",
            new ClassBuilder((FluentBuilderImpl) fluent).build()
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
        Assertions.assertEquals("public static If6 start(){\n" +
                "    return new Wrapper();\n" +
                "}\n" +
                "public static class Wrapper implements If0,If2,If1,If4,If3,If6,If5{\n" +
                "    private final javax0.geci.fluent.internal.TestClassBuilder.TestClass that;\n" +
                "    public Wrapper(){\n" +
                "        this.that = new javax0.geci.fluent.internal.TestClassBuilder.TestClass();\n" +
                "    }\n" +
                "    public void b(){\n" +
                "        return that.b();\n" +
                "    }\n" +
                "    public void a(){\n" +
                "        return that.a();\n" +
                "    }\n" +
                "    public void d(){\n" +
                "        return that.d();\n" +
                "    }\n" +
                "    public void c(){\n" +
                "        return that.c();\n" +
                "    }\n" +
                "    public Wrapper f(){\n" +
                "        that.f();\n" +
                "        return this;\n" +
                "    }\n" +
                "    public Wrapper copy(){\n" +
                "        that.copy();\n" +
                "        return this;\n" +
                "    }\n" +
                "    public Wrapper e(){\n" +
                "        that.e();\n" +
                "        return this;\n" +
                "    }\n" +
                "}\n" +
                "interface If0{\n" +
                "    void a();\n" +
                "    void b();\n" +
                "    void c();\n" +
                "    void d();\n" +
                "}\n" +
                "interface If2{\n" +
                "    If1 a();\n" +
                "    If1 b();\n" +
                "}\n" +
                "interface If1 extends If0,If2 {};\n" +
                "interface If3  {\n" +
                "    If1 c();\n" +
                "}\n" +
                "interface If5{\n" +
                "    If3 a();\n" +
                "    If3 b();\n" +
                "}\n" +
                "interface If4 extends If3,If5 {};\n" +
                "interface If6  {\n" +
                "    If4 a();\n" +
                "}\n",
            new ClassBuilder((FluentBuilderImpl) fluent).build()
        );
    }

    @Test
    public void testOptionalInOptionalTreeBuildup() {
        var f = FluentBuilder.from(TestClass.class);
        var aOrB = f.oneOf(f.optional("a"), f.one("b"));
        var fluent = f.one("a")
            .optional(aOrB)
            .one("c").cloner("copy");
        Assertions.assertEquals("public static If5 start(){\n" +
                "    return new Wrapper();\n" +
                "}\n" +
                "public static class Wrapper implements If0,If2,If1,If4,If3,If5{\n" +
                "    private final javax0.geci.fluent.internal.TestClassBuilder.TestClass that;\n" +
                "    public Wrapper(javax0.geci.fluent.internal.TestClassBuilder.TestClass that){\n" +
                "        this.that = that;\n" +
                "    }\n" +
                "    public Wrapper(){\n" +
                "        this.that = new javax0.geci.fluent.internal.TestClassBuilder.TestClass();\n" +
                "    }\n" +
                "    public Wrapper b(){\n" +
                "        var next = new Wrapper(that.copy());\n" +
                "        next.b();\n" +
                "        return next;\n" +
                "    }\n" +
                "    public Wrapper a(){\n" +
                "        var next = new Wrapper(that.copy());\n" +
                "        next.a();\n" +
                "        return next;\n" +
                "    }\n" +
                "    public Wrapper d(){\n" +
                "        var next = new Wrapper(that.copy());\n" +
                "        next.d();\n" +
                "        return next;\n" +
                "    }\n" +
                "    public void c(){\n" +
                "        return that.c();\n" +
                "    }\n" +
                "    public Wrapper f(){\n" +
                "        var next = new Wrapper(that.copy());\n" +
                "        next.f();\n" +
                "        return next;\n" +
                "    }\n" +
                "    public Wrapper e(){\n" +
                "        var next = new Wrapper(that.copy());\n" +
                "        next.e();\n" +
                "        return next;\n" +
                "    }\n" +
                "}\n" +
                "interface If0  {\n" +
                "    void c();\n" +
                "}\n" +
                "interface If2  extends If0 {\n" +
                "    If0 a();\n" +
                "}\n" +
                "interface If3  {\n" +
                "    If0 b();\n" +
                "}\n" +
                "interface If4 extends If2,If3{\n" +
                "}\n" +
                "interface If1 extends If0,If4 {};\n" +
                "interface If5  {\n" +
                "    If1 a();\n" +
                "}\n",
            new ClassBuilder((FluentBuilderImpl) fluent).build()
        );
    }

    public static class TestClass {
        public TestClass copy() {
            return this;
        }

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
