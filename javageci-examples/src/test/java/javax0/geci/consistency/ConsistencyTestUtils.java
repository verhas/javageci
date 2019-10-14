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
    ConsistencyTestUtils(final StringBuilder sb){
        messages = sb;
    }

    /**
     * Find the project root directory based on the fact that this is
     * the only directory that has a {@code README.md} file. Start with
     * the current working directory and works up maximum 10
     * directories.
     *
     * @return the canonical path of the project root directory
     * @throws IOException if the directory structure cannot be read
     */
    static String getDirectory() throws IOException {
        var readme = new StringBuilder("README.md");
        var counter = new AtomicInteger(10);
        File readmeFile;
        while (true) {
            readmeFile = new File(readme.toString());
            if (readmeFile.exists()) {
                break;
            }
            readme.insert(0, "../");
            Assertions.assertTrue(counter.decrementAndGet() > 0,
                "Cannot find the README.md file.");
        }
        final var parent =  readmeFile.getParentFile();
        return parent == null ? "." : parent.getCanonicalPath();
    }

    static Properties loadCompilationProperties() {
        Properties properties = new Properties();
        try (InputStream input = ConsistencyTestUtils.class.getResourceAsStream("/javax0/geci/compilation.properties")) {
            properties.load(input);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot open the compilation.properties file during test.",e);
        }
        return properties;
    }
    void modifyLines(Function<String, String> convertLine,
                     File file
    ) throws IOException {
        var modifiedLines = 0;
        final var lines = new ArrayList<String>();
        try (final var br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                var modLine = convertLine.apply(line);
                lines.add(modLine);
                if (!modLine.equals(line)) {
                    modifiedLines++;
                    messages.append("The file "+file.getName()+" was modified by the tests.");
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
