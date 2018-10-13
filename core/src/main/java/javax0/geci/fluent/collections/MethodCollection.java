package javax0.geci.fluent.collections;

import javax0.geci.api.GeciException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collect the callable methods from a class.
 */
public class MethodCollection {
    private final Class<?> klass;
    private final Set<Method> methodSet;
    private final Map<String, Method> methodMap;
    private final Map<String, String> typeMapping = new HashMap<>();
    private final Map<String, Boolean> isMultiple = new HashMap<>();

    public MethodCollection(Class<?> klass) {
        this.klass = klass;
        methodSet = collectMethods();
        var types = allArgumentTypes();
        collectDuplicates(types);
        buildTypeMapping(types);
        methodMap = collect();
    }

    /**
     * Get the method by name or by signature. If the name is unique then there is no need for signature.
     * The method throws {@link GeciException} if the method is specified by the name and it is ambiguous.
     * @param name the name or the signature of the method
     * @return the method or {@code null} if the method is not in the class
     */
    public Method get(String name){
        if( name.contains("(") ){
            var key = normalize(name);
            return methodMap.get(key);
        }
        Method method = null;
        var start = name + "(";
        boolean found = false;
        for( var signature : methodMap.keySet()){
            if( signature.startsWith(start)){
                if( found ){
                    throw new GeciException("The method name '"+name+"' is ambiguous.");
                }
                method = methodMap.get(signature);
                found = true;
            }
        }
        return method;
    }

    private static Stream<String> extractTypes(Type type) {
        return Arrays.stream(type.getTypeName().split("[,<>]"));
    }

    private static String simple(String type) {
        return type.replaceAll("^(\\w+\\.)*", "");
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
        var set = new HashSet<Method>();
        for (var method : klass.getMethods()) {
            if (method.getDeclaringClass() != Object.class || klass == Object.class) {
                set.add(method);
            }
        }
        return set;
    }

    private Set<String> allArgumentTypes() {
        return methodSet.stream()
            .map(Method::getGenericParameterTypes)
            .flatMap(Arrays::stream)
            .flatMap(MethodCollection::extractTypes).collect(Collectors.toSet());
    }

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

    private Map<String, Method> collect() {
        return methodSet.stream().collect(Collectors.toMap(this::signature, Function.identity()));
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
            s.append("  \"").append(key).append("\" -> ").append(methodMap.get(key).getName()).append("\n");
        }
        s.append("}");
        return s.toString();
    }
}
