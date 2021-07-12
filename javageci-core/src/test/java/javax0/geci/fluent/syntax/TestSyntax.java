package javax0.geci.fluent.syntax;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

public class TestSyntax {
    private static final String EXPECTED = "kw(String) (noParameters|parameters|(parameter parameter*))? regex* usage help executor build";

    @Test
    @DisplayName("The whole syntax is defined in a single expression")
    void wholeSyntaxDefinedInOne() {
        var klass = FluentBuilder.from(MyClass.class);
        var sut = klass
                .syntax("kw(String) ( noParameters | parameters | parameter+ )? regex* usage help executor build");
        sut.optimize();
        Assertions.assertEquals(EXPECTED, sut.toString());
    }

    @Test
    @DisplayName("The syntax is defined in two parts with interface name")
    void splitAndInterfaceName() {
        var klass = FluentBuilder.from(MyClass.class);
        var sut = klass
                .syntax("kw(String) ( noParameters | parameters | parameter+ )? regex* usage help executor")
                .name("SpecialName")
                .syntax("build");
        sut.optimize();
        Assertions.assertEquals(EXPECTED, sut.toString());
    }

    @Test
    @DisplayName("The syntax is defined in two 'syntax' parts with interface name and fluent api def in the middle")
    void splitAndMixedName() {
        var klass = FluentBuilder.from(MyClass.class);
        var sut = klass
                .syntax("kw(String) ( noParameters | parameters | parameter+ )?")
                .one(klass.zeroOrMore("regex"))
                .syntax("usage help executor")
                .name("").syntax("build");
        sut.optimize();
        Assertions.assertEquals(EXPECTED, sut.toString());
    }

    @Test
    @DisplayName("The syntax tree is flattened when there are multiple level of list of the same type")
    void flattenExpressions() {
        var klass = FluentBuilder.from(MyClass.class);
        var sut = klass
                .oneOf(klass.oneOf("regex", "help", "executor"), klass.oneOf("usage", "kw")).one("build");
        sut.optimize();
        Assertions.assertEquals("(executor|help|kw|regex|usage) build", sut.toString());
    }

    @Test
    @DisplayName("The syntax tree is flattened when there are multiple deel level of list of the same type")
    void flattenDeepExpressions() {
        var sut = FluentBuilder.from(MyClass.class)
                .syntax(" executor | help | ( executor|regex) | regex |   (usage (executor |kw |( kw| kw)))");
        sut.optimize();
        Assertions.assertEquals("(executor|help|regex|(usage (executor|kw)))", sut.toString());
    }

    @Test
    @DisplayName("Throws exception when syntax is split in an invalid way")
    void wrongSplitting() {
        var klass = FluentBuilder.from(MyClass.class);
        Assertions.assertThrows(GeciException.class, () ->
                klass.syntax("kw(String) ( noParameters | parameters | ")
                        .oneOrMore("parameter")
                        .syntax(")? regex* usage help executor build"));
    }

    @Test
    @DisplayName("or-ed alternatives do not need parentheses, | has higher precedence than listing")
    void syntaxExample1() {
        var klass = FluentBuilder.from(MyClass.class);
        final var sut = klass.syntax("kw (parameter | (usage help))");
        Assertions.assertEquals("kw (parameter|(usage help))", sut.toString());
        final var sutWithoutParentheses = klass.syntax("kw parameter | (usage help)");
        sut.optimize();
        Assertions.assertEquals("kw (parameter|(usage help))", sutWithoutParentheses.toString());
    }

    @Test
    @DisplayName("superfluous parentheses are ignored")
    void syntaxExample2() {
        var klass = FluentBuilder.from(MyClass.class);
        final var sut = klass.syntax("kw (((parameter | ((usage (help))))))");
        sut.optimize();
        Assertions.assertEquals("kw (parameter|(usage help))", sut.toString());
    }

    @Test
    @DisplayName("chained modifiers are okay")
    void syntaxChainedModifier_tpq() {
        var klass = FluentBuilder.from(MyClass.class);
        final var sut = klass.syntax("(parameter+)?");
        sut.optimize();
        Assertions.assertEquals("parameter*", sut.toString());
    }

    /**
     * This test checks the syntax analysis and structure optimization of the built syntax tree in case the
     * specification contains
     * <pre>{@code ( terminal_symbol X ) Y} </pre>
     * or
     * <pre>{@code ( comlex X ) Y} </pre>
     * structure where {@code X} and {@code Y} are one of the {@code *}, {@code +} and {@code ?} modifiers. These
     * specifications are redundant and express a simpler case. The Map in the code {@code modifierPairs} describe
     * all the possible pairing and their effective results.
     */
    @Test
    @DisplayName("chained modifiers are okay and are optimized")
    void syntaxChainedModifier() {
        final Map<String,String> modifierPairs = Map.of(
                "**", "*",
                "*?", "*",
                "*+", "*",
                "+*", "*",
                "+?", "*",
                "++", "+",
                "?*", "*",
                "??", "?",
                "?+", "*"
        );
        for (final var entry : modifierPairs.entrySet()) {
            final var key = entry.getKey();
            final var value = entry.getValue();
            terminal:
            {
                var klass = FluentBuilder.from(MyClass.class);
                final var sut = klass.syntax(String.format("(parameter%s)%s", key.substring(0, 1), key.substring(1)));
                sut.optimize();
                if (value.equals("+")) {
                    Assertions.assertEquals("parameter parameter*", sut.toString(), " for " + key);
                } else {
                    Assertions.assertEquals(String.format("parameter%s", value), sut.toString(), " for " + key);
                }
            }
            complex:
            {
                var klass = FluentBuilder.from(MyClass.class);
                final var sut = klass.syntax(String.format("((parameter kw)%s)%s", key.substring(0, 1), key.substring(1)));
                sut.optimize();
                if (value.equals("+")) {
                    Assertions.assertEquals("parameter kw (parameter kw)*", sut.toString(), " for " + key);
                } else {
                    Assertions.assertEquals(String.format("(parameter kw)%s", value), sut.toString(), " for " + key);
                }
            }
        }

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
