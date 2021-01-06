# Mapper generating toMap and fromMap

This generator generates two methods into a class:

* `toMap()` that converts the actual object to a `Map<String,Object>`,
* `fromMap(Map<String,Object> map)` that creates an object from the map.

The map will contain the names of the fields as keys and the values of the fields as values.
If the value is an instance of a class that has a `toMap()` method then the generated code will invoke it converting the object to a map before using it as a value.
Similarly if the map has a `fromMap(Map)` method then the generated `fromMap()` code will invoke that on a field to convert the map stored as a value in the map.

If you look at the generated code you will see that the actual code is a bit more complex implementing  `toMap0()` and `fromMap0()` methods.
This is needed to handle recursive object structures properly.

Note that this way code generation may need to be executed twice when it is executed the first time.
The first run will generate the code and all `toMap()` and `fromMap()` methods for all the classes.
This run, however, will not use the `toMap()` and `fromMap()` methods of the other classes, because at this time they are not in the classes yet.
The second run recognizes that some of the the fields represent classes that have (because of the first round of code generation) `toMap()` and `fromMap()` methods.
Note that these two runs have to be executed with two compilation and test phase.
Simply running the two generation from a single unit test twice does not help because there is no compilation phase between them and to recognize that a field referenced class has the `toMap()` and `fromMap()` methods it is not enough that the source code has them.
They have to be in the compiled and loaded class so that the generator can find them via reflection.

The `fromMap()` method returns a new object.
To create the new object the generated code uses the `new XXXX()` default constructor.
The generator does not check if the default construtor can be used this way or not.
If it cannot be used that way then the generator has to be configured in the class source code using the `@Geci` annotation or the editor-fold segment parameters with a factory string.
This string will be used in the generated code.
For example the sample `Person` class has the following code:

```Java
@Geci("mapper factory='newPerson()'")
public class Person extends AbstractPerson {
    ...
}
```

It declares that instead of calling `new Person()` the generated code should invoke the static `newPerson()` method.
(There is no checking that the method exists or that the given string is syntactically correct.
The configuration parameter can be anything.
Whatever is configured in the `factory` configuration string it will be used in the generated code verbatim.)

The methods will use all the fields that the class can use.
This is all the fields that are declared inside the method and also all the inherited fields that the code inside the class can access.
It means all inherited non-static public, non-static protected and in case the class is in the same package as the super class then the non-static package private fields.

A sample use of this feature is that having a `fromMap()` method in a class makes it relatively easy to initialize a new object setting all the fields.
This can be handy many times to initialize unit tests.

The `@Geci` annotation can also be configured with a `filter` parameter that controls with a selection expression which field to include into the code generation.
The default expression is `!transient & !static`.
This filter can be defined on each field separately overriding the definition on the class level.
Since this overriding controls only the selection of the single fields the filter defined right there should not be too complex.
Simply `filter=true` will include the field into the code generation even if the global filter selection would rule it out and `filter=false` will exclude the field even if the global filter selection would include the field into the code generation.

Filter expressions can be fairly complex and are documented on the page '[filter expressions](FILTER_EXPRESSIONS.adoc)'.

## Mapper invocation

The mapper can be invoked from a test using the following code

<!-- snip TestMapper_testMapper -->
```java
    @Test
    void testMapper() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven().module("javageci-examples")
                        .mainSource())
                        .register(Mapper.builder().build()).generate(),
                geci.failed());
    }
```

## Configuration parameters

<!-- snip Mapper_configurationParameters snippet="epsilon"
                               append="snippets='Mapper_Config_.*'" -->

* `filter` can be used to to select the define the selector expression to select the fields that will be taken into account for the map conversion.
If a `final` field is selected by the expression it will be taken in to account when generating the `tMap()` method, but it will be excluded from the `fromMap()` method because being `final` there is no way the `fromMethod()` could modify the value of the field.

* `generatedAnnotation` can ge used to specify the annotation that is used to annotate the generated methods.
By default it is the `javax0.geci.annotations.Generated` class.

* `field2MapKeyMapper` is a function that converts the name of the field, which is already a string into another string.
It is useful in case you want to use different key names in the map.
You can, for example convert the field names to all capital or insert a prefix before the names.
The default is not to change the name and use the key, which is the field name.

* `factory` is a string that is the code to create a new instance of the class.
The default is a "new {{ClassName}}()" like expression where the actual class name is used after the keyword `new`.
<!-- end snip -->
