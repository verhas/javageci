package javax0.geci.consistency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

class TestDocumentation {


    final private StringBuilder messages = new StringBuilder();

    /**
     * Check the version in the parent POM is the same as the
     * parent pom versions in the module poms. Since the poms are
     * generated using Jamal this is less of an issue though.
     * <p>
     * If there is any mismatch then the test fails.
     * <p>
     * The test also checks the readme and if there is any
     * <pre>{@code
     * <version>...</version>
     * }</pre>
     * <p>
     * line it updates it with the current version read from the pom
     * unless the correct version is already there. If there was update
     * then the test fails.
     */
    @Test
    @DisplayName("Test documentation and project file consistency")
    void testDocumentation() {
        try {
            final var rootDir = ConsistencyTestUtils.getDirectory();
            final var parentPom = new File(rootDir + "/" + "pom.xml");
            Assertions.assertTrue(parentPom.exists(), "There is no parent 'pom.xml'");
            final var xml = Xml.from(parentPom);
            final var version = xml.get("/project/version");
            final var modules = xml.gets("/project/modules/module");
            for (final var module : modules) {
                checkModuleParentVersion(rootDir, module, version);
            }
            final var documentationVersion = version.replaceAll("-JVM8$","");

            for( final String fn : new String[]{"/README.md","/TUTORIAL_USE.md"}) {
                final var readme = new File(rootDir + fn);
                Assertions.assertTrue(readme.exists(), fn+"does not exist?");
                new ConsistencyTestUtils(messages).modifyLines(
                        s -> s.replaceAll("<version>(.*?)</version>", "<version>" + documentationVersion + "</version>"),
                        readme
                );
                if (messages.length() > 0) {
                    Assertions.fail("Version number was updated in README.md. Commit changes for the release and run build again.");
                }
                Assertions.assertEquals(0, messages.length(), messages.toString());
            }
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            Assertions.fail("Cannot parse pom.xml", e);
        }
    }

    private void checkModuleParentVersion(String rootDir, String module, String projectVersion) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        final var pom = new File(rootDir + "/" + module.trim() + "/pom.xml");
        final var xml = Xml.from(pom);
        var version = xml.get("/project/parent/version");
        if (!projectVersion.equals(version)) {
            messages.append("Module '").append(module).append("' uses different parent version than the actual in the project\n");
        }
    }

}
