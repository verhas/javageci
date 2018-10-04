package javax0.geci.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFileCollector {

    @Test
    public void collectAllFiles() throws IOException {
        var sources = Set.of("src/test/java/javax0/geci/util");
        var files = new FileCollector(sources).collect();
        assertEquals(1, files.size());
        assertTrue(files.iterator().next().endsWith("TestFileCollector.java"));

    }
}
