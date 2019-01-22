package javax0.geci.buildfluent;
import javax0.geci.engine.Geci;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import javax0.geci.tests.fluent.ReplCommandBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class ReplCommandBuilderFluenterTest {


    @Test
    @DisplayName("Fluent API for the CommandDefinitionBuilder is up-to-date")
    void generateFluendAPI4CommandDefinitionBuilder() throws Exception {
        if (new Geci().source(maven().mainSource()).register(new Fluent()).generate()) {
            Assertions.fail(Geci.FAILED);
        }
    }

    public static FluentBuilder sourceBuilderGrammar() {
        var klass = FluentBuilder.from(ReplCommandBuilder.class);
        return klass
                .one("kw")
                .optional(klass.oneOf(klass.one("noParameters"), klass.one("parameters"), klass.oneOrMore("parameter")))
                .zeroOrMore("regex")
                .one("usage")
                .one("help")
                .one("executor").name("CommandDefinitionBuilderReady")
                .one("build");
    }
}
