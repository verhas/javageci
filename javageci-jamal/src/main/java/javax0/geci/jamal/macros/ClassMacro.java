package javax0.geci.jamal.macros;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.BadSyntaxAt;
import javax0.jamal.api.InnerScopeDependent;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.InputHandler;
import javax0.jamal.tools.Params;
import javax0.jamal.tools.PlaceHolders;

public class ClassMacro implements Macro, InnerScopeDependent {

    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var format = Params.<String>holder("classFormat", "format").orElse("$simpleName");
        Params.using(processor).from(this).between("()").keys(format).parse(in);
        InputHandler.skipWhiteSpaces(in);
        final var className = in.toString().trim();
        try {
            final var klass = Class.forName(className);
            return PlaceHolders.with(
                // snippet classFormats
                "$simpleName", klass.getSimpleName(),
                "$name", klass.getName(),
                "$canonicalName", klass.getCanonicalName(),
                "$packageName", klass.getPackageName(),
                "$typeName", klass.getTypeName()
                // end snippet
            ).format(format.get());
        } catch (Exception e) {
            throw new BadSyntaxAt("The class '" + className + "' cannot be found on the classpath in the macro '" + getId() + "'.", in.getPosition(), e);
        }
    }

    @Override
    public String getId() {
        return "java:classs";
    }
}