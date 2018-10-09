package javax0.geci.engine;

import javax0.geci.api.Generator;
import javax0.geci.util.FileCollector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Geci implements javax0.geci.api.Geci {

    private final Set<String[]> directories = new HashSet<>();
    private final List<Generator> generators = new ArrayList<>();

    @Override
    public javax0.geci.api.Geci source(String... directory) {
        directories.add(directory);
        return this;
    }

    @Override
    public javax0.geci.api.Geci register(Generator... generatorArr) {
        for (var generator : generatorArr) {
            generators.add(generator);
        }
        return this;
    }

    @Override
    public boolean generate() throws IOException {
        final var collector = new FileCollector(directories);
        final var sources = collector.collect();
        for (final var source : sources) {
            for (var generator : generators) {
                generator.process(source);
            }
        }
        var generated = false;
        for (var source : sources) {
            source.consolidate();
        }
        for (var source : collector.newSources) {
            source.consolidate();
        }
        for (var source : sources) {
            if (source.isModified()) {
                source.save();
                generated = true;
            }
        }
        for (var source : collector.newSources) {
            source.save();
        }
        return generated;
    }
}
