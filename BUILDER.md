<!-- snip BuilderStart snippet="epsilon" append="snippets='Builder_ClassDescription_.*'" -->

# Builder

The builder generator generates a builder as an inner class into a class.
The builder class will have a method to specify the values of the fields.
The fields taken into account are filtered the usual way using the `filter` configuration field.
Usually (by default) only `private` fields will have a builder method, which are not `private` or `static`.

The generator is also capable generating aggregator methods when the field is a collection or some other type that can aggregate/collect several values.
A field is an aggregator type if the class of the field has at least one method named `add(x)` that has one argument.
The actual name is `add` by default but this is configurable.
For example if a field is of type `List` then it will be treated as aggregator type because the class `List` has a method `add`.
The name of the corresponding aggregator method in the builder will be `add` plus the name of the field with capitalized first letter.

There are several values that can be configured for the generator in the generators builder pattern.
This can be done in the test code where the generator is registered into the `Geci` object, or on the class/field level using annotations.

<!-- end snip -->

<!-- snip BuilderConfig snippet="epsilon" append="snippets='Builder_Config_.*'" -->

# Configuration

The configuration values can be configured on the builder of the generator in the test code where the generator object is registered into the `Geci` object that is used to run the generation.
The configuration items that are `String` can be configured on the [target class](TERMINOLOGY.md) and also on the fields individually.

* `generatedAnnotation` can define the class that will be
used to mark the generated methods and classes as generated.
By default it is the value if `javax0.geci.annotations.Generated.class`

* `filter` can define the filter expression
for the fields that will be included in the builder. The
default is `private & !static & !final`. If there is a field that
you want to include into the builder individually in spite of
the fact that the "global" filter expression excludes the
field then you can annotate the field with `@Geci("builder
filter=true")`. This can be a good practice in case the field
is a collection or some other aggregator and you want to have
the aggregator methods, but the field itself is final
initialized on the declaration line or in the constructor of
the [target class](TERMINOLOGY.md). If a field is final the
generator never generates a builder method that sets the
field itself because that is not possible and would result a
code that does not compile.

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
default value is `add`. In the standard
collection types this method is called `add` therefore the
default value is `add`. There can be two
reasons to configure this value for a specific field to be
different. One reason is the obvious, when the method that
aggregates values is named differently. The other reason when
the aggregating method is named `add` but
you do not want the builder to create aggregator methods for
this fields into the builder. In this case the field should
be defined to be an empty string, because it is certain that
the class will not have a method that has empty name.

* When an aggregator is generated the generated code checks
that the field to which we want to add the argument value is
not `null` if `checkNullInAggregator` is "true". The default
value is `"true"`. If this value is
configured to be false then this check will be skipped and
the generated code will call the underlying aggregator method
even when the field is null. In this case there will be a
{code NullPointerException} thrown. If the value is true,
then the check is done and in case the field is `null` then
the generated code will throw `IllegalArgumentException`
naming the field.

* `setterPrefix` can define a setter prefix. The
default value is `""`. If this value is
not `null` and not an empty string then the name of the
setter method will start with this prefix and the name of the
field will be added with the first character capitalized.

* The created `builder()` method returns a `Builder`
instance. The `Builder` class is a non-static inner class of
the [target class](TERMINOLOGY.md), because the build process
needs to access the fields of the [target
class](TERMINOLOGY.md) during the build process. Because of
this the method `builder()` (or whatever it is named in the
configuration `builderFactoryMethod`) needs to create a new
instance of the [target class](TERMINOLOGY.md). The default
is to invoke the default constructor. It is applied when
`factory` is null or empty string. If this
configuration value is anything else then this string will be
used as it is to create a new instance of the [target
class](TERMINOLOGY.md). For example if there is a static
method called `factory()` that returns a new instance of the
[target class](TERMINOLOGY.md) then this configuration
parameter can be set to `"factory()". The default values is
`""`.

* In the setter and aggregator methods the argument is
`x`. If you do not like this naming then
you can use `argumentVariable` to specify a different
name.
<!-- end snip -->
