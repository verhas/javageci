package javax0.geci.jamal.macros;

import javax0.geci.jamal.macros.holders.ClassHolder;
import javax0.geci.jamal.macros.holders.FieldHolder;
import javax0.geci.jamal.macros.holders.FieldsHolder;
import javax0.geci.jamal.macros.holders.ImportsHolder;
import javax0.geci.jamal.macros.holders.MethodHolder;
import javax0.geci.jamal.macros.holders.MethodsHolder;
import javax0.geci.tools.GeciReflectionTools;
import javax0.refi.selector.Selector;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.api.UserDefinedMacro;
import javax0.jamal.tools.Params;

import java.util.Arrays;

import static java.lang.String.format;

/**
 * Experimental macro, which can set a named user defined macro to hold a list of methods or fields.
 */
public class Set implements Macro{
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var only = Params.<String>holder(null, "selector", "when", "if", "only").orElse("true");
        final var klassName = Params.holder(null, "class", "from").asString();
        final var id = Params.holder(null, "id").asString();
        final var isMethods = Params.holder(null, "methods").asBoolean();
        final var isFields = Params.holder(null, "fields").asBoolean();
        Params.using(processor).keys(only, klassName, id, isMethods, isFields).parse(in);
        if (isMethods.is() && isFields.is()) {
            throw new BadSyntax(format("'%s' cannot collect both methods and fields at the same time", getId()));
        }
        final var selector = Selector.compile(only.get());
        final Class<?> klass;
        try {
            klass = GeciReflectionTools.classForName(klassName.get());
        } catch (ClassNotFoundException e) {
            throw new BadSyntax(format("Class '%s' cannot be found for the macro '%s'", klassName, getId()));
        }
        final String identifier;
        if (isMethods.is()) {
            identifier = id.get();
            processor.define(new MethodsHolder(Arrays.stream(GeciReflectionTools.getAllMethodsSorted(klass)).filter(selector::match)
                .map(m -> define(new MethodHolder(m, ImportsHolder.instance(processor)), processor))
                .toArray(MethodHolder[]::new), identifier));
        } else if (isFields.is()) {
            identifier = id.get();
            processor.define(new FieldsHolder(Arrays.stream(GeciReflectionTools.getAllFieldsSorted(klass)).filter(selector::match)
                .map(f -> define(new FieldHolder(f, ImportsHolder.instance(processor)), processor))
                .toArray(FieldHolder[]::new), identifier));
        } else {
            identifier = klass.getSimpleName();
            processor.define(new ClassHolder(klass));
        }
        return "";
    }

    private <T extends UserDefinedMacro> T define(T macro, Processor processor) {
        processor.define(macro);
        return macro;
    }
}
