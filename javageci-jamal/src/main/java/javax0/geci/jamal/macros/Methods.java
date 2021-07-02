package javax0.geci.jamal.macros;

import javax0.geci.jamal.macros.holders.ImportsHolder;
import javax0.geci.jamal.util.EntityStringer;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.InnerScopeDependent;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.ObjectHolder;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.Params;

/**
 * Macro to get all the methods of a class. Templates utilizing this macro can best use the output in a multi-variable
 * for a loop.
 * <p>
 * The macro looks up the methods (inherited and declared) of a class and returns a list of the methods. The format of
 * the information, the selection criteria, which methods to include in the output, and the separator characters are all
 * defined in user defined macros.
 * <p>
 * The format of the data about the individual methods is driven by the user defined macro {@code $mformat}. It can be
 * set in the code template file using the macro {@code mformat} or {@code format} defined in {@code res:geci.jim}. (The
 * macro {@code format} sets also the user defined macro {@code fformat}, which is used by the macro {@link Fields}.
 * <p>
 * The format can contain the following placeholders:
 * <ul>
 *     <li>{@code $class} is replaced by the declaring class of the field.</li>
 *     <li>{@code $name} is replaced by the name of the field.</li>
 *     <li>{@code $type} is replaced by the type of the field.</li>
 *     <li>{@code $modifiers} is replaced by the space-separated list of the field's modifiers. This string will
 *     also contain a trailing space of the string if it is not empty.</li>
 *     <li>{@code $exceptions} is replaced by the list of the exceptions that the method throws.</li>
 *     <li>{@code $args} is replaced by the list of argument types the method has. If the method is a vararg
 *     method then the last type will end with {@code ...} instead of {@code []}.</li>
 *     <li>{@code $exsep} is used as separator in the list of exceptions. The default value is '{@code :}'.</li>
 *     <li>{@code $argsep} is used as separator in the list of arguments. The default value is '{@code :}'.</li>
 * </ul>
 * <p>
 * The selector is defined by the user defined macro {@code $selector}. You can set it using the macro {@code
 * selector}. All these user defined macros are defined in the {@code res:geci.jim} file.
 * <p>
 * The class is defined by {@code $class}, again use {@code class} macro.
 * <p>
 * The macro is inner scope dependent, which means that these macros can be used inside the opening and closing {@code
 * {% %}} strings. This is the recommended use unless you want to reuse the same values in the same file. Using the
 * macros inside the {@code methods} macro will limit the effect of the macros to the actual {@code methods} macro. This
 * use also assumes that the {@code methods} macro is used with the {@code #} character as
 * <pre>{@code
 * {%#methods ...%}
 * }</pre>
 * to ensure that these macros inside are evaluated before the {@code methods} macro starts to be evaluated.
 * <p>
 * The information about the methods are joined using coma.
 */
public class Methods implements Macro, InnerScopeDependent {

    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var selectorPar = Params.<String>holder("$selector", "selector").orElse("true");
        final var klassName = Params.<String>holder("$class", "class");
        final var format = Params.<String>holder("$mformat", "format", "methodFormat").orElse("$class|$name|$args");
        final var argsep = Params.<String>holder("$argsep", "argsep", "argumentSeparator").orElse(":");
        final var exsep = Params.<String>holder("$exsep", "exsep", "exceptionsSeparator").orElse(":");
        Params.using(processor).from(this).between("()").keys(selectorPar, klassName, format, argsep, exsep).parse(in);
        if (!klassName.isPresent()) {
            throw new BadSyntax("There is no $class defined for the macro `methods`");
        }
        final var selector = Selector.compile(selectorPar.get());
        final Class<?> klass;
        try {
            klass = GeciReflectionTools.classForName(klassName.get());
        } catch (ClassNotFoundException e) {
            throw new BadSyntax("Class '" + klassName + "' cannot be found for the macro `methods`");
        }

        final var imports = ImportsHolder.instance(processor);

        var allMethods = GeciReflectionTools.getAllMethodsSorted(klass);
        final var sb = new StringBuilder();
        String s = "";
        for (final var m : allMethods) {
            if (selector.match(m)) {
                sb.append(s).append(EntityStringer.method2Fingerprint(m, format.get(), argsep.get(), exsep.get(), imports));
                s = ",";
            }
        }
        return sb.toString();
    }
}