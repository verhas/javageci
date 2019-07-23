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

Use `@AnnotationBuilder(module="name-of-module")` to generate the annotation
into a different maven module (in this example into the module "name-of-module").

## `in='name.of.package'`

Use `@AnnotationBuilder(in="name.of.package")` to generate the annotation in a different
(sub-)package.
 
## `absolute='true'`
 
Use `@AnnotationBuilder(absolute="yes")` to generate the annotation not relative to the
place of the generator, but to the java folder. Please note, that if you use `absolute="yes"`,
then you should also specify a package with the `in` parameter.

### Parameter example

Let's say you have two modules `example-generators` and `example-annotations`.
You put your generator (let's say `exampleGenerator`) in `example-generators` in the package `an.example.package`.

<table>
<thead>
    <tr>
        <td> <b>Annotation</b> </td>
        <td> <b>Will result in</b> </td>
    </tr>
</thead>
<tbody>
    <tr>
        <td><pre>@AnnotationBuilder</pre> </td>
        <td>
            module: example-generators <br/>
            package: an.example.package.annotation
        </td>
    </tr>
    <tr>
        <td><pre>@AnnotationBuilder(module="example-annotations")</pre>
        </td>
        <td>
            module: example-annotations <br/>
            package: an.example.package.annotation
        </td>
    </tr>
    <tr>
        <td><pre>@AnnotationBuilder(in="other")</pre>
        </td>
        <td>
            module: example-generators<br/>
            package: an.example.package.other
        </td>
    </tr>
    <tr>
        <td>
            <pre>@AnnotationBuilder (
    module="example-annotations",
    in="other"
)</pre>
        </td>
        <td>
            module: example-annotations <br />
            package: an.example.package.other
        </td>
    </tr>
    <tr>
        <td><pre>@AnnotationBuilder (
    module="example-annotations", 
    in="an.other.example", 
    absolute="yes"
)</pre>
        </td>
        <td>
            module: example-annotations <br/>
            package: an.other.example
        </td>
    </tr>
</tbody>
</table>

If you have multiple generators in a module, for which you would like to put all 
annotations in a common place, you can specify these parameters when registering 
the annotation builder.
