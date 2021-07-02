package javax0.geci.jamal.macros;

import javax0.geci.jamal.macros.holders.ImportsHolder;
import javax0.geci.jamal.util.EntityStringer;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.InnerScopeDependent;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.Params;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Macro to get all the fields of a class. Templates utilizing this macro can best use the output in a multi-variable
 * for a loop.
 * <p>
 * The macro looks up the fields (inherited and declared) of a class and returns a list of the fields. The format of the
 * information, the selection criteria, which fields to include in the output.
 * <p>
 * The format of the data about the individual fields is driven by the user defined macro {@code $fformat}. It can be
 * set in the code template file using the macro {@code fformat} or {@code format} defined in {@code res:geci.jim}. (The
 * macro {@code format} sets also the user defined macro {@code mformat}, which is used by the macro {@link Methods}.
 * <p>
 * The format can contain the following placeholders:
 * <ul>
 *     <li>{@code $class} is replaced by the declaring class of the field.</li>
 *     <li>{@code $name} is replaced by the name of the field.</li>
 *     <li>{@code $type} is replaced by the type of the field.</li>
 *     <li>{@code $modifiers} is replaced by the space-separated list of the field's modifiers. This string will
 *     also contain a trailing space of the string if it is not empty.</li>
 * </ul>
 * <p>
 * The selector is defined by the user defined macro {@code $selector}. You can set it using the macro {@code
 * selector}. All these user defined macros are defined in the {@code res:geci.jim} file.
 * <p>
 * The class is defined by {@code $class}, again use {@code class} macro.
 * <p>
 * The macro is inner scope dependent, which means that these macros can be used inside the opening and closing {@code
 * {% %}} strings. This is the recommended use unless you want to reuse the same values in the same file. Using the
 * macros inside the {@code fields} macro will limit the effect of the macros to the actual {@code fields} macro. This
 * use also assumes that the {@code fields} macro is used with the {@code #} character as
 * <pre>{@code
 * {%#fields ...%}
 * }</pre>
 * to ensure that these macros inside are evaluated before the {@code fields} macro starts to be evaluated.
 * <p>
 * The information about the fields are joined using coma.
 */
public class Fields implements Macro, InnerScopeDependent {

    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var selectorPar = Params.<String>holder("$selector", "selector").orElse("true");
        final var klassName = Params.<String>holder("$class", "class");
        final var format = Params.<String>holder("$fformat", "format", "methodFormat").orElse("$class|$name|$args");
        Params.using(processor).from(this).between("()").keys(selectorPar, klassName, format).parse(in);

        final var selector = Selector.compile(selectorPar.get());
        if (!klassName.isPresent()) {
            throw new BadSyntax("There is no class defined for the macro `fields`");
        }
        final Class<?> klass;
        try {
            klass = GeciReflectionTools.classForName(klassName.get());
        } catch (ClassNotFoundException e) {
            throw new BadSyntax("Class '" + klassName + "' cannot be found for the macro `methods`");
        }
        var declaredFields = GeciReflectionTools.getAllFieldsSorted(klass);
        final var imports = ImportsHolder.instance(processor);
        final var sb = new StringBuilder();
        String s = "";
        for (final var m : declaredFields) {
            if (selector.match(m)) {
                sb.append(s).append(EntityStringer.field2Fingerprint(m, format.get(), imports));
                s = ",";
            }
        }
        return sb.toString();
    }
}