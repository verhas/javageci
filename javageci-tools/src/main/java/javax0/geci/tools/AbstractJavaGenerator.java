package javax0.geci.tools;

import javax0.geci.api.Source;
import javax0.geci.tools.syntax.GeciAnnotationTools;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Generators that work on Java source files that already have their compiled class files during testing time are
 * encouraged to extend this class instead of {@link AbstractGeneratorEx} or instead of implementing the interface
 * {@link javax0.geci.api.Generator}.
 * <p>
 * Concrete classes have to override te method {@link #process(Source, Class, CompoundParams)}.
 * <p>
 * When the method is called the parameters are collected from the class or in case the class is not annotated then
 * from the comments in front of the class declaration. The requirement is that the configuration line is commented
 * out using {@code //} in front of the configuration "annotation" and the next line is the start of the class.
 * Since this is read from the source file the code checks that the next line matches the pattern {@link #CLASS_LINE}.
 * The pattern is created to check that the {@code class} keyword is on the line followed by some space, then the
 * name of the class and that the end of the line is the opening brace of the class on the same line.
 */
//
public abstract class AbstractJavaGenerator extends AbstractGeneratorEx {
    private static final Pattern CLASS_LINE = Pattern.compile("class\\s+\\w[\\w\\d_$]*\\s*.*(\\{)\\s*$");

    public void processEx(Source source) throws Exception {
        final var klass = source.getKlass();
        if (klass != null) {
            var global = Optional.ofNullable(GeciReflectionTools.getParameters(klass, mnemonic())).orElseGet(() ->
                    GeciAnnotationTools.getParameters(source, mnemonic(), "//", "", CLASS_LINE));
            if (global != null) {
                process(source, klass, global);
            }
        }
    }

    /**
     * Concrete classes extending this abstract class {@link AbstractJavaGenerator} should implement this method.
     *
     * @param source is the source object that the generator will work from.
     * @param klass the klass that was created from the source during the compilation
     * @param global contains the parameters collected from the {@link javax0.geci.annotations.Geci} annotation
     *               on the class or from the annotation like comment in front of the class start in the source.
     * @throws Exception any exception thrown by the generator
     */
    public abstract void process(Source source, Class<?> klass, CompoundParams global) throws Exception;

    public abstract String mnemonic();
}
