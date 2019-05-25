# Java::Geci module structures and dependencies

This document describes the modules and their dependencies in
Java::Geci. The understanding of this structure helps to decide which
modules are needed when you use the library.

Module Structure Diagram 

![model structure](images/module-dependency.svg)

## Introduction

Java::Geci is a multi module project and it contains several
libraries/jar files when it is compiled. The dependency vectors for
these jar files are

    <groupId>com.javax0.geci</groupId>
    <artifactId>javageci-MODULE</artifactId>
    <version>version</version>
    
There are 7 modules:

1. `api` the API interfaces.
2. `engine` the framework implementation.
3. `annotations` annotation interfaces, for example `@Geci` and
    `@Generated`
4. `core` generators developed inside the project
5. `tools` helper classes that are not inherent part of the framework
6. `examples` example code using the code generators and some code
    generators that are for example purposes and are not meant to be
    used in production
7. `jamal` an experimental code generator that uses the Jamal macro
    processor
    
## Modules

### `api`

This module contains the interfaces that define the API of the
framework. Programs that implement code generators depend on this
module, because this module contains the `Generator` interface that the
generator classes have to implement.

Note that most of the generators also depend on the `tools` module for
support library use and also to extend the readily available abstract
generator classes instead of implementing the interface from zero.

The module `engine` implements the interfaces of this module except the
`Generator`.
 
### `engine`

This module contans the framework that does all the support work for the
generators like scanning the file system for source code; starting up
the generators that are needed by the individual source codes and
writing back changes into source files in case some generator created
some new code.

Programs that use code generators depend on this module. Since this
framework is used only during unit test execution the dependency should
be `test` scope.

Note that the programs using generators do not need to depend on the
`api` module.

### `annotations`

This module contains the `@Geci` and the `@Generated` annotations.
Programs that use code generators may depend on this module. The classes
and members of the classes may be annotated using the `@Geci`
annotations and code generators may generate code that annotates some
methods or fields or whatever the generator generates using the
`@Generated` annotations.

Because the annotations remain in the code even though they are not used
during the production execution the scope of the dependency should be
`compile` (this is the default dependency in Maven).

Note that the use of this module is not a must. The framework is
flexible and automatically will use any annotation that is named `@Geci`
or just any annotation that itself is annotated with `@Geci` or with an
annotation that was annotated similarly (any level).

Developers may decide to drop the dependency and use their own
annotations named `@Geci` or any other name. (At least one annotation
has to be named `@Geci`, which is used to annotate the other
annotations.)

Simple use of the generators may not even need any annotation since the
need for code generation is recognized when the source code contains an
`editor-fold` segment with an `id` that matches the mnemonic of the
generator.

*Note:* that there is an annotation interface named `Generated` in the
JDK. That annotation has a retention policy `SOURCE`, which means that
the compiled class byte code does not contain this annotation and thus
cannot be reflectively queried. This there is the need for this
annotation.

### `core`

This module contains the code generator classes that are developed as
part of the Java::Geci project. These include

* accessor
* builder
* delegator
* equals
* factory
* fluent
* mapper

Programs that use one of these generators should have a dependency on
this module.

### `tools`

This module contains support tools that many code generators use. This
module contains also the abstract generator classes that actual
generator implementation may extend instead of implementing the
`Generator` interface directly.

### `examples`
### `jamal`
