package javax0.geci.tools;

import javax0.geci.api.GeciException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestTracer {

    final byte[] b = new byte[102400];

    private class TestFile implements AutoCloseable {
        final String fileName;

        TestFile(final String fileName) {
            this.fileName = fileName;
            deleteTargetFile();
        }

        public String name() {
            return fileName;
        }

        public boolean exists() {
            return file().exists();
        }

        public File file() {
            return new File(fileName);
        }


        public String content() throws IOException {
            final var length = new FileInputStream(file()).read(b);
            return new String(b, 0, length, StandardCharsets.UTF_8);
        }

        @Override
        public void close() {
            deleteTargetFile();
        }

        private void deleteTargetFile() {
            final var file = file();
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public TestFile testFile() {
        return TestTracer.this.new TestFile("temporary_test.xml");
    }

    @Test
    @DisplayName("Nothing is traced when tracing is switched off")
    void testSwitchedOff() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.off();
            Tracer.log("Abraka dabra");
            Tracer.dumpXML(testFile.name());
            Assertions.assertFalse(testFile.exists());
        }
    }

    @Test
    @DisplayName("A simple log trace is created using a Tracer.log() call.")
    void testSimpleLogging() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.log("Abraka dabra");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <log msg=\"Abraka dabra\"/>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("A simple log trace is created using a Tracer.log() call with a tag.")
    void testSimpleLoggingWithTag() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.log("TAG", "Abraka dabra");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <TAG msg=\"Abraka dabra\"/>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("A simple log trace is created using a Tracer.log() call with a cData.")
    void testSimpleLoggingWithCDATA() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.log("TAG", "Abraka dabra", "cdata");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <TAG msg=\"Abraka dabra\">\n" +
                "<![CDATA[cdata]]>\n" +
                "  </TAG>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("A hierarchical log trace is created using a Tracer.push() / pop() calls.")
    void testHierarchicalLogging() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.log("UP", "This is an up message");
            Tracer.push("TOP", "Abraka dabra");
            Tracer.log("Low", "this is a low message");
            Tracer.push("INNER", "Abraka dabra");
            Tracer.log("Deep", "this is a deep message");
            Tracer.pop();
            Tracer.log("PoppedUp", "This is a low message again");
            Tracer.pop();
            Tracer.log("UP", "This is an up message again");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <UP msg=\"This is an up message\"/>\n" +
                "  <TOP msg=\"Abraka dabra\">\n" +
                "    <Low msg=\"this is a low message\"/>\n" +
                "    <INNER msg=\"Abraka dabra\">\n" +
                "      <Deep msg=\"this is a deep message\"/>\n" +
                "    </INNER>\n" +
                "    <PoppedUp msg=\"This is a low message again\"/>\n" +
                "  </TOP>\n" +
                "  <UP msg=\"This is an up message again\"/>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("An exception can be logged and it will become CDATA.")
    void testExceptionLogging() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.log(new GeciException("Hooppa"));
            Tracer.dumpXML(testFile.name());
            final var content = testFile.content();
            Assertions.assertTrue(content.startsWith("<trace msg=\"tracer root\">\n" +
                "  <ERROR>\n" +
                "<![CDATA[javax0.geci.api.GeciException: Hooppa"));
            Assertions.assertTrue(content.endsWith("]]>\n  </ERROR>\n</trace>\n"));
            Assertions.assertTrue(content
                .replaceAll("geci.tools/","")
                .contains("at javax0.geci.tools.TestTracer.testExceptionLogging(TestTracer.java:"));
        }
    }

    @Test
    @DisplayName("When using it in try-with-resources it closes the level automatically.")
    void testAutoclose() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            try (final var tracer = Tracer.push("Start level")) {
                Tracer.log("Hooppa");
            }
            Tracer.log("top level");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <log msg=\"Start level\">\n" +
                "    <log msg=\"Hooppa\"/>\n" +
                "  </log>\n" +
                "  <log msg=\"top level\"/>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("When doing too many pops an exception will be traced.")
    void testTooManyPops() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            try (final var tracer = Tracer.push("Start level")) {
                Tracer.log("Hooppa");
            }
            Tracer.pop();
            Tracer.log("top level");
            Tracer.dumpXML(testFile.name());
            final var content = testFile.content();
            Assertions.assertTrue(content.replaceAll("geci.tools/","")
                .startsWith("<trace msg=\"tracer root\">\n" +
                "  <log msg=\"Start level\">\n" +
                "    <log msg=\"Hooppa\"/>\n" +
                "  </log>\n" +
                "  <ERROR>\n" +
                "<![CDATA[javax0.geci.api.GeciException: Too many Tracer.pop() calls\n" +
                "\tat javax0.geci.tools.Tracer.pop(Tracer.java:"));
            Assertions.assertTrue(content.endsWith("]]>\n" +
                "  </ERROR>\n" +
                "  <log msg=\"top level\"/>\n" +
                "</trace>\n"));
        }
    }

    @Test
    @DisplayName("When doing too many pops all pops will be traced")
    void testTooManyPopsTraced() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.push("Just to get one level deeper");
            try (final var tracer = Tracer.push("Start level")) {
                Tracer.push("1");
                Tracer.push("2");
                Tracer.push("3");
                Tracer.push("4");
                Tracer.pop();
                Tracer.pop();
                Tracer.pop();
                Tracer.pop();
                Tracer.pop();
            }
            Tracer.log("top level");
            Tracer.dumpXML(testFile.name());
            final var content = testFile.content();
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <log msg=\"Just to get one level deeper\">\n" +
                "    <log msg=\"Start level\">\n" +
                "      <log msg=\"1\">\n" +
                "        <log msg=\"2\">\n" +
                "          <log msg=\"3\">\n" +
                "            <log msg=\"4\"/>\n" +
                "          </log>\n" +
                "        </log>\n" +
                "      </log>\n" +
                "    </log>\n" +
                "    <PopTrace>\n" +
                "      <Pop msg=\"javax0.geci.tools.TestTracer.testTooManyPopsTraced(TestTracer.java:000)\"/>\n" +
                "      <Pop msg=\"javax0.geci.tools.TestTracer.testTooManyPopsTraced(TestTracer.java:000)\"/>\n" +
                "      <Pop msg=\"javax0.geci.tools.TestTracer.testTooManyPopsTraced(TestTracer.java:000)\"/>\n" +
                "      <Pop msg=\"javax0.geci.tools.TestTracer.testTooManyPopsTraced(TestTracer.java:000)\"/>\n" +
                "      <Pop msg=\"javax0.geci.tools.TestTracer.testTooManyPopsTraced(TestTracer.java:000)\"/>\n" +
                "    </PopTrace>\n" +
                "    <log msg=\"top level\"/>\n" +
                "  </log>\n" + // remove the line numbers as they may change when altering the unit test
                "</trace>\n", content
                .replaceAll(":\\d+", ":000")
                .replaceAll("geci.tools/",""));
        }
    }

    @Test
    @DisplayName("Can append text to the last log message")
    void testSimpleAppend() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.log("Abraka dabra");
            Tracer.append(" kadabra");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <log msg=\"Abraka dabra kadabra\"/>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("Can append text to the last log message even if message is null")
    void testSimpleAppendToNull() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.log((String) null);
            Tracer.append(" kadabra");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <log msg=\" kadabra\"/>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("Can prepend text to the last log message")
    void testSimplePrepend() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.log("Abraka dabra");
            Tracer.prepend(" kadabra");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <log msg=\" kadabraAbraka dabra\"/>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("Can prepend text to the last log message even if message is null")
    void testSimplePrependToNull() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.log((String) null);
            Tracer.prepend(" kadabra");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <log msg=\" kadabra\"/>\n" +
                "</trace>\n", testFile.content());
        }
    }


    @Test
    @DisplayName("Can append text to the last log message with given tag")
    void testHierarchicalAppend() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.push("tag", "Abraka");
            Tracer.push("");
            Tracer.push("");
            Tracer.push("");
            Tracer.push("");
            Tracer.append("tag", " debra");
            Tracer.log("We are still deep");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <tag msg=\"Abraka debra\">\n" +
                "    <log msg=\"\">\n" +
                "      <log msg=\"\">\n" +
                "        <log msg=\"\">\n" +
                "          <log msg=\"\">\n" +
                "            <log msg=\"We are still deep\"/>\n" +
                "          </log>\n" +
                "        </log>\n" +
                "      </log>\n" +
                "    </log>\n" +
                "  </tag>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("Can append text to the last log message with given tag even if message is null")
    void testHierarchicalAppendToNull() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.push("tag", null);
            Tracer.push("");
            Tracer.push("");
            Tracer.push("");
            Tracer.append("tag", " nulabra");
            Tracer.log("We are still deep");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <tag msg=\" nulabra\">\n" +
                "    <log msg=\"\">\n" +
                "      <log msg=\"\">\n" +
                "        <log msg=\"\">\n" +
                "          <log msg=\"We are still deep\"/>\n" +
                "        </log>\n" +
                "      </log>\n" +
                "    </log>\n" +
                "  </tag>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("Can prepend text to the last log message with given tag")
    void testHierarchicalPrepend() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.push("tag", "Abraka");
            Tracer.push("");
            Tracer.push("");
            Tracer.push("");
            Tracer.push("");
            Tracer.prepend("tag", " debra");
            Tracer.log("We are still deep");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <tag msg=\" debraAbraka\">\n" +
                "    <log msg=\"\">\n" +
                "      <log msg=\"\">\n" +
                "        <log msg=\"\">\n" +
                "          <log msg=\"\">\n" +
                "            <log msg=\"We are still deep\"/>\n" +
                "          </log>\n" +
                "        </log>\n" +
                "      </log>\n" +
                "    </log>\n" +
                "  </tag>\n" +
                "</trace>\n", testFile.content());
        }
    }

    @Test
    @DisplayName("Can prepend text to the last log message with given tag even if message is null")
    void testHierarchicalPrependToNull() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.push("tag", null);
            Tracer.push("");
            Tracer.push("");
            Tracer.push("");
            Tracer.prepend("tag", " nulabra");
            Tracer.log("We are still deep");
            Tracer.dumpXML(testFile.name());
            Assertions.assertEquals("<trace msg=\"tracer root\">\n" +
                "  <tag msg=\" nulabra\">\n" +
                "    <log msg=\"\">\n" +
                "      <log msg=\"\">\n" +
                "        <log msg=\"\">\n" +
                "          <log msg=\"We are still deep\"/>\n" +
                "        </log>\n" +
                "      </log>\n" +
                "    </log>\n" +
                "  </tag>\n" +
                "</trace>\n", testFile.content());
        }
    }


    @Test
    @DisplayName("Can prepend text to the last log message with given tag fails if there is no such tag")
    void testHierarchicalPrependFail() throws Exception {
        try (final var testFile = testFile()) {
            Tracer.on();
            Tracer.push("tag", null);
            Tracer.push("");
            Tracer.push("");
            Tracer.push("");
            Tracer.prepend("teg", " nulabra");
            Tracer.log("We are still deep");
            Tracer.dumpXML(testFile.name());
            final var content = testFile.content();
            Assertions.assertTrue(content.startsWith("<trace msg=\"tracer root\">\n" +
                "  <tag>\n" +
                "    <log msg=\"\">\n" +
                "      <log msg=\"\">\n" +
                "        <log msg=\"\">\n" +
                "          <ERROR>\n" +
                "<![CDATA[javax0.geci.api.GeciException: Walking upward in trace there is no tag 'teg'"));
            Assertions.assertTrue(content.endsWith("]]>\n" +
                "          </ERROR>\n" +
                "          <log msg=\"We are still deep\"/>\n" +
                "        </log>\n" +
                "      </log>\n" +
                "    </log>\n" +
                "  </tag>\n" +
                "</trace>\n"));
        }
    }
}
