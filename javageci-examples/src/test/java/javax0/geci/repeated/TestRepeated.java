package javax0.geci.repeated;

import javax0.geci.engine.Geci;
import javax0.geci.tools.CaseTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestRepeated {

    @Test
    @DisplayName("Execute the repeated code generation for the core generators source code")
    void coreGeneratorsRepeated() throws Exception {
        Assertions.assertFalse(
            new Geci().source(maven().module("javageci-core").mainSource())
                .register(Repeated.builder()

                    .selector("configSetters")
                    .define((ctx, s) -> ctx.segment().param("setter", "set" + CaseTools.ucase(s)))
                    .template("```private void {{setter}}(String template) {\n" +
                        "            templates().{{value}} = template;\n" +
                        "        }```")
                    .build())
                .generate()
            , Geci.FAILED
        );
    }
}
