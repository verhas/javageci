package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class TestClassBuilder {

    @Test
    public void testIncompleteBuildup() {
        var fluent = FluentBuilder.from(TestClass.class);
        Assertions.assertThrows(GeciException.class, () -> new ClassBuilder((FluentBuilderImpl) fluent).build());
    }

    private static void assertEqualToFile(String result, String resourceName) throws Exception {
        try (var output = Files.newOutputStream(
            FileSystems.getDefault().getPath("target", resourceName),
            StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            output.write(result.getBytes(StandardCharsets.UTF_8));
        }
        var is = TestClassBuilder.class.getResourceAsStream(resourceName);
        var expected = new String(is.readAllBytes(), StandardCharsets.UTF_8).replace("\r", "");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testTerminalsBuildup() throws Exception {
        var fluent = FluentBuilder.from(TestClass.class)
            .one("a")
            .optional("b")
            .oneOf("a", "b", "c", "d");
        var result = new ClassBuilder((FluentBuilderImpl) fluent).build();
        assertEqualToFile(result, "testTerminalsBuildup.txt");
    }

    @Test
    public void testTerminalsBuildupFullSample() throws Exception {
        var fluent = FluentBuilder.from(TestClass.class)
            .one("a")
            .optional("b")
            .oneOrMore("c")
            .zeroOrMore("d")
            .oneOf("a", "b");
        var result = new ClassBuilder((FluentBuilderImpl) fluent).build();
        assertEqualToFile(result, "testTerminalsBuildupFullSample.txt");
    }

    @Test
    public void testTreeBuildup() throws Exception {
        var f = FluentBuilder.from(TestClass.class);
        var aOrB = f.oneOf("a", "b");
        var fluent = f.one("a")
            .optional(aOrB)
            .oneOf("a", "b", "c", "d");
        var result = new ClassBuilder((FluentBuilderImpl) fluent).build();
        assertEqualToFile(result, "testTreeBuildup.txt");
    }

    @Test
    public void testComplexTreeBuildup() throws Exception {
        var f = FluentBuilder.from(TestClass.class);
        var aOrB = f.oneOf("a", "b");
        var fluent = f.one("a")
            .optional(aOrB)
            .one("c")
            .zeroOrMore(aOrB)
            .oneOf("a", "b", "c", "d");
        var result = new ClassBuilder((FluentBuilderImpl) fluent).build();
        assertEqualToFile(result, "testComplexTreeBuildup.txt");
    }

    @Test
    public void testOptionalInOptionalTreeBuildup() throws Exception {
        var f = FluentBuilder.from(TestClass.class);
        var aOrB = f.oneOf(f.optional("a"), f.one("b"));
        var fluent = f.one("a")
            .optional(aOrB)
            .one("c").cloner("copy");
        var result = new ClassBuilder((FluentBuilderImpl) fluent).build();
        assertEqualToFile(result, "testOptionalInOptionalTreeBuildup.txt");
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
