package javax0.geci.iterate;

import javax0.geci.core.annotations.Repeated;
import javax0.geci.engine.Source;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.CompoundParamsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestIterate {

    @Test
    @DisplayName("Properly generates Repeated")
    void testHappyPath() throws Exception {
        // GIVEN
        final var sut = Iterate.builder().build();
        final var source = Source.mock(sut).lines("package javax0.geci.iterate.sutclasses;\n" +
                                                      "\n" +
                                                      "public class IteratePrimitives {\n" +
                                                      "    \n" +
                                                      "    /* TEMPLATE\n" +
                                                      "    {{type}} get_{{type}}Value(){\n" +
                                                      "      {{type}} z = 0;\n" +
                                                      "      return z;\n" +
                                                      "    }\n" +
                                                      "    LOOP type=int|long|short\n" +
                                                      "    EDITOR-FOLD-ID getters\n" +
                                                      "     */\n" +
                                                      "    //<editor-fold id=\"getters\">\n" +
                                                      "    //</editor-fold>\n" +
                                                      "}\n")
                               .getSource();
        final CompoundParams global = new CompoundParamsBuilder("iterate").build();
        // WHEN
        sut.process(source, null, global);

        // THEN
        source.consolidate();
        Assertions.assertEquals("package javax0.geci.iterate.sutclasses;\n" +
                                    "\n" +
                                    "public class IteratePrimitives {\n" +
                                    "    \n" +
                                    "    /* TEMPLATE\n" +
                                    "    {{type}} get_{{type}}Value(){\n" +
                                    "      {{type}} z = 0;\n" +
                                    "      return z;\n" +
                                    "    }\n" +
                                    "    LOOP type=int|long|short\n" +
                                    "    EDITOR-FOLD-ID getters\n" +
                                    "     */\n" +
                                    "    //<editor-fold id=\"getters\">\n" +
                                    "        int get_intValue(){\n" +
                                    "          int z = 0;\n" +
                                    "          return z;\n" +
                                    "        }\n" +
                                    "\n" +
                                    "        long get_longValue(){\n" +
                                    "          long z = 0;\n" +
                                    "          return z;\n" +
                                    "        }\n" +
                                    "\n" +
                                    "        short get_shortValue(){\n" +
                                    "          short z = 0;\n" +
                                    "          return z;\n" +
                                    "        }\n" +
                                    "\n" +
                                    "    //</editor-fold>\n" +
                                    "}",
            String.join("\n", source.getLines()));
    }

}
