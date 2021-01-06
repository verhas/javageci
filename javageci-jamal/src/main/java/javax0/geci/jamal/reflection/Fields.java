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
 * Macro that evaluates to a string, which is a comma separated list of the fields formatted using the macro {@code
 * $fformat}
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