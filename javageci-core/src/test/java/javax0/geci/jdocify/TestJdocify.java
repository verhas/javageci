package javax0.geci.jdocify;

import javax0.geci.api.GeciException;
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
    @DisplayName("Simple CODE replacement with template that has { and } characters in it works/1")
    void simpleReplaceEmbeddedBrace1() throws Exception {
        check(generator()
            .source(
                "/**",
                " * This is to <!--CODE REPLACE ${REPLACE} ... ${REPLACE}-->{@code ${replaced indeed} ... ${replaced indeed}} be replaced",
                "*/")
            .noChange()
        );
    }

    @Test
    @DisplayName("Simple CODE replacement with template that has { and } characters in it works/2")
    void simpleReplaceEmbeddedBrace2() throws Exception {
        check(generator()
            .source(
                "/**",
                " * This is to <!--CODE REPLACE ${REPLACE} ... ${REPLACE}-->{@code} be replaced",
                "*/")
            .expected("/**",
                " * This is to <!--CODE REPLACE ${REPLACE} ... ${REPLACE}-->{@code ${replaced indeed} ... ${replaced indeed}} be replaced",
                "*/")
        );
    }

    @Test
    @DisplayName("Simple CODE replacement with template that has { and } characters in it works/3")
    void simpleReplaceEmbeddedBrace3() throws Exception {
        check(generator()
            .source(
                "/**",
                " * This is to <!--CODE REPLACE ${REPLACE}-->{@code${rirareplacidea}} be replaced",
                "*/")
            .expected("/**",
                " * This is to <!--CODE REPLACE ${REPLACE}-->{@code ${replaced indeed}} be replaced",
                "*/")
        );
    }

    @Test
    @DisplayName("Throws exception when {@code is not closed inside the comment")
    void unclosedCode() throws Exception {
        Assertions.assertThrows(GeciException.class, () -> generator()
            .source(
                "/**",
                " * This is to <!--CODE REPLACE ${REPLACE}-->{@code${rirareplacidea} be replaced",
                "*/")
            .test()
        );
    }

    @Test
    @DisplayName("Throws exception when <!--CODE is not closed")
    void unclosedCODE() throws Exception {
        Assertions.assertThrows(GeciException.class, () ->
            generator()
                .source(
                    "/**",
                    " * This is to <!--CODE REPLACE ${REPLACE}->{@code${rirareplacidea} be replaced",
                    "*/")
                .test()
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
    @DisplayName("Simple line CODE insertion without template when there is nothing in the {@code}")
    void simpleInsert() throws Exception {
        check(generator()
            .source(
                "/**",
                " * This is to <!--CODE",
                " * REPLACE-->{@code} be replaced",
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
    @DisplayName("Simple line CODE insertion without template when there is a single space in the {@code } like here")
    void simpleInsertspace() throws Exception {
        check(generator()
            .source(
                "/**",
                " * This is to <!--CODE",
                " * REPLACE-->{@code } be replaced",
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
    @DisplayName("Simple line CODE insertion without template when there is some space and \n in the {@code \n * } like here")
    void multiInsertspace() throws Exception {
        check(generator()
            .source(
                "/**",
                " * This is to <!--CODE",
                " * REPLACE-->{@code",
                " * } be replaced",
                "*/"
            )
            .expected(
                "/**",
                " * This is to <!--CODE",
                " * REPLACE-->{@code",
                " * replaced indeed} be replaced",
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

    @Test
    @DisplayName("Simple DEFINE and use of the variable")
    void simpleDEFINE() throws Exception {
        check(generator()
            .source(
                "//DEFINE VARIABLE=This is the value of the variable.",
                "/**",
                " * This is to <!--VARIABLE-->This is not the code of the variable<!--/-->",
                "*/")
            .expected(
                "//DEFINE VARIABLE=This is the value of the variable.",
                "/**",
                " * This is to <!--VARIABLE-->This is the value of the variable.<!--/-->",
                "*/")
        );
    }

    @Test

    @DisplayName("Simple DEFINE and use of the variable when original value is reformatted")
    void multilDEFINE() throws Exception {
        check(generator()
            .source(
                "//DEFINE VARIABLE=This is the value of the variable.",
                "/**",
                " * This is to <!--VARIABLE-->This is not the",
                " * code of the variable<!--/-->",
                " */")
            .expected(
                "//DEFINE VARIABLE=This is the value of the variable.",
                "/**",
                " * This is to <!--VARIABLE-->This is the value of the variable.<!--/-->",
                " */")
        );
    }

    @DisplayName("Simple DEFINE and use of the variable when original value is reformatted but is okay")
    void multilDEFINERemains() throws Exception {
        check(generator()
            .source(
                "//DEFINE VARIABLE=This is the value of the variable.",
                "/**",
                " * This is to <!--VARIABLE-->This is the",
                " * value of the variable.<!--/-->",
                " */").noChange()
        );
    }
}
