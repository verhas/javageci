# Automated annotation generation for generators

The generator `annotationBuilder` generates annotations.

By default, the annotation is generated in a sub-package called 'annotations', in
the same package the generator is in. It imports three things (which are needed every time):
* java.lang.annotation.Retention
* java.lang.annotation.RetentionPolicy
* javax0.geci.annotations.Geci
   
The name of the annotation will be the same as the `mnemonic()` of the generator, 
except with a capital starting letter.

I.e.: If the mnemonic returns "accessor" the annotation will be @Accessor.

It will also generate an empty method for every 'key' per the `implementedKeys` method,
and a `value()` method.

## Example

```java
package com.verhas.example;

@AnnotationBuilder
public class ExampleGenerator extends AbstractJavaGenerator {
    @Override String mnemonic() { return "exampleGenerator"; }
    @Override public Set<String> implementedKeys() { Set.of("example" /*...etc.*/); }
}
```

For this the AnnotationBuilder would generate:

```java
package com.verhas.example.annotation;

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

## `module='name-of-module'`

Use `@AnnotationBuilder(module="name-of-source")` to generate the annotation
into a different source (in this example into the source registered with "name-of-source").
To use this feature, you have to add the destination source with the specified name.

## `in='name.of.package'`

Use `@AnnotationBuilder(in="name.of.package")` to generate the annotation in a different
(sub-)package.

If you use `in=""` and not use `absolute="yes"`, the annotation generator will skip the file,
to avoid overwriting the file for which you want to create the annotation.
 
## `absolute='true'`
 
Use `@AnnotationBuilder(absolute="yes")` to generate the annotation not relative to the
place of the generator, but to the java folder. Please note, that if you use `absolute="yes"`,
then you should also specify a package with the `in` parameter.

### Parameter example

Let's say you have two modules `example-generators` and `example-annotations`.
You put your generator (let's say `exampleGenerator`) in `example-generators` in the package `an.example.package`.
Then, you add these sources when you register the annotation generator, using their names as identifiers, i.e.:
```java
package com.verhas.example.tests;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestExampleGenerator {
    @Test
    void testExample() {
        final var geci = new Geci();
        Assertions.assertFalse(
            geci.source(
                Source.Set.set("example-generators"),
                maven("example-generators").mainSource())
            .source(
                Source.Set.set("example-annotations"),
                maven("example-annotations").mainSource()
            )               
            .register(AnnotationBuilder.builder().build())
            .generate(),
            geci.failed()
        );
    }
}
```
Then you could use the `@AnnotationBuilder` annotation in the 'example-generators' module.

| Parameters                                                            | Will result in                         |
| --------------------------------------------------------------------- | -------------------------------------- |
| No parameters                                                         | module: example-generators             |
|                                                                       | package: an.example.package.annotation |
| `module="example-annotations"`                                        | module: example-annotations            | 
|                                                                       | package: an.example.package.annotation |
| `in="other"`                                                          | module: example-generators             |
|                                                                       | package: an.example.package.other      |
| `in="other", module="example-annotations"`                            | module: example-annotations            |  
|                                                                       | package: an.example.package.other      |
| `module="example-annotations", in="an.other.example", absolute="yes"` | module: example-annotations            |
|                                                                       | package: an.other.example              |

If you have multiple generators in a module, for which you would like to put all 
annotations in a common place, you can specify these parameters when registering 
the annotation builder.
