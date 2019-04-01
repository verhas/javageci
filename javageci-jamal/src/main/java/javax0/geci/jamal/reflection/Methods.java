package javax0.geci.jamal.reflection;

import javax0.geci.jamal.util.EntityStringer;
import javax0.geci.tools.GeciReflectionTools;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Macro that evaluates to a string, which is a comma separated list of the method fingerprints that are in the class
 * specified in the macro argument and match the selection expression. The format of the macro argument is parsed
 * by the method {@link FieldsMethodsParser#parse(String, String)} and the format is defined accordingly in
 * {@link FieldsMethodsParser}
 */
public class Methods implements Macro {

    @Override
    public String evaluate(Input in, Processor processor) {
        final var parser = FieldsMethodsParser.parse(in.toString(), "methods");
        var declaredMethods = GeciReflectionTools.getAllMethodsSorted(parser.klass);
        return Arrays.stream(declaredMethods).filter(parser.selector::match)
                .map(EntityStringer::method2Fingerprint)
                .collect(Collectors.joining(","));
    }
}