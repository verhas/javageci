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

 
## `absolute='true'`
 
