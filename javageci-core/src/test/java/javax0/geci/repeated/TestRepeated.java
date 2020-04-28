package javax0.geci.repeated;

import javax0.geci.api.GeciException;
import javax0.geci.engine.Source;
import javax0.geci.repeated.sutclasses.ContainsTemplate;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.CompoundParamsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestRepeated {

    @Test
    @DisplayName("Properly generates Repeated")
    void testHappyPath() throws Exception {
        // GIVEN
        final var sut = Repeated.builder()
                            .values("inc|1,inc2|2")
                            .selector("repeatedPart")
                            .define((ctx, s) -> {
                                final var p = s.split("\\|");
                                ctx.segment().param("method", p[0], "value", p[1]);
                            }).build();
        final var source = Source.mock(sut).lines("package javax0.geci.repeated.sutclasses;\n" +
                                                      "\n" +
                                                      "public class ContainsTemplate {\n" +
                                                      "    \n" +
                                                      "/*TEMPLATE repeatedPart\n" +
                                                      "public int {{method}}(int a){\n" +
                                                      "    return a+{{value}};\n" +
                                                      "}    \n" +
                                                      "*/\n" +
                                                      "\n" +
                                                      "//<editor-fold id=\"repeatedPart\">\n" +
                                                      "//</editor-fold>\n" +
                                                      "\n" +
                                                      "}\n")
                               .getSource();
        final CompoundParams global = new CompoundParamsBuilder("repeated").build();
        // WHEN
        sut.process(source, null, global);

        // THEN
        source.consolidate();
        Assertions.assertEquals("package javax0.geci.repeated.sutclasses;\n" +
                                    "\n" +
                                    "public class ContainsTemplate {\n" +
                                    "    \n" +
                                    "/*TEMPLATE repeatedPart\n" +
                                    "public int {{method}}(int a){\n" +
                                    "    return a+{{value}};\n" +
                                    "}    \n" +
                                    "*/\n" +
                                    "\n" +
                                    "//<editor-fold id=\"repeatedPart\">\n" +
                                    "public int inc(int a){\n" +
                                    "    return a+1;\n" +
                                    "}    \n" +
                                    "public int inc2(int a){\n" +
                                    "    return a+2;\n" +
                                    "}    \n" +
                                    "//</editor-fold>\n" +
                                    "\n" +
                                    "}",
            String.join("\n", source.getLines()));
    }

    @Test
    @DisplayName("Throws GeciException when there is no segment")
    void testThrowsGeciExceptionWhenThereIsNoSegment() throws Exception {
        // GIVEN
        final var sut = Repeated.builder()
            .values("inc|1,inc2|2")
            .selector("repeatedPart")
            .define((ctx, s) -> {
                final var p = s.split("\\|");
                ctx.segment().param("method", p[0], "value", p[1]);
            }).build();
        final var source = Source.mock(sut).lines("package javax0.geci.repeated.sutclasses;\n" +
            "\n" +
            "public class ContainsTemplate {\n" +
            "    \n" +
            "/*TEMPLATE repeatedPart\n" +
            "public int {{method}}(int a){\n" +
            "    return a+{{value}};\n" +
            "}    \n" +
            "*/\n" +
            "\n" +
            "//<editor-fold id=\"bad segment name\">\n" +
            "//</editor-fold>\n" +
            "\n" +
            "}\n")
            .getSource();
        final CompoundParams global = new CompoundParamsBuilder("repeated").build();
        // WHEN
        Assertions.assertThrows(GeciException.class, ()-> sut.process(source, null, global));

    }

}
