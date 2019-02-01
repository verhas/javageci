package javax0.geci.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static javax0.geci.api.Source.Set.set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFileCollector {

    @Test
    @DisplayName("Collect the file in this test directory and find the class for it.")
    void collectAllFiles() {
        final var sources = Map.of(set(""),new String[]{"src/test/java/javax0/geci/util"});
        var collector = new FileCollector(sources);
        collector.collect(null);
        assertEquals(1, collector.sources.size());
        assertTrue(collector.sources.iterator().next().getKlassName().endsWith("TestFileCollector"));
    }
}
