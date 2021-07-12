package javax0.geci.engine;

import javax0.geci.api.Source;
import javax0.geci.util.DirectoryLocator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static javax0.geci.api.Source.Predicates.exists;
import static javax0.geci.api.Source.Set.set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFileCollector {
    private static final int NUMBER_OF_TESTS = 5;

    /**
     * <p>Tests that the file collector works. If this test fails then check if you added or deletes some new files to
     * the test source directory {@code javax1/geci/engine}. The {@code NUMBER_OF_TESTS} is the number of files in that
     * directory.</p>
     */
    @Test
    @DisplayName("Collect the file in this test directory and find the class for it.")
    void collectAllFiles() {
        final Map<Source.Set, javax0.geci.api.DirectoryLocator> sources =
            Map.of(set(), new DirectoryLocator(exists(), new String[]{"src/test/java/javax0/geci/engine"}));
        var collector = new FileCollector(sources);
        collector.collect(null, null, Collections.emptySet());
        assertEquals(NUMBER_OF_TESTS, collector.getSources().size());
        assertTrue(collector.getSources().stream()
                .anyMatch(source -> source.getKlassName().endsWith("TestFileCollector")),
            "TestFileCollector was not found by the collector");
    }
}
