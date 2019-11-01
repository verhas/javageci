package javax0.geci.record;

import javax0.geci.api.GeciException;
import javax0.geci.engine.Source;
import javax0.geci.record.sutclasses.ClassAbstract;
import javax0.geci.record.sutclasses.ClassExtending;
import javax0.geci.record.sutclasses.ToRecord;
import javax0.geci.record.sutclasses.ToRecordAlreadyFinals;
import javax0.geci.record.sutclasses.ToRecordMultipleFields;
import javax0.geci.record.sutclasses.ToRecordMultipleValidators;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;

class TestRecord {

    @Test
    @DisplayName("Properly generates the getters, constructor, hashCode and equals and fixes field and class")
    void testHappyPath() throws IOException {
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
        final var fields = ToRecord.class.getDeclaredFields();
        final var segment = source.open("record");
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

    @Test
    @DisplayName("Properly generates ... for multiple fields")
    void testHappyPathMultipleFields() throws IOException {
        // GIVEN
        final var sut = Record.builder().build();
        final var source = Source.mock(sut).lines("package javax0.geci.record;\n" +
                                                      "\n" +
                                                      "public class ToRecordMultipleFields {\n" +
                                                      "\n" +
                                                      "    private String z;\n" +
                                                      "    private String k;\n" +
                                                      "\n" +
                                                      "    private void ToRecordMultipleFields(){}\n" +
                                                      "\n" +
                                                      "    //<editor-fold id=\"record\">\n" +
                                                      "    //</editor-fold>\n" +
                                                      "\n" +
                                                      "}\n")
                               .getSource();
        final var fields = ToRecordMultipleFields.class.getDeclaredFields();
        final var segment = source.open("record");
        Assumptions.assumeTrue(segment != null);

        // WHEN
        sut.process(source, ToRecordMultipleFields.class, null, fields, segment);

        // THEN
        source.consolidate();
        Assertions.assertEquals("package javax0.geci.record;\n" +
                                    "\n" +
                                    "public final class ToRecordMultipleFields {\n" +
                                    "\n" +
                                    "    final private  String  z;\n" +
                                    "    final private  String  k;\n" +
                                    "\n" +
                                    "    private void ToRecordMultipleFields(final String z, final String k){}\n" +
                                    "\n" +
                                    "    //<editor-fold id=\"record\">\n" +
                                    "    public ToRecordMultipleFields(final String z, final String k) {\n" +
                                    "        ToRecordMultipleFields(z, k);\n" +
                                    "        this.z = z;\n" +
                                    "        this.k = k;\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    public String getZ() {\n" +
                                    "        return z;\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    public String getK() {\n" +
                                    "        return k;\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    @Override\n" +
                                    "    public int hashCode() {\n" +
                                    "        return java.util.Objects.hash(z, k);\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    @Override\n" +
                                    "    public boolean equals(Object o) {\n" +
                                    "        if (this == o) return true;\n" +
                                    "        if (o == null || getClass() != o.getClass()) return false;\n" +
                                    "        ToRecordMultipleFields that = (ToRecordMultipleFields) o;\n" +
                                    "        return java.util.Objects.equals(that.z, z) && java.util.Objects.equals(that.k, k);\n" +
                                    "    }\n" +
                                    "    //</editor-fold>\n" +
                                    "\n" +
                                    "}",
            String.join("\n", source.getLines()));
    }

    @Test
    @DisplayName("Properly generates when the class is already final and some of the fields are already final")
    void testHappyPathAlreadyFinals() throws IOException {
        // GIVEN
        final var sut = Record.builder().build();
        final var source = Source.mock(sut).lines("package javax0.geci.record;\n" +
                                                      "\n" +
                                                      "public final class ToRecordAlreadyFinals {\n" +
                                                      "\n" +
                                                      "    private final String z;\n" +
                                                      "    private String k;\n" +
                                                      "\n" +
                                                      "    private void ToRecordAlreadyFinals(){}\n" +
                                                      "\n" +
                                                      "    //<editor-fold id=\"record\">\n" +
                                                      "    ToRecordAlreadyFinals(){\n" +
                                                      "        z = null;\n" +
                                                      "    }\n" +
                                                      "    //</editor-fold>\n" +
                                                      "\n" +
                                                      "}\n")
                               .getSource();
        final var fields = ToRecordAlreadyFinals.class.getDeclaredFields();
        final var segment = source.open("record");
        Assumptions.assumeTrue(segment != null);

        // WHEN
        sut.process(source, ToRecordAlreadyFinals.class, null, fields, segment);

        // THEN
        source.consolidate();
        Assertions.assertEquals("package javax0.geci.record;\n" +
                                    "\n" +
                                    "public final class ToRecordAlreadyFinals {\n" +
                                    "\n" +
                                    "    private final String z;\n" +
                                    "    final private  String  k;\n" +
                                    "\n" +
                                    "    private void ToRecordAlreadyFinals(final String z, final String k){}\n" +
                                    "\n" +
                                    "    //<editor-fold id=\"record\">\n" +
                                    "    public ToRecordAlreadyFinals(final String z, final String k) {\n" +
                                    "        ToRecordAlreadyFinals(z, k);\n" +
                                    "        this.z = z;\n" +
                                    "        this.k = k;\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    public String getZ() {\n" +
                                    "        return z;\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    public String getK() {\n" +
                                    "        return k;\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    @Override\n" +
                                    "    public int hashCode() {\n" +
                                    "        return java.util.Objects.hash(z, k);\n" +
                                    "    }\n" +
                                    "\n" +
                                    "    @Override\n" +
                                    "    public boolean equals(Object o) {\n" +
                                    "        if (this == o) return true;\n" +
                                    "        if (o == null || getClass() != o.getClass()) return false;\n" +
                                    "        ToRecordAlreadyFinals that = (ToRecordAlreadyFinals) o;\n" +
                                    "        return java.util.Objects.equals(that.z, z) && java.util.Objects.equals(that.k, k);\n" +
                                    "    }\n" +
                                    "    //</editor-fold>\n" +
                                    "\n" +
                                    "}",
            String.join("\n", source.getLines()));
    }

    @Test
    @DisplayName("Throws exception when class extends other class")
    void testExtendingClass() throws IOException {
        // GIVEN
        final var sut = Record.builder().build();
        final var source = Source.mock(sut).lines("").getSource();

        // WHEN
        Assertions.assertThrows(GeciException.class, () -> sut.process(source, ClassExtending.class, null, (Field[]) null, null));
    }

    @Test
    @DisplayName("Throws exception when class is abstract")
    void testAbstractClass() throws IOException {
        // GIVEN
        final var sut = Record.builder().build();
        final var source = Source.mock(sut).lines("").getSource();

        // WHEN
        Assertions.assertThrows(GeciException.class, () -> sut.process(source, ClassAbstract.class, null, (Field[]) null, null));
    }

    @Test
    @DisplayName("Throws exception when class has multiple validators")
    void testMultipleValidators() throws IOException {
        // GIVEN
        final var sut = Record.builder().build();
        final var source = Source.mock(sut).lines("").getSource();

        // WHEN
        Assertions.assertThrows(GeciException.class, () -> sut.process(source, ToRecordMultipleValidators.class, null, (Field[]) null, null));
    }
}
