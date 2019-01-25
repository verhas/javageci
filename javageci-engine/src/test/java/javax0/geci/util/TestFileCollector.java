package javax0.geci.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static javax0.geci.api.Source.Set.set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFileCollector {

    @Test
    public void collectAllFiles() {
        final var sources = (Map)Map.of(set(""),(Object)new String[]{"src/test/java/javax0/geci/util"});
        @SuppressWarnings("unchecked")
        var collector = new FileCollector(sources);
        collector.collect(null);
        assertEquals(1, collector.sources.size());
        assertTrue(collector.sources.iterator().next().getKlassName().endsWith("TestFileCollector.java"));
    }
}
