package javax0.geci.tests.repeated;

import javax0.geci.engine.Geci;
import javax0.geci.repeated.Repeated;
import javax0.geci.tools.CaseTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestRepeated {

    @Test
    @DisplayName("Execute the repeated code generation for the core generators source code")
    void coreGeneratorsRepeated() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source("./javageci-core/src/main/java/", "../javageci-core/src/main/java/")
                        .register(Repeated.builder()
                                //
                                .selector("configSetters")
                                .define((ctx, s) -> ctx.segment().param("setter", "set" + CaseTools.ucase(s)))
                                //
                                .selector("consumers")
                                .define((ctx, s) -> {
                                            String subtype;
                                            if (s.startsWith("process") && (s.endsWith(subtype = "Field") || s.endsWith(subtype = "Method") || s.endsWith(subtype = "Class"))) {
                                                ctx.segment().param("type", "BiConsumer<Context, " + subtype + ">",
                                                        "const", "BiNOOP");
                                            } else {
                                                ctx.segment().param("type", "Consumer<Context>",
                                                        "const", "NOOP");
                                            }
                                        }
                                )
                                //
                                .build())
                        .only(".*","\\.")
                        .ignore("\\.git")
                        .trace("target/trace.xml")
                        .generate()
                , geci.failed()
        );
    }
}
