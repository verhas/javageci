package javax0.geci.consistency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Test that the version has {@code -JVM8} postfix when the compilation
 * is with {@code -PJVM8} profile and that the version does not have
 * the prefix if it runs with the default profile.
 */
class TestVersionAndProfileConsistency {
    public static final String RUN_GENPOM = "Run `mvn -f genpom.xml clean` again before next build.";
    final private StringBuilder messages = new StringBuilder();

    @Test
    void testProfileAndVersion() throws IOException {
        final var rootDir = ConsistencyTestUtils.getDirectory();
        Properties properties = ConsistencyTestUtils.loadCompilationProperties();
        final var version = (String) properties.get("projectVersion");
        if( version.endsWith("-SNAPSHOT")){
            return;
        }
        if ("default".equals(properties.get("profile"))) {
            if (version.endsWith("-JVM8")) {
                File versionFile = new File(rootDir + "/version.jim");
                Assertions.assertTrue(versionFile.exists(), "version does not exist ???");

                new ConsistencyTestUtils(messages).modifyLines(s -> s.replaceAll("-JVM8}$", "}"),
                    versionFile);
                if( messages.length() > 0 ) {
                    Assertions.fail("version.jim had a '-JVM8' version. Postfix was removed.\n"+ RUN_GENPOM);
                }
                Assertions.fail("Version has -JVM8 postfix, but the version.jim file does not define it.\n"+RUN_GENPOM);
            }
        }

        if ("JVM8".equals(properties.get("profile"))) {
            if (!version.endsWith("-JVM8")) {
                File versionFile = new File(rootDir + "/version.jim");
                Assertions.assertTrue(versionFile.exists(), "version does not exist ???");

                new ConsistencyTestUtils(messages).modifyLines(s -> s.replaceAll("^\\{@define VERSION=(.*)}$", "{@define VERSION=$1-JVM8}"),
                    versionFile);
                if( messages.length() > 0 ) {
                    Assertions.fail("version.jim did not have a '-JVM8' version. Postfix was added.\n"+ RUN_GENPOM);
                }
                Assertions.fail("Version has -JVM8 postfix, but the version.jim file did not define it (added anyway).\n"+RUN_GENPOM+"\n... just to be safe");
            }
        }

    }

}
