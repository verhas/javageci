package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collect the callable methods from a class.
 */
public class MethodCollection {
    private final Class<?> klass;
    /**
     * Set of methods that are referable in the fluet interface. The methods are collected in a way that all
     * inherited and declared methods are there, thus any public or protected inherited or declared and also
     * all declared private methods are in the set. These are all reachable and referable from the Wrapper class
     * because that is a static inner class of the one, which is fluentized.
     */
    private final Set<Method> methodSet;

    /**
     * Maps the method signatures to the method data. The method data also contains the {@code Method} object.
     * The signature is the name of the method and the class names of the argument types comma separated without
     * any space. The class names are simple names in case or fully qualified names based on the following:
     *
     * <ul>
     * <li>If there is class and it's simple name is unique within all the classes that appear in the
     * methods parameters lists, then the simple name is used.</li>
     * <li>if there are two different classes used as argument types and the classes share the simple name
     * (e.g.: there is a {@code java.lang.String} and also {@code my.package.String}) then the fully qualified
     * canonical class name is used.</li>
     * </ul>
     */
    private final Map<String, MethodData> methodMap;
    /**
     * Maps the types as they are present on the fluent API definition to their normalized type. That is all types
     * that are fully qualified become simple, only the class name, except for those classes that appear in different
     * packages multiple times.
     */
    private final Map<String, String> typeMapping = new HashMap<>();
    /**
     * This map is filled to register if there are some types that have the same name and are appearing in multiple
     * packages. It may happen that method argument types refer to types that have the same simple name, though those
     * names are in different Java packages. If a simple name appears in multiple packages then this map will contain
     * {@code true} as a value for the simple name as a key.
     */
    private final Map<String, Boolean> isMultiple = new HashMap<>();
    private final boolean wrapperIfIsNeeded;

    public MethodCollection(Class<?> klass) {
        this.klass = klass;
        methodSet = collectMethods();
        wrapperIfIsNeeded = needsWrapperInterface();
        var types = allArgumentTypes();
        collectDuplicates(types);
        buildTypeMapping(types);
        methodMap = collect();
    }

    /**
     * Extract the non-generic type names that are used to build up a generic type.
     *
     * @param type the generic type that includes presumably subtypes
     * @return the stream of the individual type strings without duplicates
     */
    private static Stream<String> extractTypes(Type type) {
        return Arrays.stream(type.getTypeName().split("[,<>]"));
    }

    /**
     * Revome the package name from a fully qualified class name.
     *
     * @param type the fully qualified name
     * @return the simple name, which is essentially the class name and nothing else
     */
    private static String simple(String type) {
        return type.replaceAll("^(\\w+\\.)*", "");
    }

    /**
     * Create a new {@link MethodData} object for the method.
     *
     * @param method the method for the new object to hold
     * @return the new object initialized
     */
    private static MethodData methodData(Method method) {
        MethodData md = new MethodData();
        md.method = method;
        return md;
    }

    public boolean needWrapperInterface() {
        return wrapperIfIsNeeded;
    }

