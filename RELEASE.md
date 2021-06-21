# Release History

This document contains the release history of Java::Geci from the newest
to the oldest release. If you see that the first "release" in this document
is `-SNAPSHOT` is means that the version is not yet released. However, the
changes are already collected in the file.

There is no guarantee that a `-SNAPSHOT` version will ever be released. For
example the `1.4.1-SNAPSHOT` version was in this file for some time and then
the cumulated changes were so significant, the version became
`1.5.0-SNAPSHOT` without releasing `1.4.1` ever.

# 1.6.2.1

Same as 1.6.2 except top-level pom.xml was modified so that the deployment to maven central is automatic. Up to 1.6.1 there was an error that uploaded the artifacts and left the repo open and did not release.

Jamal 1.6.3 is used, where you can

```
            {%#for (a,b,c) in `END`list,with,), in, it `END` = ... %}
```

# 1.6.2

Jamal dependency version upped up to 1.6.3

# 1.6.1 Jamal templating

Support macros for Jamal templating were redesigned and it uses now Jamal 1.5.3
There is a sample to generate unit test proxy static inner class

# 1.6.0 - mvn central deployment pending

Documentation update. It should have been 1.5.1 but accidentally it became 1.6.0

This version uses the OSSH deploy plugin and is configured to send the artifacts automatically to maven central without manual hacking.

# 1.5.0

* A fix in the fluent generator.
  In prior versions when a grammar started with an optional method or with an optional expression (e.g.: zero or more times alternative methods, anything that is `(...)*` in the syntax description) then the generated interface structure required ONE or more invocations instead of ZERO or more.

* Fluent generator was annotated with `@AnnotationBuilder` and hence, there is a `@Fluent` annotation in `core-annorations`.

* Jdocify was developed.

* GeciException thrown from the generators are caught, enriched with the source file information the generator was working on and then thrown again in the Geci process.
  This eliminates the need to fetch the file name from the source code and to add it to the exception where it is originally thrown.

* The lexical analyser keeps the original format of the characters and strings.
  It can also be queried in case the generator needs to know the exact escape sequences in the string or character literal.

* Generators replying on the lexical analysis do not need to delete a lexical element from the list of lexical elements of a source file and insert a new one when it can simply be done replacing the lexeme string.
  It is not possible to change the type of the lexical element though.

* A bug prevented the proper comparing of any Java file that contained string or character with `\n` or `\r` literals.
  The code was throwing an exception thus code generation was aborted.

* A bug prevented the proper comparing of any Java file that contained hexadecimal long literals.
  The code was throwing an exception thus code generation was aborted.

# 1.4.0 feature release (2019-11-25)

* Segment API extended so that you can query individual segment parameters by their key.
  
* A bug fixed that was in the experimental and still not fully documented lexer regex macthing implementation
  
* There is a new generator `iterate` that aims to replace the `repeated` and later the `templated` generators.
  The generator `repeated` is deprecated.    

# 1.3.0 feature release (2019-10-28)

* Support Java 8

  The code is still Java 11 level, but there is no use of any Java 8+ JDK class or method.
  The build features a `JVM8` profile that will create Java 8 compatible byte code.
  The generated JavaDoc in the `1.3.0-JVM8` release is erroneous and there is no intention to fix it.
  The source and JavaDoc jars are available from the normal `1.3.0` release.
   
* generators can access lexical elements lists
  
  There is a new class `JavaLexed` that can be used to match the JavaSource using pattern matching (kind of regex but based on lexical elements instead of characters) and it is also possible to update/modify the source code.

* Record generator

  There is a new generator that creates classes that mimic the proposed Java `record` functionality that will be available in the future some time.
  This generator also demonstrate how to use the `JavaLexed` class in generators that want to modify the Java source on lexical element level.

* document generation supports JavaDoc

  You can insert doclets into JavaDoc code without special code inserter generator (the special code inserter is part of the library now).


# 1.2.0 feature release (2019-07-26)

* Documentation generation snippet support

  There is a whole new module that contains generators that support document generation inserting snippets cut off from code into documents.

* Generator configuration unified and supported by code generation

* parameter parsing supports " inside string, other escape sequences, and numeric and boolean values

* multi-line segments headers are supported in markdown documents

* Java generated code is compared on the lexical level and thus reformatted code is not regenerated

# 1.1.0 feature release (2019-01-29)

* Fluent API changes:
  * Fluent API can be defined in a single string using a simple syntax definition calling the fluent API building fluent API method `syntax()`
  * Wrapper contains only those methods that are actually used in fluent API building
  * Builder methods can also be `private`, `protected` or package private and they do not need to be public.
    They are wrapped anyway.
  * You can explicitly include a method into the fluent API wrapper calling `include()`
  
* Geci core changes:
  * Generators that work on Java classes and are implemented extending the `AbstractGenerator` automatically get the configuration from comment if the class is not annotated
  * Any annotation can be used that is named `Geci` not only the predefined in the given library
  * Annotation parameters that are defined and have string value are appended to the configuration.
    It makes sense when the annotation is defined for the using project, as the provided `@Geci` annotation does not have other parameters only `value`
  * File collections can be limited using regular expressions matching file names.
  * Generation throws an error if generators are configured so that they do not work on any source.


# 1.0.0 initial release (2019-01-19)