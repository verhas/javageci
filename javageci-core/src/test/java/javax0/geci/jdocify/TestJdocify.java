package javax0.geci.jdocify;

import javax0.geci.engine.testsupport.GeneratorTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestJdocify {
    // used reflective by tested code
    final static String REPLACE = "replaced indeed";

    private static void check(GeneratorTester tester) throws Exception {
        tester.test();
        Assertions.assertEquals(tester.expected(), tester.actual());
    }

    private GeneratorTester generator() {
        return GeneratorTester.generator(Jdocify.builder().build()).klass(TestJdocify.class);
    }

    @Test
    @DisplayName("Simple CODE replacement without template works")
    void simpleReplace() throws Exception {
        check(generator()
            .source(
                "/**",
                " * This is to <!--CODE REPLACE-->{@code this is to be replaced} be replaced",
                "*/")
            .expected("/**",
                " * This is to <!--CODE REPLACE-->{@code replaced indeed} be replaced",
                "*/")
        );
    }

    @Test
    @DisplayName("Multi line CODE replacement without template works")
    void multiReplace() throws Exception {
        check(generator()
            .source(
                "/**",
                " * This is to <!--CODE",
                " * REPLACE-->{@code this is ",
                " * to be replaced} be replaced",
                "*/"
            )
            .expected(
                "/**",
                " * This is to <!--CODE",
                " * REPLACE-->{@code replaced indeed} be replaced",
                "*/"
            )
        );
    }

    @Test
    @DisplayName("Multi line CODE replacement is not hurt if only reformatted")
    void multiReplaceNon() throws Exception {
        check(generator()
            .source(
                "/**",
                " * This is to <!--CODE",
                " * REPLACE-->{@code replaced",
                " * indeed} be replaced",
                "*/"
            )
            .noChange()
        );
    }

    @Test
    @DisplayName("Simple CODE replacement with template works")
    void templatedReplace() throws Exception {
        check(generator()
            .source(
                "/**",
                " * This is to <!--CODE REPLACE $REPLACE x-->{@code this is to be replaced} be replaced",
                "*/"
            )
            .expected(
                "/**",
                " * This is to <!--CODE REPLACE $REPLACE x-->{@code $replaced indeed x} be replaced",
                "*/"
            )
        );
    }

}
