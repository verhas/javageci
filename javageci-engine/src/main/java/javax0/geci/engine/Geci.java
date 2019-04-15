package javax0.geci.engine;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Phased;
import javax0.geci.api.Source;
import javax0.geci.util.FileCollector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static javax0.geci.api.Source.Set.set;

public class Geci implements javax0.geci.api.Geci {

    public static final String FAILED = "Geci modified source code. Please compile and test again.";

    private final Map<Source.Set, String[]> directories = new HashMap<>();
    private final List<Generator> generators = new ArrayList<>();
    private Set<Predicate<Path>> predicates = new HashSet<>();
    private int phases = 1;

    @Override
    public javax0.geci.api.Geci phases(int phases) {
        if (phases < 1) {
            throw new GeciException("The number of phases should be a positive number.");
        }
        this.phases = phases;
        return this;
    }


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

    @Override
    public javax0.geci.api.Geci only(String... patterns) {
        Collections.addAll(this.predicates,
                Arrays.stream(patterns)
                        .map(Pattern::compile)
                        .map(pattern -> (Predicate<Path>) path -> pattern.matcher(FileCollector.toAbsolute(path)).find())
                        .toArray((IntFunction<Predicate<Path>[]>) Predicate[]::new));
        return this;
    }

    @Override
    public javax0.geci.api.Geci only(Predicate<Path>... predicates) {
        Collections.addAll(this.predicates, Arrays.stream(predicates)
                .toArray((IntFunction<Predicate<Path>[]>) Predicate[]::new));
        return this;
    }

    private void setDefaultDirectories() {
        source(Source.maven());
    }

    @Override
    public boolean generate() throws IOException {
        if (directories.isEmpty()) {
            setDefaultDirectories();
        }
        final var collector = new FileCollector(directories);
        collector.collect(predicates);
        for (int i = 0; i < phases; i++) {
            for (final var source : collector.sources) {
                for (var generator : generators) {
                    if (generator instanceof Phased) {
                        if (((Phased) generator).active(i)) {
                            generator.process(source);
                        }
                    } else {
                        if (i == 0) {
                            generator.process(source);
                        }
                    }
                }
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
