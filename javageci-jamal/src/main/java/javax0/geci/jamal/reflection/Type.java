package javax0.geci.jamal.reflection;

import javax0.geci.jamal.Reflection;
import javax0.geci.jamal.util.EntityStringer;
import javax0.geci.tools.GeciReflectionTools;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import static javax0.geci.jamal.util.EntityStringer.isFingerPrintAField;

/**
 * Macro that evaluates to the type of a method or a field, which is defined using the fingerprint in the macro
 * argument.

 */
public class Type implements Macro {
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var fingerPrint = in.toString().trim();
        final Class<?> type;
        if (isFingerPrintAField(fingerPrint)) {
            var field = EntityStringer.fingerprint2Field(fingerPrint);
            type = field.getType();
        } else {
            var method = Reflection.globalMethodMap.get(fingerPrint);
            type = method.getReturnType();
        }
        return GeciReflectionTools.getGenericTypeName(type);
    }
}
