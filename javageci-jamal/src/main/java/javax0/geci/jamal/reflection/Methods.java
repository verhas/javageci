package javax0.geci.jamal.reflection;

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
 * Macro that evaluates to a string, which is a comma separated list of the methods formatted according to {@code $mformat}
 */
public class Methods implements Macro, InnerScopeDependent {

    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var reader = new MacroReader(processor);
        final var selector = Selector.compile(reader.readValue("$selector").orElse("true"));
        final var klassName = reader.readValue("$class").orElseThrow(
            () -> new BadSyntax("There is no class defined for the macro `methods`")
        );
        final Class<?> klass;
        try {
            klass = GeciReflectionTools.classForName(klassName);
        } catch (ClassNotFoundException e) {
            throw new BadSyntax("Class " + klassName + "cannot be found for the macro `methods`");
        }
        final var format = reader.readValue("$mformat").orElse("$class|$name|$args");
        final var argsep = reader.readValue("$argsep").orElse(":");
        final var exsep = reader.readValue("$exsep").orElse(":");
        var allMethods = GeciReflectionTools.getAllMethodsSorted(klass);
        return Arrays.stream(allMethods).filter(selector::match)
            .map(m -> EntityStringer.method2Fingerprint(m,format,argsep,exsep))
            .collect(Collectors.joining(","));
    }
}