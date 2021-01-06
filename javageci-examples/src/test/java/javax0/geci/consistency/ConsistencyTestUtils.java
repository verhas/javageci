package javax0.geci.consistency;

import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ConsistencyTestUtils {

    final StringBuilder messages;

    ConsistencyTestUtils(final StringBuilder sb) {
        messages = sb;
    }

    /**
     * Find the project root directory based on the fact that this is the only directory that has a {@code ROOT.dir}
     * file. Start with the current working directory and works up maximum 10 directories.
     *
     * @return the canonical path of the project root directory
     * @throws IOException if the directory structure cannot be read
     */
    static String getDirectory() throws IOException {
        var rootDir = new StringBuilder("ROOT.dir");
        var counter = new AtomicInteger(10);
        File rootDirFile;
        while (true) {
            rootDirFile = new File(rootDir.toString());
            if (rootDirFile.exists()) {
                break;
            }
            rootDir.insert(0, "../");
            Assertions.assertTrue(counter.decrementAndGet() > 0,
                "Cannot find the ROOT.dir file.");
        }
        final var parent = rootDirFile.getParentFile();
        return parent == null ? "." : parent.getCanonicalPath();
    }

    static Properties loadCompilationProperties() {
        Properties properties = new Properties();
        try (InputStream input = ConsistencyTestUtils.class.getResourceAsStream("/javax0/geci/compilation.properties")) {
            properties.load(input);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot open the compilation.properties file during test.", e);
        }
        return properties;
    }

    void modifyLines(Function<String, String> convertLine,
                     File file) throws IOException {
        var modifiedLines = 0;
        final var lines = new ArrayList<String>();
        int lineNumber = 0;
        try (final var br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                lineNumber++;
                var modLine = convertLine.apply(line);
                lines.add(modLine);
                if (!modLine.equals(line)) {
                    modifiedLines++;
                    if (modifiedLines == 1) {
                        messages.append("The file ").append(file.getName()).append(" was modified by the tests.\n");
                        messages.append("The lines changed:\n");
                    }
                    messages.append("@@ -").append(lineNumber).append(",1 -").append(lineNumber).append(",1 @@\n");
                    messages.append("-").append(line).append("\n");
                    messages.append("+").append(modLine).append("\n");
                }
            }
        }
        if (modifiedLines > 0) {
            try (final var fileWriter = new FileWriter(file)) {
                for (final var line : lines) {
                    fileWriter.write(line);
                    fileWriter.write("\n");
                }
            }
        }
    }


}
