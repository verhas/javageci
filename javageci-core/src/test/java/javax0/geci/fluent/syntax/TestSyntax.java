package javax0.geci.fluent.syntax;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class TestSyntax {
    private static final String EXPECTED = "kw(String) (noParameters|parameters|(parameter parameter*))? regex* usage help executor build";

    @Test
    @DisplayName("The whole syntax is defined in a single expression")
    void wholeSyntaxDefinedInOne() {
        var klass = FluentBuilder.from(MyClass.class);
        var sut = klass
                .syntax("kw(String) ( noParameters | parameters | parameter+ )? regex* usage help executor build");
        Assertions.assertEquals(EXPECTED, sut.toString());
    }

    @Test
    @DisplayName("The syntax is defined in two parts with interface name")
    void splitAndInterfaceName() {
        var klass = FluentBuilder.from(MyClass.class);
        var sut = klass
                .syntax("kw(String) ( noParameters | parameters | parameter+ )? regex* usage help executor").name("SpecialName").syntax("build");
        Assertions.assertEquals(EXPECTED, sut.toString());
    }

    @Test
    @DisplayName("The syntax is defined in two parts with interface name and fluent api def in the middle")
    void splitAndMixedName() {
        var klass = FluentBuilder.from(MyClass.class);
        var sut = klass
                .syntax("kw(String) ( noParameters | parameters | parameter+ )?").one(klass.zeroOrMore("regex")).syntax("usage help executor").name("").syntax("build");
        Assertions.assertEquals(EXPECTED, sut.toString());
    }

    @Test
    @DisplayName("The syntax is split in an invalid way")
    void wrongSplitting() {
        var klass = FluentBuilder.from(MyClass.class);
        Assertions.assertThrows(GeciException.class, () ->
                klass.syntax("kw(String) ( noParameters | parameters | ").oneOrMore("parameter").syntax(")? regex* usage help executor build"));
    }

    /**
     * We need this class here, because the fluent builder actually checks that the methods really exist.
     */
    static class MyClass {
        private void kw(String s) {
        }

        private void noParameters() {
        }

        private void parameters(Set<String> parset) {
        }

        private void parameter(String s) {
        }

        private void regex(String name, String pattern) {
        }

        private void usage(String s) {
        }

        private void help(String s) {
        }

        private void executor(String s) {
        }

        private void build() {
        }

    }
}
