package javax0.geci.tutorials.simple;

import javax0.geci.api.Source;
import javax0.geci.engine.Geci;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestSimpleGrammar {

    public static FluentBuilder defineSimpleGrammar() {
        FluentBuilder b = FluentBuilder.from(SimpleGrammar.class);
        return b.oneOf(
                b.one("singleWord"),
                b.one("parameterisedWord"),
                b.one("word1").optional("optionalWord"),
                b.one("word2").oneOf("wordChoiceA", "wordChoiceB"),
                b.oneOrMore("word3"))
                .one("end");
    }

    public static FluentBuilder defineSimpleGrammar_syntax() {
        FluentBuilder b = FluentBuilder.from(SimpleGrammar.class);
        return b.syntax(
                "(singleWord | parameterisedWord | (word1 optionalWord?) | (word2 (wordChoiceA | wordChoiceB)) | word3+) end");
    }

    @Test
    void createGrammar() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(geci
                        .source(
                                maven().module("javageci-examples").mainSource())
                        .source(
                                Source.Set.set("java"),
                                "./src/test/java",
                                "./javageci-examples/src/test/java")
                        .register(new Fluent())
                        .generate(),
                geci.failed());
    }

    @Test
    @DisplayName("Test generated simple grammar")
    void testGeneratedSimpleGrammar() {
        var sut = defineSimpleGrammar();
        sut.optimize();
        Assertions.assertEquals(
                "(parameterisedWord|singleWord|(word1 optionalWord?)|" +
                        "(word2 (wordChoiceA|wordChoiceB))|" +
                        "(word3 word3*)) " +
                        "end",
                sut.toString());
    }

    @Test
    @DisplayName("Test generated simple grammar")
    void testGeneratedSimpleGrammarFromSyntax() {
        var sut = defineSimpleGrammar_syntax();
        sut.optimize();
        Assertions.assertEquals(
                "(parameterisedWord|" +
                        "singleWord|" +
                        "(word1 optionalWord?)|" +
                        "(word2 (wordChoiceA|wordChoiceB))|" +
                        "(word3 word3*))" +
                        " end",
                sut.toString());
    }

    /*
     * Test all possible call chains. If the fluent API generation fails then this method will not compile in the next
     * compilation round.
     * <p>
     * In that case
     *
     * 1. this method has to be commented out,
     * 2. bug in the code generation fixed
     * 3. run the code generation again
     * 4. remove commenting out.
     */
    @Test
    @DisplayName("Call all empty methods on simple grammar")
    public void test() {
        SimpleGrammar.start().word1().optionalWord().end();
        SimpleGrammar.start().word1().end();
        SimpleGrammar.start().word2().wordChoiceA().end();
        SimpleGrammar.start().word2().wordChoiceB().end();
        SimpleGrammar.start().word3().end();
        SimpleGrammar.start().word3().word3().end();
        SimpleGrammar.start().word3().word3().word3().end();
    }

}
