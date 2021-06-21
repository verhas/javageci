# equals() and hashCode() Code Generator

The generator `equals` generates the `equals()` and the `hashCode()` methods.
Generating these two functions takes into account all fields by default, except static fields.
This can be altered using the `filter` configuration parameter.

If `equals()` is provided by manual code then it will not be generated.

If `hashCode()` is provided by manual code then it will not be generated.

The generated methods will be annotated with `@Override` and `@Generated("equals")` annotations.

The generated methods will have the standard and usual structure.

## `filter='expression'`

Use `@Geci("equals filter='expression')` annotation to specify a selector expression that will control which fields to include into the `equals()` and `hashCode()` generation.
The default expression is `!static`.

For more information on filter expression see [filter
expressions](FILTER_EXPRESSIONS.adoc).

When this configuration key is used in an annotation on a specific field then it makes sense if it is either `true` or `false` since this specific expression overriding the global configuration controls only the inclusion or exclusion of the specific field.
To include a field no matter what the global configuration is, annotate it with `@Geci("equals filter='true'")`.
To exclude a field no matter what the global configuration is, annotate it with `@Geci("equals filter='false'")`.

## `useObjects='true'`

Use `@Geci("equals useObjects='true')` annotation on the class to make a
generated code that uses `Objects.equals()` and `Objects.hashCode()`.
The default is not to use the `Objects` class methods.
 
## `notNull='true'` for non-null fields
 
If a field is annotated with `@Geci("equals notNull='true'")` then the code in `equals()` will not check if the field is `null` or not.
If the field is `null` even though it was annotated to be not `null` then the execution will most probably cause null pointer exception.

If this configuration is used in the annotation on the class itself then all fields will inherit this configuration and will be treated by the code generator as guaranteed to be not null.

## `subclass='ok'`

You can use `@Geci("equals subclass='ok')` on the class annotation to make the generated `equals()` work with subclasses, so that `a.equals(b)` can be `true` even if `a` has different class than `b` but `b` is subclass of `a`.
Note that this is a practice that is needed by some frameworks, which return proxy objects but still use the `equals()` for comparison.
At the same time this construct violates the definition how `equals()` should work.
If subclassing is enabled then the `equals()` method will be declared `final`.

## `hashFilter='true'`

You can specify a [filter expressions](FILTER_EXPRESSIONS.adoc) to exclude certain fields from the generated `hashCode()` method.
Note that the fields for the `hashCode()` method are filtered by the filter expression `filter` first and then using the optional `hashFilter`.
It is not possible to include a field into the calculation of the hash code, which field is not used in the generated `equals()` method.
