# Delegator Code Generator

The delegator can be used to create methods that invoke the method of the same name and arguments through a field. 

An example is to implement multiple inheritance. You want to code a class that implements the `Map` interface and
you implement this using a `HashMap`. At the same time you want your class to be an instance of a `java.io.Writer`
that handles output filling some template data stored in the internal `HashMap`. In that case you should `extend`
the abstract class `Witer` and implement the interface `Map`.

To implement the interface `Map` you can have a field that is `Map` type and is initialized to be a `HashMap` or
some other implementation of `Map` that fits your needs. This also means that you have to create methods for each
method that the `Map` interface defines, each calling the same method and the same parameters in the `HashMap`.
Delegator creates these methods automatically. If you want to create some of the methods yourself then delegator
will just skip those during code generation.

The appropriate workflow is to create first the test code that will check the generated code or generate the code when
the check fails.

```java
    @Test
    public void createDelegator() throws Exception {
        Assertions.seertFalse(new Geci().source(maven().module("javageci-examples").javaSource())
                                  .register(new Delegator()).generate(),Geci.FAIL);
        }
```

then annotate the class and the field:

```java
@Geci("delegator")
public class SampleComposite<K,V> {
    @Geci("delegator id='contained1'")
    Map<K,V> contained1;
```

When this is done then you can run the test code from the IDE and get the methods:

```java
    @javax0.geci.annotations.Generated("delegator")
    public V compute(K arg1, java.util.function.BiFunction<? super K,? super V,? extends V> arg2) {
        return contained1.compute(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V computeIfAbsent(K arg1, java.util.function.Function<? super K,? extends V> arg2) {
        return contained1.computeIfAbsent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V computeIfPresent(K arg1, java.util.function.BiFunction<? super K,? super V,? extends V> arg2) {
        return contained1.computeIfPresent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V get(Object arg1) {
        return contained1.get(arg1);
    }

    ...
```


Then you can extend the header of the class to

```java
@Geci("delegator")
public class SampleComposite<K,V> implements Map<K,V>{
    @Geci("delegator id='contained1'")
    Map<K,V> contained1;
```

Now, that the generator implemented the delegating methods the class implements the interface. If you
run the code generating test it will run fine without fail.

The delegator creates each method annotated with the `Generated()` annotation. This helps the delegator
recognize when the programmer manually implements some of the delegator methods. A manually created method
should not be annotated with the `Generated()` annotation and the delegator will simply skip this method
and does not generate this specific method.

You can use the delegator code generator without compile dependency on the Java::Geci annotation module.
Although `Geci` and `Generated` are defined in that module you are free to use any other annotation you may
define in your own project. The framework will work any annotation so long as long their simple name is `Geci`.
The generator does not even require that the name of the annotation is `Generated`. All it requires is to use
the `new Delegator(YourAnnotation.class)` constructor and then it will use that annotation to mark the
generated methods and eventually will look for this annotation in the method when it decides if an already existing
method was manually created or generated.

You can also filter the methods that have to be generated to delegate functionality. The class or field annotation

```java
@Geci("delegator filter='static | protected")
public class SampleComposite<K,V> {
    @Geci("delegator id='contained1' filter='!static & public'")
    Map<K,V> contained1;
```

can contain a parameter `filter` that defines a filtering expression. In the example above the delegators are
created in the class for methods that are `static` or `protected`. This, however, does not apply to the methods
invoked through `contained1` because it defines a different filter to generate delegator methods only for methods
that are not `static` and the same time are `public`. If there is no filter defined this is the default behaviour.

For filter expressions read [about filter expressions](FILTER_EXPRESSIONS.md)