# Java::Geci module structures and dependencies

This document describes the modules and their dependencies in Java::Geci.
The understanding of this structure helps to decide which modules are needed when you use the library.

Module Structure Diagram with dependencies

![model structure](images/module-dependency.svg)

## Introduction

Java::Geci is a multi module project, and it contains several libraries/jar files when it is compiled.
The dependency vectors for these jar files are

    <groupId>com.javax0.geci</groupId>
    <artifactId>javageci-MODULE</artifactId>
    <version>version</version>

expressed as Maven dependency.

The JPMS module names have the form `geci.MODULE`.
Here `geci.` is a constant prefix, `MODULE` is the individual module name.

There are 8 modules (each can stand in place of `MODULE` above):

* `api` defines the interfaces.
* `engine` is the framework implementation.
* `annotations` defines annotation interfaces, for example `@Geci` and `@Generated`
* `core` contains the generators developed inside the project
* `core-annotations` defines annotation interfaces for each of the generators, which are defined in the module `core`.
   When using a generator the code can use one of these annotations instead of using `@Geci("...")` with the generator name in the annotation string.
* `tools` contains helper classes that are not inherent part of the framework, but are very handy when writing generators 
* `examples` example code using the code generators and some code generators that are for example purposes.
   They are not meant to be used in production.
* `jamal` an experimental code generator that uses the Jamal macro processor.
   This module is not developed further and is deprecated.
   New versions are automatically released with the other modules, but there is no change in the functionality.
    
## Modules

### `api`

This module contains the interfaces that define the API of the framework.
Programs that implement code generators depend on this module, because this module contains the `Generator` interface that the generator classes have to implement.

Note that most of the generators also depend on the `tools` module for support library use and to extend the readily available abstract generator classes instead of implementing the interface from scratch.

The module `engine` implements the interfaces defined in this module except the interface `Generator`.
 
### `engine`

This module contains the framework that does all the support work for the generators like scanning the file system for source code; starting up the generators that are needed by the individual source codes and writing back changes into source files in case some generator created some new code.

Programs that use code generators depend on this module.
Since this framework is used only during unit test execution the dependency should be `test` scope.

Note that the programs using generators do not need to depend on the `api` module.

### `annotations`

This module contains the `@Geci` and the `@Generated` annotations.
Programs that use code generators may depend on this module.
The classes and members of the classes may be annotated using the `@Geci` annotations and code generators may generate code that annotates some methods or fields or whatever the generator generates using the `@Generated` annotations.

Because the annotations remain in the code even though they are not used during the production execution the scope of the dependency should be `compile` (this is the default dependency in Maven).

Note that the use of this module is not a must.
The framework is flexible and automatically will recognize any annotation that is named `@Geci` or just any annotation that itself is annotated with `@Geci` (not a specific one, just an annotation named `@Geci`) or with an annotation that was annotated similarly (any level recursively).

Developers may decide to drop the dependency and use their own annotations named `@Geci` or any other name.
(At least one annotation has to be named `@Geci`, which is used to annotate the other annotations.)

Simple use of the generators may not even need any annotation since the need for code generation is recognized when the source code contains an `editor-fold` segment with an `id` that matches the mnemonic of the generator.

*Note:* that there is an annotation interface named `Generated` in the JDK.
That annotation has a retention policy `SOURCE`, which means that the compiled class byte code does not contain this annotation and thus cannot be reflectively queried.
This is the reason why there is the need for the `@Generated` annotation in this module.

### `core`

This module contains the code generator classes that are developed as part of the Java::Geci project.
These include

* accessor
* builder
* delegator
* equals
* factory
* fluent
* mapper
* jdocify
* iterator

Programs that use one of these generators should have a dependency on this module.
Since this module has a transitive dependency on the module `engine` there is no need for explicitly require dependency on the `engine` module for programs that use the code generators.

### `core-annotations`

This module defines annotation interfaces for each and every generator, which is defined in the module `core`.
These interfaces are automatically generated using a special generator `javax0.geci.annotationbuilder.AnnotationBuilder` which is in the `core` module.

When an annotation interface is annotated with `@Geci("xxx")` then the annotation itself can be used instead of using the annotation `@Geci("xxx")` on the class that needs the generator.
For example the annotation interface `Builder` has the annotation `@Geci("builder")` as

```java
@Geci("builder")
@Retention(RetentionPolicy.RUNTIME)
public @interface Builder {
...
``` 
which means that whenever we need the `Builder` generator to work on a class we can use the annotation `@Builder` instead of `@Geci("builder")`. 

### `tools`

This module contains support tools that many code generators use.
This module contains also the abstract generator classes that actual generator implementation may extend instead of implementing the `Generator` interface directly.
When writing a new generator you are free to use these classes.
As a matter of fact, you can use the classes in this module totally independent of Java::Geci if you wish.

### `examples`

Examples contain sample use of code generators.
There are some code generators, like the "Hello, World" code generator, which are not meant to be used in production.
There are also some integration tests for some of the generators implemented in the module `core`.

### `jamal`

This module contains an experimental code generator that lets you program your Java code with a preprocessor.
The usability of the tool is questionable, thus this is still an experiment and is currently deprecated.
Later the deprecation may be removed or the module may totally be eliminated.

## Recommendations

You can have simple generators in your project in the `src/test` directory.
More than simple generators deserve their own project. 

Generators implemented in a separate project will contain their code in the `src/main` source directory and evidently the dependencies should also have `compile` scope instead of `test`.
The project using the generator will have a `test` scope dependency on the generator project.

When you develop one or more generator in a separate project and you use JPMS then it is recommended to `require transitive geci.engine`.
This will release the generator using programs from the burden to express their dependency on the `engine` module.