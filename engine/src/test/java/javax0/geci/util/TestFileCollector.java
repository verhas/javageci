package javax0.geci.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFileCollector {

    @Test
    public void collectAllFiles() throws IOException {
        final var sources = (Set)Set.of((Object)new String[]{"src/test/java/javax0/geci/util"});
        var files = new FileCollector(sources).collect();
        assertEquals(1, files.size());
        assertTrue(files.iterator().next().getKlassName().endsWith("TestFileCollector.java"));

    }
}
