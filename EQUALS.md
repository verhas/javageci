# equals() and hashCode() Code Generator

The generator `equals` generates the `equals()` and the `hashCode()` methods. Generating these two
functions takes into account all fields, except

* static fields,
* fields that have `@Geci("equals exclude='true')` annotation

If `equals()` is provided by manual code then it will not be generated.
If `hashCode()` is provided by manual code then it will not be generated.

The generated methods will ve annotated with `@Override` and `@Generated("equals")` annotations.

The generated methods will have the standard and usual structure. If the parameter `useObjects`
is used then the `Objects.equals()` static method is used to compare object fields and the
`Objects.hashCode()` static method will be used to calculate the hash code.
If a field is annotated with `@Geci("equals notNull='true'")` then the code in `equals()` will
not check if the field is `null` or not. If the field is `null` even though it was annotated
to be not null then the execution will most probably cause null pointer exception.

You can also use `@Geci("equals subclass='ok')` on the class annotation to make equals work
with subclasses, so that `a.equals(b)` can be `true` even if `a` has differnet class than `b`
but `b` is subclass of `a`. Note that this is a practice that is needed by some frameworks that
return proxy objects and still use the `equals()` for comparison. At the same time this construct
violates the definition how `equals()` should work.   