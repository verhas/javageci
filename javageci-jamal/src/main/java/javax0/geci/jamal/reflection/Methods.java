package javax0.geci.jamal.reflection;

import javax0.geci.jamal.util.EntityStringer;
import javax0.geci.tools.GeciReflectionTools;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import java.util.Arrays;
import java.util.stream.Collectors;

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