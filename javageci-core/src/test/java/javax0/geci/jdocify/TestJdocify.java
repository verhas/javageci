package javax0.geci.jdocify;

import javax0.geci.engine.Source;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.CompoundParamsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestJdocify {
    final static String REPLACE = "replaced indeed";

    void generalTest(final List<String> sourceLines, final List<String> expectedOutput) throws Exception {
        // GIVEN
        final var sut = Jdocify.builder().build();
        final var source = Source.mock(sut).lines(sourceLines.stream().toArray(String[]::new)).getSource();
        final CompoundParams global = new CompoundParamsBuilder("jdocify").build();
        // WHEN
        sut.process(source, TestJdocify.class, global);

        // THEN
        source.consolidate();
        Assertions.assertEquals(String.join("\n", expectedOutput),
            String.join("\n", source.getLines()));
    }

    @Test
    void simpleReplace() throws Exception {
        generalTest(
            List.of(
                "/**",
                " * This is to <!--CODE REPLACE-->{@code this is to be replaced} be replaced",
                "*/"
            ),
            List.of(
                "/**",
                " * This is to <!--CODE REPLACE-->{@code replaced indeed} be replaced",
                "*/"
            )
        );
    }

    @Test
    void templatedReplace() throws Exception {
        generalTest(
            List.of(
                "/**",
                " * This is to <!--CODE REPLACE $REPLACE x-->{@code this is to be replaced} be replaced",
                "*/"
            ),
            List.of(
                "/**",
                " * This is to <!--CODE REPLACE $REPLACE x-->{@code $replaced indeed x} be replaced",
                "*/"
            )
        );
    }

}
