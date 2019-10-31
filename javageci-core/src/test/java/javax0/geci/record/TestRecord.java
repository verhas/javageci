package javax0.geci.record;

import javax0.geci.api.Segment;
import javax0.geci.engine.Source;
import javax0.geci.util.JavaSegmentSplitHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;

public class TestRecord {

    @Test
    void test() throws IOException {
        // GIVEN
        final var sut = Record.builder().build();
        final var source = Source.mock(sut).lines("package javax0.geci.record;\n" +
                                                  "\n" +
                                                  "public class ToRecord {\n" +
                                                  "\n" +
                                                  "    private String z;\n" +
                                                  "\n" +
                                                  "    private void ToRecord(){}\n" +
                                                  "\n" +
                                                  "    //<editor-fold id=\"record\">\n" +
                                                  "    //</editor-fold>\n" +
                                                  "\n" +
            "}\n")
            .getSource();
        Field[] fields = ToRecord.class.getDeclaredFields();
        Segment segment = source.open("record");
        Assumptions.assumeTrue(segment != null);

        // WHEN
        sut.process(source, ToRecord.class, null, fields, segment);

        // THEN
        source.consolidate();
        Assertions.assertEquals("package javax0.geci.record;\n" +
                                    "\n" +
                                    "public final class ToRecord {\n" +
                                    "\n" +
                                    "    final private  String  z;\n" +
                                    "\n" +
                                    "    private void ToRecord(final String z){}\n" +
                                    "\n" +
                                    "    //<editor-fold id=\"record\">\n" +
                                    "    public ToRecord(final String z) {\n" +
                                    "        ToRecord(z);\n" +
                                    "        this.z = z;\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    public String getZ() {\n" +
                                    "        return z;\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    @Override\n" +
                                    "    public int hashCode() {\n" +
                                    "        return java.util.Objects.hash(z);\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    @Override\n" +
                                    "    public boolean equals(Object o) {\n" +
                                    "        if (this == o) return true;\n" +
                                    "        if (o == null || getClass() != o.getClass()) return false;\n" +
                                    "        ToRecord that = (ToRecord) o;\n" +
                                    "        return java.util.Objects.equals(that.z, z);\n" +
                                    "    }\n" +
                                    "    //</editor-fold>\n" +
                                    "\n" +
                                    "}",
            String.join("\n", source.getLines()));
    }
}
