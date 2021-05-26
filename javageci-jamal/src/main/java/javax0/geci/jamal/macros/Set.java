package javax0.geci.jamal.macros;

import javax0.geci.jamal.macros.holders.FieldsHolder;
import javax0.geci.jamal.macros.holders.MethodsHolder;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.InnerScopeDependent;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.Params;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static java.lang.String.format;

public class Set implements Macro, InnerScopeDependent {
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var only = Params.<String>holder("$selector", "selector", "when", "if", "only").orElse("true");
        final var klassName = Params.holder("$class", "class", "from").asString();
        final var id = Params.holder(null, "id").asString();
        final var isMethods = Params.holder(null, "methods").asBoolean();
        final var isFields = Params.holder(null, "fields").asBoolean();
        Params.using(processor).between("()").keys(only, klassName, id, isMethods, isFields).parse(in);
        if (isMethods.is() && isFields.is()) {
            throw new BadSyntax(format("'%s' cannot collect both methods and fields the same time", getId()));
        }
        final var selector = Selector.compile(only.get());
        final Class<?> klass;
        try {
            klass = GeciReflectionTools.classForName(klassName.get());
        } catch (ClassNotFoundException e) {
            throw new BadSyntax(format("Class '%s' cannot be found for the macro '%s'", klassName, getId()));
        }
        if (isMethods.is()) {
            processor.define(new MethodsHolder(Arrays.stream(GeciReflectionTools.getAllMethodsSorted(klass)).filter(selector::match)
                .toArray(Method[]::new), id.get()));
        } else if (isFields.is()) {
            processor.define(new FieldsHolder(Arrays.stream(GeciReflectionTools.getAllFieldsSorted(klass)).filter(selector::match)
                .toArray(Field[]::new), id.get()));
        } else {
            throw new BadSyntax(format("'%s' can collect methods or fields. One of them should be specified.", getId()));
        }
        return "";
    }
}
