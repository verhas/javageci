# Mapper generating toMap and fromMap

This generator generates two maps into a class:

* `toMap()` that converts the actual object to a `Map<String,Object>`,
* and the static `fromMap(Map<String,Object> map)` that creates an object from the map.

The map will contain the names of the fields as keys and the values of the fields as values. If the value is an
instance of a class that has a `toMap()` method then the generated code will invoke it converting the object to a
map before using it as a value. Similarly if the map has a `fromMap(Map)` method then the generated `fromMap()` code
will invoke that on a field to convert the map stored as a value in the map.

If you look at the generated code you will see that the actual code is a bit more complex implementing  `toMap0()`
anf `fromMap0()` methods. This is needed to handle recursive object structures properly.  

Note that this way code generation may need to be executed twice: once to generate the code and all `toMap()` and
`fromMap()` methods and a second time to recognize that the fields represent classes that have (because of the
first round of code generation) `toMap()` and `fromMap()` methods.

The `fromMap()` method returns a new object. To create the new object it uses the `new XXXX()` default constructor. If
this is not appropriate then the class should be configured in the `@Geci` annotation with a factory string. This
will be used in the generated code. For example the sample `Person` class:

```Java
@Geci("mapper factory='newPerson()'")
public class Person extends AbstractPerson {
    ...
}
```

declares that instead of calling `new Person()` the generated code should invoke the static `newPerson()` method. (There
is no checking that the method exists. The configuration parameter can be anything. Whatever is configured in the
`factory` configuration string it will be used in the generated code verbatim.)

The methods will use all the fields that the class can use. This is all the fields that are declared inside the method
and also all the inherited fields that the code inside the class can access. It means all inherited non-static public,
non-static protected and in case the class is in the same package as the super class then the non-static package private
fields.

A sample use of this feature is that having a `fromMap()` method in a class makes it very easy to initialize a new
object setting all the fields. This is handy many times to initialize unit tests.

The `@Geci` annotation can also be configured with a `filter` parameter that controls with a selection expression
which field to include into the code generation. The default expression is `!transient & !static`. This filter can
be defined on each field separately overriding the definition on the class level. Since this overriding controls only
the selection of the single fields the filter defined right there should not be too complex. Simply
`filter='true'` will include the field into the code generation even if the global filter selection would rule it out
and `filter='false'` will exclude the field even if the global filter selection would include the field into the
code generation.

Filter expressions can be fairly complex and are documented on the page '[filter expressions](FILTER_EXPRESSIONS.md)'.

## Mapper invocation

The mapper can be invoked from a test using the following code

```java
    @Test
    void testMapper() throws Exception {
        Assertions.assertFalse(
                new Geci().source(maven().module("javageci-examples")
                .mainSource()).register(new Mapper()).generate(),
                Geci.FAILED);
    }
```

The `Mapper` class has four constructors. With the different arguments you can specify the annotation type that you want
to use on the generated methods signalling that they are generated and you can also specify a `Function<String,String>`
available at test execution time that can convert the field names to Map key values in case you want to use somewhat
modified key values and not those that are identical to the field names in the Java code.