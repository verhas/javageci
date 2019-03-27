package javax0.geci.tutorials.simple;

import javax0.geci.api.Source;
import javax0.geci.engine.Geci;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
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
    public void createGrammar() throws Exception {
        if (new Geci().source(maven().module("javageci-examples").mainSource())
                .source(Source.Set.set("java"), "./src/test/java", "./javageci-examples/src/test/java")
                .register(new Fluent()).generate()) {
            Assertions.fail(Geci.FAILED);
        }
    }

    public void test() {
        SimpleGrammar.start().word3().word3().word3().end();
    }

}
