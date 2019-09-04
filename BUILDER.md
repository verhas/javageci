<!-- snip BuilderStart snippet="epsilon" append="snippets='Builder_ClassDescription_.*'" -->

# Builder

The builder generator generates an inner class into the target source
file. The Builder class has methods to configure the filtered fields
of the target class. There is also a static method `builder()`
created that returns the builder for the class. The `Builder` inner
class also has a method `build()` that returns the built class.

<!-- end snip -->

<!-- snip BuilderConfig snippet="epsilon" append="snippets='Builder_Config_.*'" -->

# Configuration

* `generatedAnnotation` can define the class that will be
used to mark the generated methods and classes as generated.
By default it is the value if `javax0.geci.annotations.Generated.class`

* `filter` can define the filter expression for
the fields that will be included in the builder. The default
is `private & !static & !final`.

* `builderName` can define the name of the inner
class that implements the builder functionality. The default
value is `Builder`.

* `builderFactoryMethod` can define the name of the method
that generates a new builder class instance. The default
value is `builder`.

* `buildMethod` can define the name of the method
inside the builder class that closes the build chain and
returns the built class. The default value is
`build`.

* `aggregatorMethod` can define the name of the
aggregator method. The aggregator method is the one that can
add a new value to a field that is a collection type. The
default value is `add`.

* When an aggregator is generated the generated code checks
that the argument is not `null` if `checkNullInAggregator`
is "true". The default value is `"true"`.
If this value is configured to be false then this check will
be skipped and the generated code will call the underlying
aggregator method even when the argument is null.

* `setterPrefix` can define a setter prefix. The
default value is `""`. If this value is
not null and not an empty string then the name of the setter
method will start with this prefix and the name of the field
will be added with the first character capitalized.
<!-- end snip -->
