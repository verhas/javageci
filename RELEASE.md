# Release History

1.3.0 feature release

* Support Java 8

  The code is still Java 11 level, but there is no use of any Java 8+
  JDK class or method. The build features a `JVM8` profile that will
  create Java 8 compatible byte code. The generated JavaDoc in the
  `1.3.0-JVM8` release is erroneous and there is no intention to fix it.
  The source and JavaDoc jars are available from the normal `1.3.0`
  release.
   
* generators can access lexical elements lists
  
  There is a new class `JavaLexed` that can be used to match the
  JavaSource using pattern matching (kind of regex but based on lexical
  elements instead of characters) and it is also possible to
  update/modify the source code.

* Record generator

  There is a new generator that creates classes that mimic the proposed
  Java `record` functionality that will be available in the future some
  time. This generator also demonstrate how to use the `JavaLexed` class
  in generators that want to modify the Java source on lexical element
  level.

* document generation supports JavaDoc

  You can insert doclets into JavaDoc code without special code inserter
  generator (the special code inserter is part of the library now).


1.2.0 feature release

* Documentation generation snippet support

  There is a whole new module that contains generators that support
  document generation inserting snippets cut off from code into
  documents.

* Generator configuration unified and supported by code generation

* parameter parsing supports " inside string, other escape sequences,
  and numeric and boolean values

* multi-line segments headers are supported in markdown documents

* Java generated code is compared on the lexical level and thus
  reformatted code is not regenerated

1.1.0 feature release

* Fluent API changes:
  * Fluent API can be defined in a single string using a simple syntax definition calling the fluent API building
    fluent API method `syntax()`
  * Wrapper contains only those methods that are actually used in fluent API building
  * Builder methods can also be `private`, `protected` or package private and they do not need to be public. They are
    wrapped anyway.
  * You can explicitly include a method into the fluent API wrapper calling `include()`
  
* Geci core changes:
  * Generators that work on Java classes and are implemented extending the `AbstractGenerator` automatically get
    the configuration from comment if the class is not annotated
  * Any annotation can be used that is named `Geci` not only the predefined in the given library
  * Annotation parameters that are defined and have string value are appended to the configuration. It makes sense
    when the annotation is defined for the using project, as the provided `@Geci` annotation does not have other 
    parameters only `value`
  * File collections can be limited using regular expressions matching file names.
  * Generation throws an error if generators are configured so that they do not work on any source.


1.0.0 initial release