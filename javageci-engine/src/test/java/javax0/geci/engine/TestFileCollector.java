package javax0.geci.engine;

import javax0.geci.api.Source;
import javax0.geci.util.DirectoryLocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static javax0.geci.api.Source.Predicates.exists;
import static javax0.geci.api.Source.Set.set;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFileCollector {
    @Test
    @DisplayName("Collect the file in this test directory and find the class for it.")
    void collectAllFiles() {
        final Map<Source.Set, javax0.geci.api.DirectoryLocator> sources = Map.of(set(),new DirectoryLocator(exists(),new String[]{"src/test/java/javax0/geci/engine"}));
        var collector = new FileCollector(sources);
        collector.collect(null,null, Set.of());
        assertEquals(4, collector.getSources().size());
        for( final var source : collector.getSources() ){
            if( source.getKlassName().endsWith("TestFileCollector"))
                return;
        }
        Assertions.fail("TestFileCollector was not found by the collector");
    }
}
