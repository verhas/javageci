# Delegator Code Generator

The delegator can be used to create methods that invoke the method of the same name and arguments through a field.
It essentially delegates the calls to the method of the same name of the object referenced by the field on which it delegates.

An example can be to implement multiple inheritance.
You may want to code a class that implements the `Map` interface.
This can be implemented having a `HashMap` field and calling the methods on this field for every method of the `Map` interface.
At the same time you want your class to be an instance of a `java.io.Writer` that handles output filling some template data stored in the internal `HashMap`.
In that case you should `extend` the abstract class `Witer` and implement the interface `Map`.

To implement the interface `Map` you can have a field that is  a`Map` type and is initialized to be a `HashMap` or some other concrete implementation of `Map` that fits your needs.
This also means that you have to create methods for each method that the `Map` interface defines, each calling the same method and the same parameters in the `HashMap`.

Delegator creates these methods automatically.
If you create some of the methods yourself then the delegator generator will just skip those during code generation.

The appropriate workflow is to create first the test code that will check the generated code or generate the code when the check fails.

<!-- snip TestDelegator -->
```java
    @Test
    public void testDelegator() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven()
                        .module("javageci-examples")
                        .mainSource())
                        .register(Delegator.builder().build())
                        .generate(),
                geci.failed());
    }
```

then annotate the class and the field:

<!-- snip MapWriter_head regex="replace='/implements Map<K,V> //'" -->
```java
@Geci("delegator")
public class MapWriter<K, V> extends Writer implements Map<K, V> {

    @Geci("delegator id='map' methods=' !name ~ /equals|hashCode/ & !static '")
    final Map<K, V> contained = new HashMap<>();
```

Do NOT write `implements Map<K,V> ` to the class declaration yet.
It will need the delegating methods to be implemented and we do not have them at the moment.

When this is done you can run the test code from the IDE or via the build process and get the methods created:
<!-- snip generated_map-->
```java
    //<editor-fold id="map">
    @javax0.geci.annotations.Generated("delegator")
    public V compute(K arg1, java.util.function.BiFunction<? super K,? super V,? extends V> arg2) {
        return contained.compute(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V computeIfAbsent(K arg1, java.util.function.Function<? super K,? extends V> arg2) {
        return contained.computeIfAbsent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V computeIfPresent(K arg1, java.util.function.BiFunction<? super K,? super V,? extends V> arg2) {
        return contained.computeIfPresent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V getOrDefault(Object arg1, V arg2) {
        return contained.getOrDefault(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V merge(K arg1, V arg2, java.util.function.BiFunction<? super V,? super V,? extends V> arg3) {
        return contained.merge(arg1,arg2,arg3);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V putIfAbsent(K arg1, V arg2) {
        return contained.putIfAbsent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V replace(K arg1, V arg2) {
        return contained.replace(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V get(Object arg1) {
        return contained.get(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V put(K arg1, V arg2) {
        return contained.put(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V remove(Object arg1) {
        return contained.remove(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean containsKey(Object arg1) {
        return contained.containsKey(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean containsValue(Object arg1) {
        return contained.containsValue(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean isEmpty() {
        return contained.isEmpty();
    }

    @javax0.geci.annotations.Generated("delegator")
    public int size() {
        return contained.size();
    }

    @javax0.geci.annotations.Generated("delegator")
    public java.util.Collection<V> values() {
        return contained.values();
    }

    @javax0.geci.annotations.Generated("delegator")
    public java.util.Set<K> keySet() {
        return contained.keySet();
    }

    @javax0.geci.annotations.Generated("delegator")
    public java.util.Set<java.util.Map.Entry<K,V>> entrySet() {
        return contained.entrySet();
    }

    @javax0.geci.annotations.Generated("delegator")
    public void clear() {
        contained.clear();
    }

    @javax0.geci.annotations.Generated("delegator")
    public void putAll(java.util.Map<? extends K,? extends V> arg1) {
        contained.putAll(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean remove(Object arg1, Object arg2) {
        return contained.remove(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean replace(K arg1, V arg2, V arg3) {
        return contained.replace(arg1,arg2,arg3);
    }

    @javax0.geci.annotations.Generated("delegator")
    public void forEach(java.util.function.BiConsumer<? super K,? super V> arg1) {
        contained.forEach(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public void replaceAll(java.util.function.BiFunction<? super K,? super V,? extends V> arg1) {
        contained.replaceAll(arg1);
    }

    //</editor-fold>
```

