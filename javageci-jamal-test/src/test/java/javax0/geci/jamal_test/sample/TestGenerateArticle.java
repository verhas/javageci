package javax0.geci.jamal_test.sample;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.engine.Processor;
import javax0.jamal.tools.FileTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class TestGenerateArticle {

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

    @Test
    void testGenerateArticle() throws BadSyntax, IOException {
        final var dir = getDirectory();
        final var in = FileTools.getInput(dir + "/javageci-jamal/ARTICLE1.wp.jam");
        final var processor = new Processor("{%", "%}");
        final var result = processor.process(in);
        FileTools.writeFileContent(dir + "/javageci-jamal/ARTICLE1.wp", result);
    }
}
