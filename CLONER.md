# Cloner Code Generator

<!-- snip Cloner_head regex="replace='|^~s~*~s||' escape='~'"-->
This code generator will generate a `copy()` method that returns the clone of the object referenced by `this` in the class body and also methods named `withXyzAbc()` for every `xyzAbc` field.
For example the sample class `NeedCloner` has the fields
<!-- end snip --> 
<!-- snip NeedCloner_fields trim="to=0"-->
```java
private final int aInt = 10;
protected int bInt = 55;
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

    it.bInt = bInt;
    it.inheritedExcludedField = inheritedExcludedField;
    it.inheritedField = inheritedField;
}

NeedCloner withBInt(int bInt) {
    final var it = copy();
    it.bInt = bInt;
    return it;
}

NeedCloner withInheritedField(String inheritedField) {
    final var it = copy();
    it.inheritedField = inheritedField;
    return it;
}

//</editor-fold>
```

The methods `withXXX()` by default return a new instance and sets the field directly to the value specified in the argument.
You can also use the cloner generator to have a code that alters the current object when you call the `withXXX()` method.
In that case you have to call `copy()` explicitly before the `withXXX()` calls.

## Configuration options

The configuration options can be used the regular way, from the generator builder, in the source code on the class level or on the fields.

<!-- snip Cloner_Config snippet="epsilon" trim="to=0" append="snippets='Cloner_Config_.*'" regex="replace='/private~sString~s/### `/' replace='/;.*$/`/' replace='|^~s*/~*||' escape='~'" skip="do"-->
### `generatedAnnotation = javax0.geci.annotations.Generated.class`

This builder-only parameter defines the class that is used to annotate the generated classes.
By default the Geci generated annotation class is used as shown above.
If this parameter is set to `null` in the builder then there will be no `Generated` annotation placed on the class.

### `filter = "!static & !final"`

The filter expression that selects the fields that are to be used during the cloning process.
By default all the fields that are not static and are not final are copied to the new object.
Static fields are shared by the different instances and thus any copy operation would just copy the value from a field back to the same field.
Final fields can not be written after the constructor has finished it's work, therefore they cannot be altered and copied.

The set of the fields is also controlled by the other option `declaredOnly` (see later).

### `cloneMethod = "copy"`

The name of the method that creates a clone of the object.
The default name is `copy()`.
Although it seems to be reasonable to name this method `clone()` it is recommended not to.
There is a `clone()` method defined in the class `Object` and this name collisions will cause obnoxious inconveniences.

### `cloneMethodProtection = "public"`

The protection of the copy method.
### `copyMethod = "copy"`

The name of the generated method that copies the values of the fields.

### `superCopyMethod = null`

The name of the copy method of the parent class that the copy method calls.
This has only significance if `copyCallsCuper` is `true`.
There is no check that the parent has this method or not.
It may happen that the parent does not have this method at the time of code generation because the code generation creates this method.

If the value is `null` or empty string then the configured value for `copyMethod` is used.
### `copyMethodProtection = "protected"`

The protection of the generated copy method.

### `cloneWith = "true"`

The `withXXX()` methods calls `copy()` to make a fresh copy of the object before setting the field.
In case you want to use the generated code where usually many fields are altered for the new object then it is better to set this configuration value to `false`.
In this case the `withXXX()` methods alter the actual object referenced by `this`.
When the cloning code was generated that way then call `copy()` explicitly on the original object before calling `withXXX()` methods presumably chained for many different fields.

### `copyCallsSuper = "false"`

Set this configuration value to `true` to make the copying method call the same method in the parent class through the reference `super`.

### `declaredOnly = null`

When you work with class hierarchy where each class in the inheritance chain then specifying this configuration parameter to true will limit the working to use only the fields that are declared in the actual class.

<!-- end snip -->