    /**
     * @return the set of the normalized method signatures. Only the methods that are references in the fluent
     * interface and also {@code close()} in case it is there.
     */
    public Set<String> methodSignatures() {
        return methodMap.entrySet().stream()
            .filter(e -> e.getValue().referenced)
            .map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * Get the method by name or by signature. If the name is unique then there is no need for signature.
     * The method throws {@link GeciException} if the method is specified by the name and it is ambiguous.
     * Returns {@code null} if the method is not found.
     *
     * @param name the name or the signature of the method
     * @return the method object or {@code null} if the method is not in the class
     */
    public Method get(String name) {
        var md = get0(name);
        if (md == null) {
            return null;
        } else {
            md.referenced = true;
            return md.method;
        }
    }

    /**
     * Get the method by name or by signature. If the name is unique then there is no need for signature.
     * The method throws {@link GeciException} if the method is specified by the name and it is ambiguous.
     * Returns {@code null} if the method is not found.
     *
     * @param name the name or the signature of the method
     * @return true is the method is a final in the fluent api call, and false otherwise
     */
    public Boolean isExitNode(String name) {
        var md = get0(name);
        if (md == null) {
            return null;
        } else {
            return md.isExitNodeMethod;
        }
    }


    public void include(String method) {
        clude(method, true);
    }

    public void exclude(String method) {
        clude(method, false);
    }

    private void clude(String method, boolean b) {
        var md = get0(method);
        if (md == null) {
            throw new GeciException("Method '" + method + "' does not exist, can not be exlcuded from the fluent interface.");
        } else {
            md.isFluent = b;
        }
    }


    public Boolean isFluentNode(String name) {
        var md = get0(name);
        if (md == null) {
            return null;
        } else {
            return md.isFluent;
        }
    }

    /**
     * Declare that the method specified by the name (signature) is a final in the fluent call.
     * <p>
     * Final methods are special because they have the original return type in the interfaces as well as in the
     * wrapper class and they are not chain-able.
     *
     * @param name the name or the signature of the method
     */
    public void exitNode(String name) {
        var md = get0(name);
        if( md != null ) {
            md.isExitNodeMethod = true;
        }else{
            throw new IllegalArgumentException("The method "+name+" is signalled as exit node, but it is not defined.");
        }
    }

    private MethodData get0(String name) {
        if (name.contains("(")) {
            var key = normalize(name);
            return methodMap.get(key);
        }
        MethodData methodData = null;
        var start = name + "(";
        boolean found = false;
        for (var signature : methodMap.keySet()) {
            if (signature.startsWith(start)) {
                if (found) {
                    throw new GeciException("The method name '" + name + "' is ambiguous.");
                }
                methodData = methodMap.get(signature);
                found = true;
            }
        }
        return methodData;
    }

    /**
     * Normalize the name for unique signature string. The normalization is a complex process that lets the user
     * use the class names in their simple form.
     * <p>
     * The normalization looks at all the types that are used in all of the method arguments including the generic
     * type parameters of the argument types recursively. When a type is unique with its simple name (w/o the package
     * declaration) then the normalized name is the simple name. If the same class name is used from different
     * packages then the normalized name is the fully qualified name.
     * <p>
     * Note: this normalization is for the fluent API to describe the methods and has nothing to do with what is really
     * imported in the actual class file and what is not. It may happen as an example that
     * {@code javax0.geci.annotations.Geci} is imported and can be used in the code as {@code Geci}
     * and {@code javax0.geci.api.Geci} is not imported. If these two types are used to describe method parameters then
     * they should be specified with fully qualified names in the fluent API parameter strings and that is how this
     * method will normalize them.
     * <p>
     * Note: the implementation assumes that all package names are lower case and class names are capitalized and
     * thus it can not happen that a class name is substring of another class name in fully qualified form. If ever
     * this becomes a problem then the for loop should iterate over a sorted list of the collected types so that the
     * longest type names are replaced with their simple counterpart first.
     *
     * @param s the type name to be normalized in its fully qualified form or in simple form like
     *          {@code java.lang.String}, {@code String}, {@code javax0.geci.MyClass} or {@code MyClass}
     * @return the normalized form of the class.
     */
    private String normalize(String s) {
        var norming = s;
        for (var type : typeMapping.keySet()) {
            norming = norming.replace(type, typeMapping.get(type));
        }
        return norming;
    }

    private String signature(Method method) {
        var arglist = Arrays.stream(method.getGenericParameterTypes())
            .map(t -> normalize(t.getTypeName()))
            .collect(Collectors.joining(","));
        var exceptionlist = Arrays.stream(method.getGenericExceptionTypes())
            .map(t -> normalize(t.getTypeName()))
            .collect(Collectors.joining(","));
        return method.getName() +
            "(" + arglist + ")" +
            (exceptionlist.length() == 0 ? "" : " throws " + exceptionlist);
    }

    /**
     * Collect all the methods of the class that can be invoked unless they are inherited from Object. However
     * if the class happens to be the Object class then collect all methods.
     * <p>
     * Note that this is not likely that onyone will use this method for the Object class in production.
     *
     * @return the set of methods
     */
    private Set<Method> collectMethods() {
        var set = Arrays.stream(klass.getMethods())
            .filter(this::isNeeded).collect(Collectors.toCollection(HashSet::new));
        if (klass != Object.class) {
            set.addAll(Arrays.stream(klass.getDeclaredMethods())
                .filter(this::isNeeded).collect(Collectors.toSet()));
        }
        return set;
    }

    /**
     * A complex fluent API can be based on a builder class that contains methods accepting a builder as an argument.
     * The fluent API building is a good example. In the class {@link javax0.geci.fluent.FluentBuilder} the
     * methods like {@link javax0.geci.fluent.FluentBuilder#optional(FluentBuilder)} accept a
     * {@link FluentBuilder} as argument. This is used to build up complex API structures.
     * <p>
     * When generating fluent API this situation is handled with an extra "wrapper" interface. This "wrapper" interface
     * has no methods. The generated {@code Wrapper} class implements this interface and and all
     * other interfaces generated by the code generation extend this interface.
     * <p>
     * The wrapper methods that wrap methods of this type will accept this interface as argument type instead of the
     * original class and when calling the original underlying method and the argument will be replaced by the value
     * that holds the delegating object.
     * <p>
     * The name of the wrapper interface is encoded in the class {@link InterfaceSet#WRAPPER_INTERFACE_NAME}.
     *
     * @return {@code true} if there is the need to generate this interface
     */
    private boolean needsWrapperInterface() {
        return Arrays.stream(klass.getMethods())
            .filter(this::isNeeded)
            .map(Method::getParameterTypes)
            .flatMap(Arrays::stream)
            .anyMatch(parameterClass -> parameterClass == klass);
    }

    private boolean isNeeded(Method method) {
        return (method.getModifiers() & Modifier.STATIC) == 0 &&
            (method.getDeclaringClass() != Object.class || klass == Object.class);
    }

    private Set<String> allArgumentTypes() {
        return methodSet.stream()
            .map(Method::getGenericParameterTypes)
            .flatMap(Arrays::stream)
            .flatMap(MethodCollection::extractTypes).collect(Collectors.toSet());
    }

    /**
     * Fills the {@code isMultiple} map from the set.
     *
     * @param types the names of the types. The names are presumably the fully qualified names.
     */
    private void collectDuplicates(Set<String> types) {
        isMultiple.clear();
        types.forEach(type -> {
            var s = simple(type);
            isMultiple.put(s, isMultiple.containsKey(s));
        });
    }

    private void buildTypeMapping(Set<String> types) {
        typeMapping.clear();
        typeMapping.putAll(types.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                type -> isMultiple.get(simple(type)) ? type : simple(type))));
    }

    private Map<String, MethodData> collect() {
        return methodSet.stream().collect(Collectors.toMap(this::signature, MethodCollection::methodData));
    }

    /**
     * @return the string reperesentation of the map in a {@code key -> methodName} JSON like string. The keys are
     * sorted alphabetically so the string result is deterministic.
     */
    @Override
    public String toString() {
        var s = new StringBuilder();
        s.append("{\n");
        for (var key : new TreeSet<>(methodMap.keySet())) {
            s.append("  \"").append(key).append("\" -> ").append(methodMap.get(key).method.getName()).append("\n");
        }
        s.append("}");
        return s.toString();
    }

    private static class MethodData {
        Method method;
        boolean isExitNodeMethod = false;
        boolean isFluent = true;
        boolean referenced = false;
    }
}
