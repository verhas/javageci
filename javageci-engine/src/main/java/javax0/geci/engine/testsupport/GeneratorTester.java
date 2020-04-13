package javax0.geci.engine.testsupport;


import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.engine.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.CompoundParamsBuilder;

import java.util.Arrays;
import java.util.List;

public class GeneratorTester {

    final Generator sut;
    private Class<?> klass = null;
    private String[] sourceLines;
    private List<String> expected;
    private boolean mustModify = true;
    private Source source;

    private GeneratorTester(Generator sut) {
        this.sut = sut;
    }

    public static GeneratorTester generator(Generator generator) {
        return new GeneratorTester(generator);
    }

    public GeneratorTester klass(Class<?> klass) {
        this.klass = klass;
        return this;
    }

    public GeneratorTester source(String... sourceLines) {
        this.sourceLines = sourceLines;
        return this;
    }

    public GeneratorTester expected(String... expectedLines) {
        this.expected = Arrays.asList(expectedLines);
        return this;
    }

    public GeneratorTester noChange() {
        this.expected = Arrays.asList(sourceLines);
        this.mustModify = false;
        return this;
    }

    public GeneratorTester test() throws Exception {
        // GIVEN
        final var mockBuilder = Source.mock(sut);
        source = mockBuilder.lines(sourceLines).getSource();
        final CompoundParams global = new CompoundParamsBuilder("jdocify").build();

        // WHEN
        if (sut instanceof AbstractJavaGenerator) {
            ((AbstractJavaGenerator) sut).process(source, klass, global);
        } else {
            sut.process(source);
        }

        // THEN
        source.consolidate();
        if (!mockBuilder.isTouched()) {
            throw new GeciException("Source code was not touched");
        }
        if (mockBuilder.isModified((orig, gen) -> !orig.equals(gen)) != mustModify) {
            throw new GeciException("The code was " + (mustModify ? "not modifed" : "modified"));
        }
        return this;
    }

    public String expected() {
        return String.join("\n", expected);
    }

    public String actual() {
        return String.join("\n", source.getLines());
    }

}
