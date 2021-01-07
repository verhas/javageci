package javax0.geci.jamal.macros;

import javax0.geci.jamal.util.EntityStringer;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.InnerScopeDependent;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Macro to get all the fields of a class. The output can best be used in a multi-variable for loop.
 * <p>
 * The macro looks up the fields (inherited and declared) of a class and returns a list of the fields. The format of the
 * information, the selection criteria, which fields to include into the output and the separator characters are all
 * defined in user defined macros.
 * <p>
 * The format of the data about the individual fields is driven by the user defined macro {@code fformat}. This can be
 * set in the code template file using the macro {@code fformat} or {@code format} defined in {@code res:geci.jim}. (The
 * macro {@code format} sets also the user defined macro {@code mformat}, which is used by the macro {@link Methods}.
 * <p>
 * The format can contain the following placeholders:
 * <ul>
 *     <li>{@code $class} is replaced by the declaring class of the field.</li>
 *     <li>{@code $name} is replaced by the name of the field.</li>
 *     <li>{@code $type} is replaced by the type of the field.</li>
 *     <li>{@code $modifiers} is replaced by the space separated list of the modifiers of the field. This string will
 *     also contain a trailing space of the string is not empty.</li>
 * </ul>
 * <p>
 * The selector is defined by the user defined macro {@code $selector}. This can be set using the macro {@code
 * selector}. All these user defined macros are defined in the {@code res:geci.jim} file.
 * <p>
 * The class is defined by {@code $class}, again use {@code class} macro.
 * <p>
 * The information about the fields are joined using comma.
 * <p>
 */
public class Fields implements Macro, InnerScopeDependent {

    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var reader = new MacroReader(processor);
        final var selector = Selector.compile(reader.readValue("$selector").orElse("true"));
        final var klassName = reader.readValue("$class").orElseThrow(
            () -> new BadSyntax("There is no class defined for the macro `fields`")
        );
        final Class<?> klass;
        try {
            klass = GeciReflectionTools.classForName(klassName);
        } catch (ClassNotFoundException e) {
            throw new BadSyntax("Class " + klassName + "cannot be found for the macro `methods`");
        }
        final var format = reader.readValue("$fformat").orElse("$class|$name");
        var declaredFields = GeciReflectionTools.getAllFieldsSorted(klass);
        return Arrays.stream(declaredFields).filter(selector::match)
            .map(f -> EntityStringer.field2Fingerprint(f, format))
            .collect(Collectors.joining(","));
    }
}