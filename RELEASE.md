# Release History

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