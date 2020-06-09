package javax0.geci.tools;

import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Generators that work on Java source files that already have their
 * compiled class files during testing time are encouraged to extend
 * this class instead of {@link AbstractGeneratorEx} or instead of
 * implementing the interface {@link javax0.geci.api.Generator Generator}.
 *
 * <p> Concrete classes have to override te method {@link
 * #process(Source, Class, CompoundParams)}.
 *
 * <p> When the method is called the parameters are collected from the
 * class or in case the class is not annotated then from the comments in
 * front of the class declaration. The requirement is that the
 * configuration line is commented out using {@code //} in front of the
 * configuration "annotation" and the next line is the start of the
 * class. Since this is read from the source file the code checks that
 * the next line matches the pattern {@link #CLASS_LINE}. The pattern is
 * created to check that the {@code class} keyword is on the line
 * followed by some space, then the name of the class and that the end
 * of the line is the opening brace of the class on the same line.
 *
 * <p> If neither the annotation nor the annotation syntax comment is
 * present on the source code there is still a last resort to recognize
 * the file as one to be handled by the generator. The code finds the
 * segments that have the {@code id} parameter value of the generator
 * mnemonic and uses all the parameters defined on that line to
 * configure the generator.
 *
 * <pre>{@code
 *   // <editor-fold if="mnemonic" key="foo">
 * }</pre>
 * <p>
 * will be processed and the {@code key} will be associated with the
 * {@code foo}.
 *
 * <p> The parameters on the segment starting line is used as additional
 * and secondary values even if the annotation or the comment
 * configuration exists. Note, however, that the annotation and the
 * commented annotations are found first when querying a parameter. Thus
 * if there is a
 *
 * <pre>
 * {@code @}{@code Geci("mnemonic key='bar'")
 * class myClass {
 *  ...
 *
 *   // <editor-fold if="mnemonic" key="foo">
 *
 *       ...
 *  }
 * }</pre>
 *
 * <p> annotation in the code on the class then the value {@code foo}
 * will be hidden by the value {@code bar} and when the generator code
 * queries the {@code global} (3rd parameter) of the method {@link
 * #process(Source, Class, CompoundParams)} with the key {@code key} the
 * value will be {@code bar}.
 */
public abstract class AbstractJavaGenerator extends AbstractGeneratorEx {
    private static final Pattern CLASS_LINE = Pattern.compile("class\\s+[a-zA-Z_][\\w$]*\\s*.*\\{\\s*$");

    protected int phase = 0;

    protected void writeGenerated(Segment segment, Class<? extends Annotation> annotation) {
        if (annotation != null) {
            segment.write("@" + annotation.getCanonicalName() + "(\"" + mnemonic() + "\")");
        }
    }

    protected final List<Class<?>> classes = new ArrayList<>();

    /**
     * Child classes can override this method to return {@code true} in case they want to process a source class and
     * source code even if the class is not annotated, there is no annotation before the class line in a comment and
     * there is no editor-fold segment with the mnemonic of the generator.
     *
     * <p>In situations like that the generator may scan the class and look at the methods, fields and other members to
     * decide if it wants to work and generate some code. When there is no editor-fold segment, Geci will insert it
     * before closing brace of the class using regular expression pattern matching. That way the generator only needs
     * perhaps a simple annotation on a field and nothing else to be typed by the programmer. This makes the usage of
     * the generator extremely simple.
     *
     * <p> The concrete generators are supposed to define a configuration parameter with {@code boolean} value (so that
     * it can only be used in the builder) with the name {@code processAllClasses} so that the user can decide if they
     * want to use this feature. Note that scanning through all the classes via reflection may need significant time and
     * in case of large projects this may increase the build time. Considering that implementing this method
     * non-configurable and returning a {@code true} value all the time seems to be a non-optimal choice.
     *
     * @return {@code false} as a default.
     */
    protected boolean processAllClasses() {
        return false;
    }

    public final void processEx(Source source) throws Exception {
        final var klass = source.getKlass();
        if (klass != null) {
            if (phase == 0) {
                classes.add(klass);
            }

            final CompoundParams annotationParams;
            var nullableAnnotationParams = GeciReflectionTools.getParameters(klass, mnemonic());
            if (nullableAnnotationParams == null) {
                Tracer.log("Parameters were not found in annotation");
                var commentParams = GeciAnnotationTools.getParameters(source, mnemonic(), "//", CLASS_LINE);
                if (commentParams == null) {
                    Tracer.log("Parameters were not found in annotation like comment");
                    annotationParams = null;
                } else {
                    annotationParams = commentParams;
                    Tracer.push("Parameters collected from the comment");
                }
            } else {
                Tracer.push("Parameters collected from the annotation");
                annotationParams = nullableAnnotationParams;
            }
            if (annotationParams != null) {
                annotationParams.trace();
                Tracer.pop();
            }
            final CompoundParams editorFoldParams;
            try (final var segment = source.open(mnemonic())) {
                editorFoldParams = segment == null ? null : (javax0.geci.tools.CompoundParams) segment.sourceParams();
            }
            if (editorFoldParams != null) {
                Tracer.push("Parameters collected from the editor fold header");
                editorFoldParams.trace();
                Tracer.pop();
            }
            var global = new CompoundParams(annotationParams, editorFoldParams);
            Tracer.push("Composed effective parameter set");
            global.trace();
            Tracer.pop();

            try (final var pos = Tracer.push("setting the constraint on the parameters keys=[" + (implementedKeys() == null ? "" : String.join(",", implementedKeys())) + "]")) {
                global.setConstraints(source, mnemonic(), implementedKeys());
            }
            if (nullableAnnotationParams != null || processAllClasses()) {
                Tracer.log("Allowing default segment");
                source.allowDefaultSegment();
            }
            if (nullableAnnotationParams != null || editorFoldParams != null || processAllClasses()) {
                try (final var tracePosition = Tracer.push("Start", this.getClass().getName() + ".process( source=" + klass.getName() + " )")) {
                    process(source, klass, global);
                }
                Tracer.prepend("Source", "[PROCESSED] ");
            } else {
                Tracer.log("NotExecuted", "There are no annotations, no editor-fold with id='" + mnemonic() + "' and this generator processAllCasses() returns false");
            }
        } else {
            if (source.getAbsoluteFile().endsWith("module-info.java")) {
                Tracer.log("ModuleInfo", source.getAbsoluteFile() + " has no class, it is not processed.");
            } else {
                Tracer.log("ERROR", "There is no class " + source.getKlassName() + " for " + source.getAbsoluteFile() + " skipping ");
            }
        }
    }

    /**
     * Concrete classes can return an immutable set of the keys that the
     * generator processes. The method {@code processEx()} checks the
     * actual configuration keys against this set and in case there is
     * any configuration key not used by the generator then it
     * throws {@code GeciException}.
     *
     * @return {@code null}. Concrete implementations should return the
     * set of the keys that are accepted by the generator.
     */
    public Set<String> implementedKeys() {
        return null;
    }

    /**
     * Concrete classes extending this abstract class {@link AbstractJavaGenerator} should implement this method.
     *
     * @param source is the source object that the generator will work from.
     * @param klass  the klass that was created from the source during the compilation
     * @param global contains the parameters collected from the {@link javax0.geci.annotations.Geci} annotation
     *               on the class or from the annotation like comment in front of the class start in the source.
     * @throws Exception any exception thrown by the generator
     */
    public abstract void process(Source source, Class<?> klass, CompoundParams global) throws Exception;

    private final String calculatedMnemonic = CaseTools.lcase(this.getClass().getSimpleName());

    public String mnemonic() {
        return calculatedMnemonic;
    }
}
