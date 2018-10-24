package javax0.geci.fluent;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.fluent.internal.ClassBuilder;
import javax0.geci.fluent.internal.FluentBuilderImpl;
import javax0.geci.tools.AbstractGenerator;
import javax0.geci.tools.CompoundParams;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Fluent extends AbstractGenerator {
    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        var definingMethod = getDefiningMethod(global.get("definedBy"));
        var builder = (FluentBuilderImpl) definingMethod.invoke(null);
        var generatedCode = new ClassBuilder(builder).build();
        try (var segment = source.open(global.get("id"))) {
            segment.write(generatedCode);
        }
    }

    private Method getDefiningMethod(String s) {
        var sepPos = s.indexOf("::");
        if (sepPos == -1) {
            throw new GeciException("Fluent structure definedBy has to have 'className::methodName' format");
        }
        var className = s.substring(0, sepPos);
        var methodName = s.substring(sepPos + 2);
        final Class klass;
        try {
            klass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new GeciException("definedBy class '" + className + "' can not be found");
        }
        final Method method;
        try {
            method = klass.getMethod(methodName, null);
        } catch (NoSuchMethodException e) {
            throw new GeciException("definedBy method '" + methodName +
                    "' can not be found in the class '" + className + "'");
        }
        if ((method.getModifiers() & Modifier.STATIC) == 0) {
            throw new GeciException("definedBy method '" + methodName +
                    "' from the class '" + className + "' should be static");
        }
        if (!FluentBuilder.class.isAssignableFrom(method.getReturnType())) {
            throw new GeciException("definedBy method '" + methodName +
                    "' from the class '" + className + "' should return type " +
                    FluentBuilderImpl.class.getName());
        }
        return method;
    }

    @Override
    public String mnemonic() {
        return "fluent";
    }
}
