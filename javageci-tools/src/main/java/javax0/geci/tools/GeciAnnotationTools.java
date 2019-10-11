package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import javax0.geci.annotations.Generated;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.reflection.Selector;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GeciAnnotationTools {
    private static final Pattern SEGMENT_HEADER_PATTERN = Pattern.compile("//\\s*<\\s*editor-fold(.*)>");
    private static final Pattern ANNOTATION_PATTERN = Pattern.compile("@Geci\\(\"(.*)\"\\)");
    private static final Pattern pattern = Pattern.compile("([\\w\\d_$]+)\\s*=\\s*'(.*?)'");

    /**
     * Get the strings of the values of the {@link Geci} annotations
     * that are on the element parameter. The {@link Geci} annotation
     * has a single value parameter that is a string.
     *
     * <p>
     * The method takes care of the special case when there is only one
     * {@link Geci} annotation on the element and also when there are
     * many.
     *
     * <p>
     * Note that the annotation does not need to be the one, which is
     * defined in the javageci annotation library. It can be any
     * annotation interface so long as long the name is {@code Geci} and
     * the method {@code value()} returns {@code java.lang.String}.
     *
     * @param element the class, method or field that is annotated.
     * @return the array of strings that contains the values of the
     * annotations. If the element is not annotated then the returned
     * array will have zero elements. If there is one {@link Geci}
     * annotation then the returned String array will have one element.
     * If there are many annotations then the array will contains each
     * of the values.
     */
    public static String[] getGecis(AnnotatedElement element) {
        return getDeclaredAnnotationUnwrapped(element)
            .filter(GeciAnnotationTools::isAnnotationGeci)
            .map(GeciAnnotationTools::getValue)
            .toArray(String[]::new);
    }

    /**
     * @param element the annotated element for which we need the
     *                annotations
     * @return a stream of the annotations used on the {@code element}.
     * In case any of the annotations is a collection of repeated
     * annotations then the repeated annotations will be returned
     * instead of the collecting annotations. I.e.: if an element has a
     * {@code Gecis} annotation, which is never used directly in the
     * source code, but is put into the byte code by the compiler to
     * wrap the repeated {@code Geci} annotations then the stream will
     * contain the {@code Geci} annotations and not the one {@code
     * Gecis}.
     */
    private static Stream<Annotation> getDeclaredAnnotationUnwrapped(AnnotatedElement element) {
        final var allAnnotations = element.getDeclaredAnnotations();
        return Arrays.stream(allAnnotations)
            .flatMap(GeciAnnotationTools::getSelfOrRepeated);
    }

    /**
     * Get a stream that delivers the annotation itself, or a stream
     * that returns the annotations that are repeated using this
     * annotation as a group annotations.
     *
     * <p>For example the module {@code annotations} in Java::Geci
     * defines the annotations {@code Geci} and also {@code Gecis}.
     * Source code will never directly use {@code Gecis}, but they may
     * contain several {@code Geci} annotations.
     *
     * <p>When a class or other annotated element has several {@code
     * Geci} annotations then these annotations are collected into a
     * {@code Gecis} annotation and this one is returned. The {@code
     * value()} method of this annotation returns an array of {@code
     * Geci} annotations. This is standard Java annotation handling.
     *
     * <p>This method returns a stream. When the annotation is a
     * "normal" annotation then the stream will contain only one
     * element. When the annotation is used to group several annotations
     * and their {@code value()} method returns an array of annotation
     * objects then the stream will deliver these objects instead of the
     * annotation.
     *
     * @param annotation which will be delivered in the stream or which
     *                   contains the other annotations.
     * @return the stream of the annotation or annotations
     */
    private static Stream<Annotation> getSelfOrRepeated(Annotation annotation) {
        try {
            final var value = annotation.annotationType().getMethod("value");
            value.setAccessible(true);
            if (Annotation[].class.isAssignableFrom(value.getReturnType())) {
                return Stream.of((Annotation[]) value.invoke(annotation));
            } else {
                return Stream.of(annotation);
            }
        } catch (NoSuchMethodException |
            IllegalAccessException |
            InvocationTargetException e) {
            return Stream.of(annotation);
        }
    }

    /**
     * Checks that an annotation is a Geci annotation or not.
     * <p>
     * <p>
     * An annotation is Geci annotation in case the name of the
     * annotation interface is {@code Geci} or if the annotation
     * interface itself is annotated with a Geci annotation.
     * <p>
     * <p>
     * This is a recursive definition and because annotations may be
     * annotated recursively directly by themselves or indirectly
     * through other annotations we have to be careful not to check an
     * annotation for "Geciness" that we have already started to check.
     *
     * <p>
     * <p>
     * The rule is that if an annotation could only be Geci because it
     * is recursively annotated by itself then it is not Geci. Somewhere
     * in the loop there has to be an annotation that has the name
     * {@code Geci}.
     *
     * @param annotation the annotation that we want to know if it is
     *                   Geci or not
     * @return {@code true} if the annotation is a Geci annotation.
     */
    private static boolean isAnnotationGeci(Annotation annotation) {
        return isAnnotationGeci(annotation, new HashSet<>());
    }

    /**
     * This is the recursive implementation of {@link
     * #getGecis(AnnotatedElement)}. The second parameter is an empty
     * set at the start and later it is filled when invoked recursively.
     * This will prevent infinite loops in case there is an annotation
     * loop, like {@code @interface A} if annotated using {@code @B} and
     * {@code @interface B} is annotared using {@code @A}.
     *
     * @param annotation the annotation that we want to know if it is
     *                   Geci or not
     * @param checked    a set containing all the annotations for which
     *                   this method was already invoked. (Not
     *                   necessarily returned yet though.)
     * @return {@code true} if the annotation is a Geci annotation.
     */
    private static boolean isAnnotationGeci(Annotation annotation,
                                            Set<Annotation> checked) {
        checked.add(annotation);
        if (annotationName(annotation).equals("Geci")) {
            return true;
        }
        final var annotations = annotation.annotationType()
            .getDeclaredAnnotations();
        return Arrays.stream(annotations)
            .filter(x -> !checked.contains(x))
            .anyMatch(x -> isAnnotationGeci(x, checked));
    }

    private static String annotationName(Annotation a) {
        return a.annotationType().getSimpleName();
    }

    /**
     * Get the value string from the annotation and in case there are
     * other annotation parameters that return a {@code String} value
     * and they are defined on the annotation then append the
     * "key='value'" after the value string. That way the annotation
     * parameters become part of the configuration.
     *
     * <p>Also when the value does not contain a mnemonic at the start
     * then the name of the annotation will be prepended to the string
     * with a space separating it from the parameters. Note that since
     * annotations are Java interfaces and thus are supposed to start
     * with upper case letters but mnemonics are like variables,
     * starting with lower case letters the name of the annotation is
     * modified lower casing the first character.
     *
     * @param annotation the annotation that contains the configuration
     * @return the configuration string
     */
    static String getValue(Annotation annotation) {
        try {
            final String rawValue = getRawValue(annotation);
            final var annotationName = getAnnotationGeciName(annotation);
            final var value = getValue(CaseTools.lcase(annotationName), rawValue.trim());
            for (final var method : getAnnotationDeclaredMethods(annotation)) {
                if (method.getReturnType().equals(String.class) &&
                    !method.getName().equals("value") &&
                    !method.getName().equals("toString")) {
                    final String param = geParam(annotation, method);
                    if (param != null && param.length() > 0) {
                        value.append(" ")
                            .append(method.getName())
                            .append("='")
                            .append(param)
                            .append("'");
                    }
                }
            }
            return value.toString();
        } catch (ClassCastException e) {
            throw new GeciException("Cannot use '" + annotationName(annotation)
                + "' as generator annotation.", e);
        }
    }

    private static String getAnnotationGeciName(Annotation annotation) {
        final var selfName = annotation.annotationType().getSimpleName();
        final var annotations = annotation.annotationType()
            .getDeclaredAnnotations();
        final var renamedName = Arrays.stream(annotations)
            .filter(GeciAnnotationTools::isAnnotationGeci).map(GeciAnnotationTools::getRawValue).findFirst();
        return renamedName.isPresent() && renamedName.get().length() > 0 ?
            renamedName.get() : selfName;
    }

    private static String geParam(Annotation annotation, Method method) {
        try {
            method.setAccessible(true);
            return (String) method.invoke(annotation);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return "";
        }
    }

    private static String getRawValue(Annotation annotation) {
        try {
            final var valueMethod = getAnnotationValueMethod(annotation);
            valueMethod.setAccessible(true);
            final var value = valueMethod.invoke(annotation);
            if (value instanceof String) {
                return (String) value;
            }
            if (value instanceof String[]) {
                final var values = (String[]) value;
                return String.join(" ",values);
            }
            throw new IllegalArgumentException("The annotation " + annotationName(annotation)
                + " value() return type is not String or String[]");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return "";
        }
    }

    /**
     * Get the value method of an annotation.
     *
     * <p>Implementation notes: when executing this code in an
     * application that uses JPMS (e.g. Java::Geci) then the annotation
     * object itself is not an instance of the annotation interface. It
     * is fairly obvious, since interfaces do not have instances. The
     * object is the instance of a proxy class which is in the package
     * {@code com.sun.proxy.jdk.proxy1} in the module {@code
     * jdk.proxy1}. This module does not "opens" this package and there
     * is no way to open this package for good reason. Because it is not
     * opened for reflective access we cannot invoke any method of this
     * class on the annotation object instance, let alone we cannot even
     * call {@code setAccessible()} on it.
     *
     * <p>The good news is that we do not need. What we really need is
     * to call the method of the interface reflectively on the
     * annotation object instance.
     *
     * <p>In short: we invoke the interface method and not the
     * class method on the object.
     *
     * <p>Annotation objects have only one interface they implement and
     * that is the annotation interface. Therefore there is no need to
     * check that the annotation class has interfaces and how many. We
     * just grab the annotation interface as the zero-th element of the
     * array.
     *
     * @param annotation of which we need the value method
     * @return the value method
     * @throws NoSuchMethodException when the annotation does not have a
     *                               {@code value()} method
     */
    private static Method getAnnotationValueMethod(Annotation annotation) throws NoSuchMethodException {
        return annotation.annotationType().getDeclaredMethod("value");
    }

    /**
     * Get the declared methods of the annotation that this annotation
     * object instance implements. See also the implementation comments
     * on the documentation of {@link
     * #getAnnotationValueMethod(Annotation)}.
     *
     * @param annotation of which we need the declared methods
     * @return the array of declared methods
     */
    private static Method[] getAnnotationDeclaredMethods(Annotation annotation) {
        return annotation.annotationType().getDeclaredMethods();
    }

    /**
     * Get the value string adjusted with the name of the annotation.
     *
     * <p> The annotation value string should start with the mnemonic of
     * the generator. If the generator uses it's own annotation than the
     * name of the annotation can be used to match the mnemonic of the
     * generator. For example, if there is a generator that has the
     * mnemonic {@code mygenerator} then the annotation {@code
     * MyGenerator} can be used to configure it. At the same time it
     * would be waste of characters to write
     *
     * <pre>{@code
     *
     * @MyGenerator("mygenerator a='1' b='2' ... z='xxx"")
     *
     * }</pre> <p> Therefore in situations like this the mnemonic can be
     * omitted from the start of the configuration string and Java::Geci
     * will use the name of the annotation first character lower cased
     * as the mnemonic of the generator. Thus
     *
     * <pre>{@code
     *
     * @MyGenerator("a='1' b='2' ... z='xxx"")
     *
     * }</pre>
     * <p>
     * will be served to the generator {@code mygenerator} even though
     * the configuration string in the annotation does not start with
     * this mnemonic of the generator.
     *
     * <p>
     * This method checks the value string and if it starts with a
     * mnemonic then it simply returns the string. If there seems to be
     * a mnemonic missing from the start of the string then it will
     * prepend the name of the class of the annotation in from of the
     * string separated by a space.
     *
     * <p>
     * If the string that starts at the start of the value string and
     * lasts till the first space of at last the end of it contains a
     * {@code =} character then it is not a mnemonic and then the
     * mnemonic will be inserted. Otherwise the value will be return
     * virgo intacta.
     *
     * @param annotationName the name of the annotation to be used as
     *                       mnemonic (first character already
     *                       lowercase)
     * @param value          the value to modify or leave alone
     * @return the value modified or as it was
     */
    private static StringBuilder getValue(String annotationName,
                                          String value) {
        final var mnemonicEnd = value.indexOf(' ');
        final String mnemonic = mnemonicEnd == -1 ?
            value
            :
            value.substring(0, mnemonicEnd);
        if (mnemonic.contains("=") || mnemonic.length() == 0) {
            return new StringBuilder(annotationName)
                .append(value.length() == 0 ? "" : " ")
                .append(value);
        } else {
            return new StringBuilder(value);
        }
    }

    /**
     * Checks if the element is real source code or was generated.
     *
     * <p> Generators are encouraged to annotate the generated elements
     * with the annotation {@link Generated}. This is good for the human
     * reader and the same time some generators can decide if an element
     * is in the compiled class because it was generated or because the
     * programmer provided a version for the element manually. For
     * example the delegator generator does not generate the delegating
     * methods that are provided by the programmer manually but it
     * regenerates all methods that are needed and have the {@link
     * Generated} annotation.
     *
     * @param element that needs the decision if it is generated or
     *                manually programmed
     * @return {@code true} if the element was generated (has the
     * annotation {@link Generated}).
     */
    public static boolean isGenerated(AnnotatedElement element) {
        return Selector.compile("annotation ~ /Generated/").match(element);
    }

    /**
     * Get the parameters from the source file directly reading the
     * source. When a generator uses this method the project may not
     * need {@code com.javax0.geci:javageci-annotation:*} as a {@code
     * compile} time dependency when the "annotation" is commented out.
     * This configuration tool can also be used when the source is not
     * Java, as it does not depend on Java annotations.
     *
     * <p> The lines of the source are read from the start and the
     * parameters composed from the first line that is successfully
     * processed are returned.
     *
     * @param source            the source object holding the code lines
     * @param generatorMnemonic the name of the generator that needs the
     *                          parameters. Only the parameters that are
     *                          specific for this generator are read.
     * @param prefix            characters that should prefix the
     *                          annotation. In case of Java it is {@code
     *                          //}. The line is first trimmed from
     *                          leading and trailing space, then the
     *                          {@code prefix} characters are removed
     *                          from the start then it has to match the
     *                          annotation syntax. If this parameter is
     *                          {@code null} then it is treated as empty
     *                          string, a.k.a. no prefix.
     * @param nextLine          is a regular expression that should
     *                          match the line after the successfully
     *                          matched configuration line. If the next
     *                          line does not match the pattern then the
     *                          previous line is ignored. Typically this
     *                          is something line {@code
     *                          /final\s*int\s*xx} when the generator
     *                          wants to get the parameters for the
     *                          {@code final int xx} declaration. If
     *                          this variable is {@code null} then there
     *                          is no pattern matching performed, and
     *                          all parameter holding line that looks
     *                          like a {@code Geci} annotation is
     *                          accepted and processed.
     *                          <p> Note also that if one or more lines
     *                          looks like {@code Geci} annotations then
     *                          they are skipped and the {@code
     *                          nextLine} pattern is matched against the
     *                          next line that is not a configuration
     *                          line. This allows the program to have
     *                          multiple configuration lines for
     *                          different generators preceding the same
     *                          source line.
     * @return the new {@link CompoundParams} object or {@code null} in
     * case there is no configuration found in the file for the
     * specific generator with the specified conditions.
     */
    public static CompoundParams getParameters(Source source,
                                               String generatorMnemonic,
                                               String prefix,
                                               Pattern nextLine) {
        CompoundParams paramConditional = null;
        for (var line : source.getLines()) {
            if (paramConditional != null) {
                if (nextLine == null || nextLine.matcher(line).find()) {
                    return paramConditional;
                }
            }

            final Matcher match = getMatch(prefix, line);
            if (match.matches()) {
                if (paramConditional == null) {
                    var string = match.group(1);
                    paramConditional = getParameters(generatorMnemonic, string);
                }
            } else {
                paramConditional = null;
            }
        }
        return null;
    }

    /**
     * Get the parameters from the source file directly reading the
     * source. This method tries to find a line that has the format
     *
     * <pre>{@code
     *  // <editor-fold id="mnemonic" a="parm" b="param" ... >
     * }</pre>
     *
     * <p> and read the parameters from that line.
     *
     * <p>The parameter {@code desc} is ignored since that is used by
     * the editor to display a short description when the editor fold is
     * closed and thus it would not be nice to forbid the use of it in
     * case the generator does not have a parameter named "desc".
     *
     * @param source   the source object holding the code lines
     * @param mnemonic the name of the generator
     * @return a compound object that contains the parameters defined in
     * the segments that have the {@code id="mnemonic"} or {@code null}
     * if there is no appropriate segment starting line that would match
     * the syntax and the mnemonic
     * @deprecated The parameters of a segment should be accessed directly
     * through the Segment object representing it.
     */
    @Deprecated(/*since = "1.2.0"*/)
    public static CompoundParams getSegmentParameters(Source source,
                                                      String mnemonic) {
        for (var line : source.getLines()) {
            final var trimmedLine = line.trim();
            final var headerMatcher = SEGMENT_HEADER_PATTERN.matcher(trimmedLine);
            if (headerMatcher.matches()) {
                final var params = new CompoundParamsBuilder(headerMatcher.group(1)).exclude("desc").redefineId().build();
                if (mnemonic.equals(params.id())) {
                    return params;
                }
            }
        }
        return null;
    }

    public static CompoundParams getParameters(String generatorMnemonic, String string) {
        if (string.startsWith(generatorMnemonic + " ") || string.equals(generatorMnemonic)) {
            return new CompoundParamsBuilder(string).redefineId().build();
        } else {
            return null;
        }
    }

    /**
     * Get a matcher of the line against the {@code @Geci( ... ) }
     * pattern to extract the configuration parameters from a comment
     * line. Before the regular expression matching the line is trimmed,
     * prefix is chopped off from the start and the end of
     * the line and then the remaining line is trimmed again.
     *
     * @param prefix the string that is chopped off from the start of the line if it is there
     * @param line   the line to match
     * @return the matcher of regular expression matching
     */
    private static Matcher getMatch(String prefix, String line) {
        final var trimmedLine = line.trim();
        final var chopped = prefix != null && trimmedLine.startsWith(prefix) ?
            trimmedLine.substring(prefix.length()) : trimmedLine;
        final var matchLine = chopped.trim();
        return ANNOTATION_PATTERN.matcher(matchLine);
    }

    /**
     * This method is not used any more. The functionality was moved to
     * {@link CompoundParamsBuilder} and it is based on lexical analysis
     * instead of simple regular expression use.
     *
     * <p> Get the parameters into a map from the string. The {@link
     * Geci} annotation has one single value that is a string. This
     * string is supposed to have the format:
     *
     * <pre>
     *
     *     generator_menomic key='value' ... key='value'
     * </pre>
     *
     * <p> The key can be anything that is more or less an identifier
     * (contains only alphanumeric characters, underscore and {@code $}
     * character, but can also start with any of those, thus it could be
     * '{@code 1966}').
     *
     * <p>The value is enclosed between single quotes, that makes it
     * easier to type and read as single quotes do not need escaping in
     * strings. These quotes can not be skipped.
     *
     * @param s the string parameter
     * @return the map composed from the string
     */
    @Deprecated(/*forRemoval = true*/)
    public static Map<String, String> getParameters(String s) {
        var pars = new HashMap<String, String>();
        var matcher = pattern.matcher(s);
        while (matcher.find()) {
            var key = matcher.group(1);
            var value = matcher.group(2);
            pars.put(key, value);
        }
        return pars;
    }


}
