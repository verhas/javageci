package javax0.geci.engine;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSource {

    @Test
    public void testSource() throws IOException {
        final var collector = new FileCollector(Map.of());
        var sut = new Source(collector, "ddd", Paths.get("xyz.java"));
        sut.inMemory = true;
        sut.lines.addAll(Arrays.asList(
            "This is something there",
            "    // <editor-fold id=\"myId\">",
            "this is to be replaced",
            "this is also to be replaced",
            "//</editor-fold>",
            " // <editor-fold id=\"otherId\">",
            "this is unharmed",
            "this is also unharmed",
            "//</editor-fold>"
        ));
        var seg = sut.open("myId");
        seg.write("this is the replacement first line");
        seg.write("this is the replacement 2 line");
        seg.write("this is the replacement 3 line");
        seg.write_r("next is intended");
        seg.write("intended line");
        seg.write_l("normal line");
        sut.consolidate();
        assertEquals("This is something there\n" +
            "    // <editor-fold id=\"myId\">\n" +
            "    this is the replacement first line\n" +
            "    this is the replacement 2 line\n" +
            "    this is the replacement 3 line\n" +
            "    next is intended\n" +
            "        intended line\n" +
            "    normal line\n" +
            "//</editor-fold>\n" +
            " // <editor-fold id=\"otherId\">\n" +
            "this is unharmed\n" +
            "this is also unharmed\n" +
            "//</editor-fold>", String.join("\n", sut.lines));
    }
}
