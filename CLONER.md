# Cloner Code Generator

<!-- snip Cloner_head regex="replace='|^~s~*~s||' replace='|~s*~*/||' replace='|/~*~*||' tilde='yes'"-->

This code generator will generate a `copy()` method that returns the
clone of the object referenced by `this` in the class body and also
methods named `withXyzAbc()` for every `xyzAbc` field. For example the
sample class `NeedCloner` has the fields

<!-- end snip --> 
<!-- snip NeedCloner_fields trim="to=0"-->
```java
private final int aInt = 10;
protected int bINt = 55;
```

and it also inherits two fields

<!-- snip AbstractNeedCloner_fields trim="to=0"-->
```java
public String inheritedField;
@Geci("cloner filter='false'")
public String inheritedExcludedField;
```

and based on that the generator produces the code

<!-- snip NeedCloner_generated_code trim="to=0"-->
```java
//<editor-fold id="cloner">
@javax0.geci.annotations.Generated("cloner")
public NeedCloner copy() {
    final var it = new NeedCloner();
    copy(it);
    return it;
}
protected void copy(NeedCloner it) {

    it.bINt = bINt;
    it.inheritedExcludedField = inheritedExcludedField;
    it.inheritedField = inheritedField;
}

NeedCloner withBINt(int bINt) {
    final var it = copy();
    it.bINt = bINt;
    return it;
}

NeedCloner withInheritedField(String inheritedField) {
    final var it = copy();
    it.inheritedField = inheritedField;
    return it;
}

//</editor-fold>
```

The methods `withXXX()` by default return a new instance and sets the
field directly to the value specified in the argument. You can also use
the cloner generator to have a code that alters the current object when
you call the `withXXX()` method. In that case you have to call `copy()`
explicitely before the `withXXX()` calls.

## Configuration options

The configuration options can be used the regular way, from the
generator builder, in the source code on the class level and also on the
fields.

### `generatedAnnotation = Generated.class`

This builder only parameter defines the class that is used to annotate
the generated classes.

### `filter = "!static & !final"`

The filter expression that selects the fields that are to be processed.

### `cloneMethod = "copy"`

The name of themethod that creates a clone of the object.

### `cloneMethodProtection = "public"`

The protection of the cloning method.

### `copyMethod = "copy"`

The name of the generated method that copies the values of the fields.

### `superCopyMethod = null`

The name of the copy method of the parent class that the copy method
calls. This has only significance if `copyCallsCuper` is `true`. There
is no check that the parent has this method or not. It may happen that
the parent does not have this method at the time of code generation
because the code generation creates this method.

If the value is `null` or empty string then the configured value for
`copyMethod` is used.

### `copyMethodProtection = "protected"`

The protection of the generated copy method.

### `cloneWith = "true"`

The `withXXX()` methods call `copy()` to make a frest copy of the object
before setting the field. In case you want to use the generated code
where usually many fields are altered for the new object then it is
better to set this configuration value to `false`. In this case the
`withXXX()` methods alter the actual object referenced by `this`. When
the cloning code was generated that way then call `copy()` explicitely
on the original object before calling `withXXX()` methods presumably
chanied for many different fields.

### `copyCallsSuper = "false"`

Set this configuration value to `true` to make the copiing method call
the same method in the parent class through the reference `super`.

### `declaredOnly = null`

When you work with class hierarchy where each class in the inhertiance
chane 
