package javax0.geci.iterate;

import javax0.geci.engine.Source;
import javax0.geci.tools.CaseTools;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.CompoundParamsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestIterate {
    @Test
    @DisplayName("Properly generates Iterated text with single value")
    void testSingleValue() throws Exception {
        // GIVEN
        final var sut = Iterate.builder().build();
        final var source = Source.mock(sut).lines("package javax0.geci.iterate.sutclasses;\n" +
            "\n" +
            "public class IteratePrimitives {\n" +
            "\n" +
            "    /* TEMPLATE\n" +
            "    {{type}} get_{{type}}Value(){\n" +
            "      {{type}} z = 0;\n" +
            "      return z;\n" +
            "    }\n" +
            "    \n" +
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
                "\n" +
                "    /* TEMPLATE\n" +
                "    {{type}} get_{{type}}Value(){\n" +
                "      {{type}} z = 0;\n" +
                "      return z;\n" +
                "    }\n" +
                "    \n" +
                "    LOOP type=int|long|short\n" +
                "    EDITOR-FOLD-ID getters\n" +
                "     */\n" +
                "    //<editor-fold id=\"getters\">\n" +
                "    int get_intValue(){\n" +
                "      int z = 0;\n" +
                "      return z;\n" +
                "    }\n" +
                "\n" +
                "    long get_longValue(){\n" +
                "      long z = 0;\n" +
                "      return z;\n" +
                "    }\n" +
                "\n" +
                "    short get_shortValue(){\n" +
                "      short z = 0;\n" +
                "      return z;\n" +
                "    }\n" +
                "\n" +
                "    //</editor-fold>\n" +
                "}",
            String.join("\n", source.getLines()));
    }

    @Test
    @DisplayName("Properly generates Iterated text with multiple value")
    void testMultipleValues() throws Exception {
        // GIVEN
        final var sut = Iterate.builder().build();
        final var source = Source.mock(sut).lines("package javax0.geci.iterate.sutclasses;\n" +
            "\n" +
            "public class IterateOverMultipleValues {\n" +
            "    /* TEMPLATE\n" +
            "    {{type}} get_{{type}}Value(){\n" +
            "      {{type}} {{variable}} = 0;\n" +
            "      return {{variable}};\n" +
            "    }\n" +
            "\n" +
            "    LOOP type,variable=int,i|long,l|short,s\n" +
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
                "public class IterateOverMultipleValues {\n" +
                "    /* TEMPLATE\n" +
                "    {{type}} get_{{type}}Value(){\n" +
                "      {{type}} {{variable}} = 0;\n" +
                "      return {{variable}};\n" +
                "    }\n" +
                "\n" +
                "    LOOP type,variable=int,i|long,l|short,s\n" +
                "    EDITOR-FOLD-ID getters\n" +
                "     */\n" +
                "    //<editor-fold id=\"getters\">\n" +
                "    int get_intValue(){\n" +
                "      int i = 0;\n" +
                "      return i;\n" +
                "    }\n" +
                "\n" +
                "    long get_longValue(){\n" +
                "      long l = 0;\n" +
                "      return l;\n" +
                "    }\n" +
                "\n" +
                "    short get_shortValue(){\n" +
                "      short s = 0;\n" +
                "      return s;\n" +
                "    }\n" +
                "\n" +
                "    //</editor-fold>\n" +
                "}",
            String.join("\n", source.getLines()));
    }

    @Test
    @DisplayName("Properly generates Iterated text with multiple value using define() in configuration")
    void testMultipleValuesWithDefine() throws Exception {
        // GIVEN
        final var sut = Iterate.builder().define( ctx -> ctx.segment().param("Type", CaseTools.ucase(ctx.segment().getParam("type").orElse("")))).build();
        final var source = Source.mock(sut).lines("package javax0.geci.iterate.sutclasses;\n" +
            "\n" +
            "public class IterateOverValuesWithDefine {\n" +
            "    /* TEMPLATE\n" +
            "    {{type}} get_{{Type}}Value(){\n" +
            "      {{type}} {{variable}} = 0;\n" +
            "      return {{variable}};\n" +
            "    }\n" +
            "\n" +
            "    LOOP type,variable=int,i|long,l|short,s\n" +
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
                "public class IterateOverValuesWithDefine {\n" +
                "    /* TEMPLATE\n" +
                "    {{type}} get_{{Type}}Value(){\n" +
                "      {{type}} {{variable}} = 0;\n" +
                "      return {{variable}};\n" +
                "    }\n" +
                "\n" +
                "    LOOP type,variable=int,i|long,l|short,s\n" +
                "    EDITOR-FOLD-ID getters\n" +
                "     */\n" +
                "    //<editor-fold id=\"getters\">\n" +
                "    int get_IntValue(){\n" +
                "      int i = 0;\n" +
                "      return i;\n" +
                "    }\n" +
                "\n" +
                "    long get_LongValue(){\n" +
                "      long l = 0;\n" +
                "      return l;\n" +
                "    }\n" +
                "\n" +
                "    short get_ShortValue(){\n" +
                "      short s = 0;\n" +
                "      return s;\n" +
                "    }\n" +
                "\n" +
                "    //</editor-fold>\n" +
                "}",
            String.join("\n", source.getLines()));
    }

    @Test
    @DisplayName("Properly generates Iterated text with multiple value with altered SEP values")
    void testSetSepValues() throws Exception {
        // GIVEN
        final var sut = Iterate.builder().build();
        final var source = Source.mock(sut).lines("package javax0.geci.iterate.sutclasses;\n" +
            "\n" +
            "public class SettingSepValues {\n" +
            "    /* TEMPLATE\n" +
            "    {{type}} get_{{type}}Value(){\n" +
            "      {{type}} {{variable}} = 0;\n" +
            "      return {{variable}};\n" +
            "    }\n" +
            "    SEP1 +\n" +
            "    SEP2 |||\n" +
            "    LOOP type+variable=int+i|||long+l|||short+s\n" +
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
                "public class SettingSepValues {\n" +
                "    /* TEMPLATE\n" +
                "    {{type}} get_{{type}}Value(){\n" +
                "      {{type}} {{variable}} = 0;\n" +
                "      return {{variable}};\n" +
                "    }\n" +
                "    SEP1 +\n" +
                "    SEP2 |||\n" +
                "    LOOP type+variable=int+i|||long+l|||short+s\n" +
                "    EDITOR-FOLD-ID getters\n" +
                "     */\n" +
                "    //<editor-fold id=\"getters\">\n" +
                "    int get_intValue(){\n" +
                "      int i = 0;\n" +
                "      return i;\n" +
                "    }\n" +
                "    long get_longValue(){\n" +
                "      long l = 0;\n" +
                "      return l;\n" +
                "    }\n" +
                "    short get_shortValue(){\n" +
                "      short s = 0;\n" +
                "      return s;\n" +
                "    }\n" +
                "    //</editor-fold>\n" +
                "}",
            String.join("\n", source.getLines()));
    }

    @Test
    @DisplayName("Properly generates Iterated text with multiple value with escaped lines that would otherwise be interpreted as SEP values")
    void testSetEscapedSepValues() throws Exception {
        // GIVEN
        final var sut = Iterate.builder().build();
        final var source = Source.mock(sut).lines("package javax0.geci.iterate.sutclasses;\n" +
            "\n" +
            "public class EscapingLines {\n" +
            "        /* TEMPLATE\n" +
            "    {{type}} get_{{type}}Value(){\n" +
            "      {{type}} {{variable}} = 0;\n" +
            "      return {{variable}};\n" +
            "    }\n" +
            "    ESCAPE\n" +
            "    SEP1 +\n" +
            "    ESCAPE\n" +
            "    SEP2 |||\n" +
            "    LOOP type,variable=int,i|long,l|short,s\n" +
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
                "public class EscapingLines {\n" +
                "        /* TEMPLATE\n" +
                "    {{type}} get_{{type}}Value(){\n" +
                "      {{type}} {{variable}} = 0;\n" +
                "      return {{variable}};\n" +
                "    }\n" +
                "    ESCAPE\n" +
                "    SEP1 +\n" +
                "    ESCAPE\n" +
                "    SEP2 |||\n" +
                "    LOOP type,variable=int,i|long,l|short,s\n" +
                "    EDITOR-FOLD-ID getters\n" +
                "     */\n" +
                "    //<editor-fold id=\"getters\">\n" +
                "    int get_intValue(){\n" +
                "      int i = 0;\n" +
                "      return i;\n" +
                "    }\n" +
                "    SEP1 +\n" +
                "    SEP2 |||\n" +
                "    long get_longValue(){\n" +
                "      long l = 0;\n" +
                "      return l;\n" +
                "    }\n" +
                "    SEP1 +\n" +
                "    SEP2 |||\n" +
                "    short get_shortValue(){\n" +
                "      short s = 0;\n" +
                "      return s;\n" +
                "    }\n" +
                "    SEP1 +\n" +
                "    SEP2 |||\n" +
                "    //</editor-fold>\n" +
                "}",
            String.join("\n", source.getLines()));
    }

    @Test
    @DisplayName("Properly generates Iterated text with single value dangling")
    void testSingleValueWithoutExplicitEditorFold() throws Exception {
        // GIVEN
        final var sut = Iterate.builder().build();
        final var source = Source.mock(sut).lines("package javax0.geci.iterate.sutclasses;\n" +
            "\n" +
            "public class IteratePrimitives {\n" +
            "\n" +
            "    /* TEMPLATE\n" +
            "    {{type}} get_{{type}}Value(){\n" +
            "      {{type}} z = 0;\n" +
            "      return z;\n" +
            "    }\n" +
            "    \n" +
            "    LOOP type=int|long|short\n" +
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
                "\n" +
                "    /* TEMPLATE\n" +
                "    {{type}} get_{{type}}Value(){\n" +
                "      {{type}} z = 0;\n" +
                "      return z;\n" +
                "    }\n" +
                "    \n" +
                "    LOOP type=int|long|short\n" +
                "     */\n" +
                "    //<editor-fold id=\"getters\">\n" +
                "    int get_intValue(){\n" +
                "      int z = 0;\n" +
                "      return z;\n" +
                "    }\n" +
                "\n" +
                "    long get_longValue(){\n" +
                "      long z = 0;\n" +
                "      return z;\n" +
                "    }\n" +
                "\n" +
                "    short get_shortValue(){\n" +
                "      short z = 0;\n" +
                "      return z;\n" +
                "    }\n" +
                "\n" +
                "    //</editor-fold>\n" +
                "}",
            String.join("\n", source.getLines()));
    }

    @Test
    @DisplayName("Properly generates Iterated text with lines escaped and skipped")
    void testSingleValueWithSkippedLine() throws Exception {
        // GIVEN
        final var sut = Iterate.builder().build();
        final var source = Source.mock(sut).lines("package javax0.geci.iterate.sutclasses;\n" +
                                                      "\n" +
                                                      "public class SkippedLines {\n" +
                                                      "    /* TEMPLATE\n" +
                                                      "    /**\n" +
                                                      "     * A simple zero getter serving as a test example\n" +
                                                      "     * @return zero in the type {{type}}\n" +
                                                      "    ESCAPE\n" +
                                                      "     */\n" +
                                                      "    // SKIP\n" +
                                                      "    /*\n" +
                                                      "    {{type}} get_{{type}}Value(){\n" +
                                                      "      {{type}} {{variable}} = 0;\n" +
                                                      "      return {{variable}};\n" +
                                                      "    }\n" +
                                                      "    LOOP type,variable=int,i|long,l|short,s\n" +
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
                                    "public class SkippedLines {\n" +
                                    "    /* TEMPLATE\n" +
                                    "    /**\n" +
                                    "     * A simple zero getter serving as a test example\n" +
                                    "     * @return zero in the type {{type}}\n" +
                                    "    ESCAPE\n" +
                                    "     */\n" +
                                    "    // SKIP\n" +
                                    "    /*\n" +
                                    "    {{type}} get_{{type}}Value(){\n" +
                                    "      {{type}} {{variable}} = 0;\n" +
                                    "      return {{variable}};\n" +
                                    "    }\n" +
                                    "    LOOP type,variable=int,i|long,l|short,s\n" +
                                    "    EDITOR-FOLD-ID getters\n" +
                                    "     */\n" +
                                    "    //<editor-fold id=\"getters\">\n" +
                                    "    /**\n" +
                                    "     * A simple zero getter serving as a test example\n" +
                                    "     * @return zero in the type int\n" +
                                    "     */\n" +
                                    "    int get_intValue(){\n" +
                                    "      int i = 0;\n" +
                                    "      return i;\n" +
                                    "    }\n" +
                                    "    /**\n" +
                                    "     * A simple zero getter serving as a test example\n" +
                                    "     * @return zero in the type long\n" +
                                    "     */\n" +
                                    "    long get_longValue(){\n" +
                                    "      long l = 0;\n" +
                                    "      return l;\n" +
                                    "    }\n" +
                                    "    /**\n" +
                                    "     * A simple zero getter serving as a test example\n" +
                                    "     * @return zero in the type short\n" +
                                    "     */\n" +
                                    "    short get_shortValue(){\n" +
                                    "      short s = 0;\n" +
                                    "      return s;\n" +
                                    "    }\n" +
                                    "    //</editor-fold>\n" +
                                    "}",
            String.join("\n", source.getLines()));
    }
}
