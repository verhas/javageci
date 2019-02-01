package javax0.geci.buildfluent;

import javax0.geci.engine.Geci;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import javax0.geci.tests.fluent.ReplCommandBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReplCommandBuilderFluenterTest {

    @Test
    @DisplayName("Fluent API for the CommandDefinitionBuilder is up-to-date")
    void generateFluendAPI4CommandDefinitionBuilder() throws Exception {
        Assertions.assertFalse(new Geci()
                .source("../javageci-examples/src/main/java", "./javageci-examples/src/main/java")
                .register(new Fluent())
                .only("ReplCommandBuilder.java")
                .generate(),
            Geci.FAILED);
    }

    public static FluentBuilder sourceBuilderGrammar() {
        var klass = FluentBuilder.from(ReplCommandBuilder.class);
        return klass
            .syntax("kw(String) ( noParameters | parameters | parameter+ )?")
            .zeroOrMore("regex")
            .syntax("usage help executor")
            .name("CommandDefinitionBuilderReady")
            .syntax("build")
            ;
    }
}
