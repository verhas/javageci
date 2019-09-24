# Create annotation for Java generators

## Introduction

The Java generators works on source files that are some way noted for
the generators. There are multiple ways to tell a generator that a Java
source file nees its attention, One is add a Geci annotation. It is
usually `javax0.geci.annotations.Geci` but not neccessarily. As it is
described in the documentation of the [configuration](CONFIGURATION.md)

>
An annotation is a Geci annotation if
>
* the name of the annotation is `Geci`
* the annotation interface is annotated using a Geci annotation. Note
  that this definition is recursive and should be interpreted
  non-circular in the meaning that somewhere in the chain there has to
  be an annotation that is named `Geci`.

When a Geci annotation has a name other than `Geci` then the name of the
annotation is considered by Java::Geci as the mnemonic of the generator
(except that the first letter of the annotation is usually uppercase to
follow Java case conventions). This mnemonic is usually the first word
in the string defined in the `@Geci("mnemonic ...")` annotation form.

The original `javax0.geci.annotations.Geci` defines only one parameter,
`value`, which contains the configuration string in the form of

    @Geci("mnemonic key1=value1 key2=value2 ... keyN=valueN")

Custom Geci annotations can have other values. When Java::Geci sees that
the annotation has other parameters defined and not only the `value`
they are also read and made part of the configuration. Looking at the
same above if we create a Geci annotation named `mnemonic` (literally)
then we can write

    @Mnemonic(key1="value1",key2="value2", ... , keyN="valueN")
    
This helps the users of the generator to know the available parameters
as the IDE helps with autocompletion and any typo in the key names will
be discovered during compile time.

Creating an annotation for a generator is a simple 5 minutes work. (Less
than 5 minutes.) But it requires no brain. It is a simple mechanical
coding that can be done using an automated generator. Setting up the
generator may be more than 5 minutes, so if you just play around with
the generators, there is no need for this, but in case you create
something that you intend to maintain and use professionally then the
extra three minutes invested settip up the annotation generator pays
back, when the first time you change the configuration but you would
forget to maintain the annotation.

## What is `annotationBuilder`

The generator `annotationBuilder` generates annotations. The major
requirement of the generator is that the generator is configured the
standard way as it is supported and required by the generator
configuration builder and as it is documented in the
[configuration](CONFIGURATION.md) page.

The generator `annotationBuilder` looks at the generator it generates
annotation for (target generator) and creates the annotation in the
subpackage `annotation` under the package of the generator. The name of
the annotation will be the string returned by the method `menmonic()` of
the target generator with the first letter upper cased.

For example, if the mnemonic returns `accessor` the annotation will be
`@Accessor`.

It generate methods for every 'key' returned by the `implementedKeys()`
method, plus the `value()` method, which is the default.

## Example

```java
package ...example;

@AnnotationBuilder
public class ExampleGenerator extends AbstractJavaGenerator {
    @Override String mnemonic() { return "exampleGenerator"; }
    @Override public Set<String> implementedKeys() { Set.of("example" /*...etc.*/); }
}
```

For this the AnnotationBuilder will generate:

```java
package ...example.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("exampleGenerator")
@Retention(RetentionPolicy.RUNTIME)
public @interface ExampleGenerator {
    
    String value() default "";
    String example() default "";
    /* ...etc. */
}
```
## Configuration

Configuration parameters can be used in the builder interface of the
generator in the test code that instantiates the generator instance or
in the target generator Geci annotation. Since there is a Geci
annotation named `AnnotationBuilder` (also created by itself, so this is
recursive code generation) using that the configuration parameters are
available as annotation parameters. 

<!-- snip AnnotationBuilder_config snippet="epsilon" 
                 append="snippets='AnnotationBuilder_config_.*'"-->

* `set='name-of-the-source-set'`

By default the annotations are generated into the same source set where the target generator is. In case of a
multi-module project you may want to separate the annotations from the generators into a different module.
The reason for that can be that the generators are test scope dependencies. On the other hand the
annotations, albeit not used during run-time are compile scope dependencies. That is because these
annotations have run-time retention and are put into the JVM byte code by the compiler. Even though they are
not used during non-test run-time, they are there and thus the JAR defining them must me on the class/module
path.

Use this configuration either calling `set(""name-of-source-set")` in the test code when building the
annotation builder generator or `@AnnotationBuilder(module="name-of-source-set")` on the generator class to
define the name of the source set where the annotation will be generated.

Since the source set is defined in the test code it is reasonable to configure this paramater via the builder
interface of the generator.

* `in='name.of.package'`

This parameter can define the name of the package where the annotations will be created.

Use `@AnnotationBuilder(in="name.of.package")` to generate the annotation in a different package. You can
specify an absolute package simply specifying the full name of the package or you can specify a package that
is relative to the package of the target generator starting the configuration value with a dot. For example
the default value for this parameter is `.annotation` that will direct the annotation builder to generate the
annotation in the subpackage `annotation` right below the target generator.

Using empty string, or only a `.` (single dot) is implicitly relative and will generate the annotation to the
same package where the generator is. Note, however, when you separate the annotations from the generators to
different modules the different modules are not allowed to define classes in the same package. The Java
module system will not load such modules.
<!-- end snip -->

### Annotation Builder Test Code

The following code snippet shows the test code that creates the
annotation builder object and executes it for the code builders of
Java::Geci.

The name for the source set is `annotation-output` and it is defined as
a constant and the constant is used for the definition of the source
set and also the name of this prepared source set is passed to the 
annotation builder generator instance via the builder method `set()`.

The annotations in this case are generated in one single package,
`javax0.geci.core.annotations`. 

<!-- snip TestAnnotationBuilder -->
```java
public class TestAnnotationBuilder {

    public static final String ANNOTATION_OUTPUT = "annotation-output";

    @Test
    public void testAnnotationBuilder() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
            geci.source(maven().module("javageci-core").mainSource())
                .source(ANNOTATION_OUTPUT, maven().module("javageci-core-annotations").mainSource())
                .register(AnnotationBuilder.builder()
                    .set(ANNOTATION_OUTPUT)
                    .in("javax0.geci.core.annotations")
                    .build())
                .generate(),
            geci.failed());
    }
}
```



