package javax0.geci.util;

import javax0.geci.api.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static javax0.geci.api.Source.Predicates.exists;
import static javax0.geci.api.Source.Set.set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFileCollector {
    @Test
    @DisplayName("Collect the file in this test directory and find the class for it.")
    void collectAllFiles() {
        final Map<Source.Set, javax0.geci.api.DirectoryLocator> sources = Map.of(set(),new DirectoryLocator(exists(),new String[]{"src/test/java/javax0/geci/util"}));
        var collector = new FileCollector(sources);
        collector.collect(null,null, Set.of());
        assertEquals(1, collector.getSources().size());
        assertTrue(collector.getSources().iterator().next().getKlassName().endsWith("TestFileCollector"));
    }
}
