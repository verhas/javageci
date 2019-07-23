# Automated annotation generation for generators

The generator `annotationBuilder` generates annotations.

The name of the annotation will be the same as the `mnemonic()` of the generator, 
except with a capital starting letter.

I.e.: "accessor" -> @Accessor

It will also generate an empty method for every key the generator has and a `value()` method. 


## `module='name-of-module'`

Use `@AnnotationBuilder(module="name-of-module")` to generate the annotation
into a different maven module (in this example into the module "name-of-module").

## `in='name.of.package'`

Use `@Geci("equals useObjects='true')` annotation on the class to make a
generated code that uses `Objects.equals()` and `Objects.hashCode()`.
The default is not to use the `Objects` class methods.
 
## `absolute='true'`
 
If a field is annotated with `@Geci("equals notNull='true'")` then the
code in `equals()` will not check if the field is `null` or not. If the
field is `null` even though it was annotated to be not `null` then the
execution will most probably cause null pointer exception.

If this configuration is used in the annotation on the class itself then
all fields will inherit this configuration and will be treated by the
code generator as guaranteed to be not null.
