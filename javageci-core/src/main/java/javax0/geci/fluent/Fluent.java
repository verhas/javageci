package javax0.geci.fluent;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.fluent.internal.ClassBuilder;
import javax0.geci.fluent.internal.FluentBuilderImpl;
import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Tree;
import javax0.geci.log.Logger;
import javax0.geci.log.LoggerFactory;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@AnnotationBuilder
public class Fluent extends AbstractJavaGenerator {
    private static final Logger LOG = LoggerFactory.getLogger();

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var syntax = global.get("syntax");
        final var definedBy = global.get("definedBy");
        if (syntax.length() > 0 && definedBy.length() > 0) {
            throw new GeciException("Both 'syntax' and 'definedBy' cannot be specified.");
        }
        try {
            final FluentBuilderImpl builder;
            if (definedBy.length() > 0) {
                final var definingMethod = getDefiningMethod(definedBy, klass);
                definingMethod.setAccessible(true);
                builder = (FluentBuilderImpl) definingMethod.invoke(null);
            } else if (syntax.length() > 0) {
                builder = (FluentBuilderImpl) FluentBuilder.from(klass).syntax(syntax);
            } else {
                throw new GeciException("Either 'syntax' or 'definedBy' has to be used to define fluent API.");
            }
            builder.optimize();
            LOG.debug("Node structure before optimization.");
            LOG.debug("" + new Tree(Node.ONCE, builder.getNodes()));
            LOG.debug("Node structure after optimization.");
            LOG.debug("" + new Tree(Node.ONCE, builder.getNodes()));
            var generatedCode = new ClassBuilder(builder).build();
            try (var segment = source.open(global.get("id"))) {
                segment.write(generatedCode);
            }
        } catch (InvocationTargetException ite) {
            throw (Exception) ite.getCause();
        }
    }

    /**
     * Get the API defining method. This method has to be static, no parameter and callable by the generator via
     * reflection during test. In other words defining method may be defined in a test class. The name of the method is
     * defined in the Geci parameter "{@code definedBy}". The string of this parameter has to have the format
     *
     * <pre>{@code fully.qualified.class.name::methodname}</pre>
     * or
     * <pre>{@code fully.qualified.class.name#methodname}</pre>
     * or
     * <pre>{@code fully.qualified.class.name.methodname}</pre>
     * The method should return a {@link FluentBuilder} object that contains the built-up fluent API structure.
     * <p>
     * Note that although the name of the class and also the class object itself is available to the generator when
     * it is invoked this parameter is not passed to the API defining method. The reason for this is that the
     * defining method is strongly coupled with the actual class and it would be extremely weird to use the same
     * defining method for different classes that await fluentization.
     *
     * @param s        the specified name of the method.
     * @param forClass the class which is to be fluentized. This is used only to compose error message in case the
     *                 method cannot be found, is not callable or some other problem arises.
     * @return the method object set accessible for invocation, just in case it is not public
     */
    private Method getDefiningMethod(String s, Class<?> forClass) {
        final int sepPos;
        final int sepSize;
        if (s.contains("::")) {
            sepPos = s.indexOf("::");
            sepSize = 2;
        } else if (s.contains("#")) {
            sepPos = s.indexOf("#");
            sepSize = 1;
        } else if (s.contains(".")) {
            sepPos = s.lastIndexOf(".");
            sepSize = 1;
        } else {
            throw new GeciException("Fluent structure definedBy has to have 'className::methodName' format for class '"
                    + forClass + "'");
        }
        var className = s.substring(0, sepPos);
        var methodName = s.substring(sepPos + sepSize);
        final Class<?> klass;
        try {
            klass = GeciReflectionTools.classForName(className);
        } catch (ClassNotFoundException e) {
            throw new GeciException("definedBy class '" + className + "' can not be found");
        }
        final Method method;
        try {
            method = klass.getMethod(methodName);
            method.setAccessible(true);
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
