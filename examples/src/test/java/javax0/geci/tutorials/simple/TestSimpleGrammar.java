package javax0.geci.tutorials.simple;

import javax0.geci.api.Source;
import javax0.geci.engine.Geci;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestSimpleGrammar {

    @Test
    public void createGrammar() throws Exception {
        if (new Geci().source(maven().module("examples").javaSource())
            .source(Source.Set.set("java"), "./src/test/java", "./examples/src/test/java")
            .register(new Fluent()).generate()) {
            Assertions.fail("Code was changed during test phase.");
        }
    }

    public static FluentBuilder defineSimpleGrammar() {
        FluentBuilder b = FluentBuilder.from(SimpleGrammar.class);
        return b.oneOf(
            b.one("singleWord"),
            b.one("word1").optional("optionalWord"),
            b.one("word2").oneOf("wordChoiceA", "wordChoiceB"),
            b.oneOf("word3")).one("end");
    }

}
