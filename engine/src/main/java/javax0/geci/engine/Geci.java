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
    public javax0.geci.api.Geci source(String ...directory) {
        directories.add(directory);
        return this;
    }

    @Override
    public javax0.geci.api.Geci register(Generator generator) {
        generators.add(generator);
        return this;
    }

    @Override
    public boolean generate() throws IOException {
        var sources = new FileCollector(directories).collect();
        for (var source : sources) {
            for (var generator : generators) {
                generator.process(source);
            }
        }
        boolean generated = false;
        for (var source : sources) {
            source.consolidate();
            if( source.save() ){
                generated = true;
            }
        }

        return generated;
    }
}
