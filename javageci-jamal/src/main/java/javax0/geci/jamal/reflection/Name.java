package javax0.geci.jamal.reflection;

import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

/**
 * Macro that evaluates to the name of a method or a field, which is defined using the fingerprint in the macro
 * argument.
 */
public class Name implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) {
        final var fingerPrint = in.toString().trim();
        final var parts = fingerPrint.split("\\|", -1);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Entity identified with " + fingerPrint + " does not have name.");
        }
        return parts[1];
    }
}
