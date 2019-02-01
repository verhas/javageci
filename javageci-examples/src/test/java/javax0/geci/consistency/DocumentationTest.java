package javax0.geci.consistency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.ArrayList;

class DocumentationTest {


    private String getDirectory() throws IOException {
        var readme = new StringBuilder("README.md");
        var counter = 10;
        File readmeFile;
        while (true) {
            readmeFile = new File(readme.toString());
            if (!readmeFile.exists()) {
                readme.insert(0, "../");
                counter--;
            } else {
                break;
            }
            Assertions.assertTrue(counter > 0, "Cannot find the README.md file.");
        }
        return readmeFile.getParentFile().getCanonicalPath();
    }

    final private StringBuilder messages = new StringBuilder();

    @Test
    @DisplayName("Test documentation and project file consistency")
    void testDocumentation() {
        try {
            var rootDir = getDirectory();
            var parentPom = new File(rootDir + "/" + "pom.xml");
            Assertions.assertTrue(parentPom.exists(), "There is no parent 'pom.xml'");
            var xml = Xml.from(parentPom);
            var version = xml.get("/project/version");
            var modules = xml.gets("/project/modules/module");
            for (final var module : modules) {
                checkModuleParentVersion(rootDir, module, version);
            }
            File readme = new File(rootDir + "/README.md");
            Assertions.assertTrue(readme.exists(), "readme does not exist the second time... ???");
            final var lines = new ArrayList<String>();
            var modifiedLines = 0;
            try (final var br = new BufferedReader(new FileReader(readme))) {
                for (String line; (line = br.readLine()) != null; ) {
                    var modLine = line.replaceAll("<version>(.*?)</version>", "<version>" + version + "</version>");
                    lines.add(modLine);
                    if (!modLine.equals(line)) {
                        modifiedLines++;
                        messages.append("The version in README.md is '").append(line).append("' and not '").append(modLine).append("'\n");
                    }
                }
            }
            if (modifiedLines > 0) {
                try (final var fileWriter = new FileWriter(readme)) {
                    for (final var line : lines) {
                        fileWriter.write(line);
                        fileWriter.write("\n");
                    }
                }
            }
            Assertions.assertEquals(0, messages.length(), messages.toString());
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            Assertions.fail("Cannot parse pom.xml", e);
        }
    }

    private void checkModuleParentVersion(String rootDir, String module, String projectVersion) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        final var pom = new File(rootDir + "/" + module + "/pom.xml");
        final var xml = Xml.from(pom);
        var version = xml.get("/project/parent/version");
        if (!projectVersion.equals(version)) {
            messages.append("Module '").append(module).append("' uses different parent version than the actual in the project\n");
        }
    }

}
