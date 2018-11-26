# equals() and hashCode() Code Generator

The generator `equals` generates the `equals()` and the `hashCode()` methods. Generating these two
functions takes into account all fields, except

* static fields,
* fields that have `@Geci("equals exclude='true')` annotation

If `equals()` is provided by manual code then it will not be generated.
If `hashCode()` is provided by manual code then it will not be generated.

The generated methods will be annotated with `@Override` and `@Generated("equals")` annotations.

The generated methods will have the standard and usual structure.

## `exclude='true'`

Use `@Geci("equals exclude='true')` annotation on a field to exclude the field from the calculation of
`equals()` and `hashCode()`.

## `useObjects='true'`

Use `@Geci("equals useObjects='true')` annotation on the class to make a generated code that uses
`Objects.equals()` and `Objects.hashCode()`.
 
## `notNull='true'` for non-null fields
 
If a field is annotated with `@Geci("equals notNull='true'")` then the code in `equals()` will
not check if the field is `null` or not. If the field is `null` even though it was annotated
to be not `null` then the execution will most probably cause null pointer exception.

## `subclass='ok'`

You can also use `@Geci("equals subclass='ok')` on the class annotation to make equals work
with subclasses, so that `a.equals(b)` can be `true` even if `a` has different class than `b`
but `b` is subclass of `a`. Note that this is a practice that is needed by some frameworks that
return proxy objects and still use the `equals()` for comparison. At the same time this construct
violates the definition how `equals()` should work. If subclassing is enabled then the `equals()`
method will be declared to be `final`.