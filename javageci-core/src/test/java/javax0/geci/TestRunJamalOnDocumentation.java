package javax0.geci;

import javax0.jamal.api.Input;
import javax0.jamal.api.Position;
import javax0.jamal.engine.Processor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class TestRunJamalOnDocumentation {

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

    @Test
    @DisplayName("Run Jamal on all of the *.jam files except the pom.xml.jam and *.yml.jamfiles")
    void runJamal() throws Exception {
        for (final var p :
            Files.walk(Paths.get(getDirectory()), Integer.MAX_VALUE)
                .filter(Files::isRegularFile)
                .filter(s -> s.toString().endsWith(".jam"))
                .filter(s -> !s.toString().endsWith("pom.xml.jam"))
                .filter(s -> !s.toString().endsWith("javageci-build.yml.jam"))
                .filter(s -> !s.toString().endsWith("ARTICLE1.wp.jam"))
                .filter(s -> !s.toString().endsWith("variables.jam"))
                .collect(Collectors.toList())) {
            executeJamal(p, Paths.get(p.toString().replaceAll("\\.jam$", "")));
        }
    }

    private void executeJamal(final Path inputPath, final Path outputPath) throws Exception {
        final String result;
        try (final var processor = new Processor("{%", "%}")) {
            result = processor.process(createInput(inputPath));
        }
        Files.writeString(outputPath, result, StandardCharsets.UTF_8, WRITE, TRUNCATE_EXISTING,CREATE);
    }

    private Input createInput(Path inputFile) throws IOException {
        var fileContent = Files.lines(inputFile).collect(Collectors.joining("\n"));
        return new javax0.jamal.tools.Input(fileContent, new Position(inputFile.toString(), 1));
    }

}
