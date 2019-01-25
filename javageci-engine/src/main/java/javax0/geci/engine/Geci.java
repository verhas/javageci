package javax0.geci.engine;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;
import javax0.geci.util.FileCollector;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static javax0.geci.api.Source.Set.set;

public class Geci implements javax0.geci.api.Geci {

    public static final String FAILED = "Geci modified source code. Please compile and test again.";

    private final Map<Source.Set, String[]> directories = new HashMap<>();
    private final List<Generator> generators = new ArrayList<>();

    @Override
    public javax0.geci.api.Geci source(String... directory) {
        directories.put(set(""), directory);
        return this;
    }

    @Override
    public javax0.geci.api.Geci source(Source.Set set, String... directory) {
        directories.put(set, directory);
        return this;
    }

    @Override
    public javax0.geci.api.Geci register(Generator... generatorArr) {
        Collections.addAll(generators, generatorArr);
        return this;
    }

    private Set<Pattern> patterns = null;

    @Override
    public javax0.geci.api.Geci only(String... patterns) {
        this.patterns = Arrays.stream(patterns).map(Pattern::compile).collect(Collectors.toSet());
        return this;
    }

    private void setDefaultDirectories() {
        source(set("mainSource"), Source.maven().mainSource());
        source(set("mainResources"), Source.maven().mainResources());
        source(set("testSource"), Source.maven().testSource());
        source(set("testResources"), Source.maven().testResources());
    }

    @Override
    public boolean generate() throws IOException {
        if (directories.isEmpty()) {
            setDefaultDirectories();
        }
        final var collector = new FileCollector(directories);
        collector.collect(patterns);
        for (final var source : collector.sources) {
            for (var generator : generators) {
                generator.process(source);
            }
        }
        var generated = false;
        var touched = false;
        for (var source : collector.sources) {
            source.consolidate();
            touched = touched || source.isTouched();
        }
        for (var source : collector.newSources) {
            source.consolidate();
            touched = touched || source.isTouched();
        }
        if (!touched) {
            throw new GeciException("The generators did not touch any source");
        }
        for (var source : collector.sources) {
            if (source.isModified()) {
                source.save();
                generated = true;
            }
        }
        for (var source : collector.newSources) {
            if (source.isModified()) {
                source.save();
                generated = true;
            }
        }
        return generated;
    }
}
