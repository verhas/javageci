# Delegator Code Generator

The delegator can be used to create methods that invoke the same method of a field. 

An example is to implement multiple inheritance. You want to code a class that implements the `Map` interface and
you implement this using a `HashMap`. At the same time you want your class to be an instance of a `java.io.Writer`
that handles output filling some template data stored in the internal `HashMap`. In that case you should `extend`
the abstract class `Witer` and implement the interface `Map`.

To implement the interface `Map` you can have a field that is `Map` type and is initialized to be a `HashMap` or
some other implementation of `Map` that fits your needs. This also means that you have to create methods for each
method that the `Map` interface defines, each calling the same method and the same parameters in the `HashMap`.
Delegator creates these methods automatically. If you want to create some of the methods yourself then delegator
will just skip those during code generation.

The appropriate workflow is to create the test code:

```java
    @Test
    public void createDelegator() throws Exception {
        if (new Geci().source(maven().module("examples").javaSource()).register(new Delegator()).generate()) {
            Assertions.fail(Geci.FAIL);
        }
```

then annotate the class and the field:

```java


```


To use the delegator the class
containing the field 