Then you can extend the header of the class to

<!-- snip MapWriter_head2 snippet="MapWriter_head" -->
```java
@Geci("delegator")
public class MapWriter<K, V> extends Writer implements Map<K, V> {

    @Geci("delegator id='map' methods=' !name ~ /equals|hashCode/ & !static '")
    final Map<K, V> contained = new HashMap<>();
```

Now, that the generator implemented the delegating methods the class implements the interface.
If you run the code generating test again it will run fine without fail.

The delegator creates each method annotated with the `@Generated` annotation.
This helps the delegator recognize when the programmer manually implements some of the delegator methods.
A manually created method must not be annotated with the `@Generated` annotation and then the delegator will simply skip these method and it does not generate this specific method.

You can use the delegator code generator without compile dependency on the Java::Geci annotation module.
Although `Geci` and `Generated` are defined in that module you are free to use any other annotation you may define in your own project.

The framework will work any annotation so long as long their simple name is `Geci`, or the annotation itself is annotated using a runtime retention annotation named Geci (recursively) as detailed precisely in the documentation [configuration](CONFIGURATION.md).

The generator does not even require that the name of the annotation is `Generated`.
All it requires is to use the

    Delegator.builder().generatedAnnotation(YourAnnotation.class).build()
    
builder chain and then it will use that annotation to mark the generated methods and eventually will look for this annotation in the method when it decides if an already existing method was manually created or generated.

You can also filter the methods that have to be generated to delegate functionality.
The class or field annotation can contain the configuration field `methods` that can specify a [filter expressions](FILTER_EXPRESSIONS.adoc) for the methods that should be considered for delegation.
Note that you can also use the configuration parameter `filter` that may limit the fields that are processed.

## Configuration

<!-- snip Delegator_Config snippet="epsilon" 
              append="snippets='Delegator_Config_.*'" -->

* `generatedAnnotation` can be used to define the annotation to mark the methods that are generated by the generator.
When the generator runs it looks up the methods of the class or interface of the field upon which it has to delegate and then it tries to find it in the code.
If the method is not in the currently compiled version of the class it has to be created.
If it is there and it has the `Generated` annotation then it has to be regenerated.
If the code is the same then the framework will take care of not overwriting the code with something that is identical.
However, if the method is there already but it is NOT annotated with the annotation `Generated` then it means the method was created manually by the programmer.
In this case it must not be generated.

The already available `Generated` annotations have compile retention and they are not available when the test runs for reflective access.
Thus there is a need for a new `Generated` annotation.
If the program already has one then it can be used instead of the one provided by the Geci framework.
It can be configured in the builder of the generator.

* `filter` can filter out some of the fields from being delegated.
The default is `!static` a.k.a. all fields that are not static are candidate for delegation.
If there are many fields in a class and only a few need delegation then the simplest strategy is to set this parameter to `false` on the class annotation and `true` on each individual field that needs delegation.

* `methods` is a filter expression that selects the methods that are to be delegated.
Because the methods themselves may not be annotated and configured because they may happen to be in a class, the delegated class for which the source code is not there (e.g.: Map in the JDK) the only way to select the methods that are needed to be delegated are filter expressions.
The configuration parameter `filter` is already used to filter the methods that need delegation, thus the methods in the delegated class or interface should use a different configuration parameter: `methods`.
The default value for this parameter is `public & !static`.

<!-- end snip -->
