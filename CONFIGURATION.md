# Configuring Generators

Generators are classes that implement the `Generator` interface, thus
their configuration is very much depends on how they are implemented.
However there are guidelines for code generator development that also
includes configuration management for code generators. This document
describes how to develop a code generator that follows these guidelines.

The advantage following these guidelines are twofold:

1. There are tools readily available for code genrators to handle
    configuration data. Using these tools the code generator code can
    focus on what the core functionality of the code generator is and
    does not need to have excessive code handling configuration.

2. Developers who use the code generator do not need to learn the
    specific configuration handlign of the actual code generator. They
    understand the configuration keys and their meaning and the coding,
    api to set those values are the same for all code generators.

From now on in this document when talking about a code generator we
assume that the actual implementation fully follow these guidelines.
    
## Configuration scopes

Code generators can be configured with many scopes. The smaller scope
configuration usually overrides the larger scope configuration. For
example a configuration value can be set

* default values are coded in the generator code by the programmer and
    they have a scope for the whole lifetime of the specific release of
    the code generator.

* for the generator object whole lifecycle, which means that the
    generator will use the set value for all the source files it
    processess, unless there is some value that the source code
    annotations and other configurations override. This level is managed
    by using the builder pattern when the generator is instantiated,
    usually in an expression that is the argument to the Java::Geci
    `register()` method.

* for the source level that will control how the execution of code
    generation for one specific source file. Such a value is usually
    configured using the annotation the class, and/or in the arguments
    of the `editor-fold` segment or in a comment that looks like the
    `@Geci` annotation.
    
* for the field, method or other managed member level. Such a valueis
    configured using the annotation on the specific member.
    
## Configuration support

Java::Geci provides support for configuration management on the
generator object life style scope and on the class and member scope.

### Generator Configuration Builder

Generators define an inner class

```java
private static class Config {
 
   ...private fields defined with default values assigned to them...

}
```
 
Note that the name has to be `Config` in other to use the code
generation support. (See more about it later.)

Generators also have a `private`, preferably `final` field

```java
private final Config config = new Config();
```

Generators also define a method named `builder()` that returns a builder
object, which is a (non-static) inner class of the generator class. This
builder class has a method for each of the fields declared in the class
`Config`.

For example there is a code generator in the `core` package named
`ConfigBuilder` and it has the following code

```java
    private static class Config {
        private String aggregatorMethod = "add";
        private String buildMethod = "build";
        private String builderFactoryMethod = "builder";
        private String builderName = "Builder";
        private String filter = "private & !static & !final";
    }
```

then the `Builder` class will be 

```java
public class Builder {
        public Builder aggregatorMethod(String aggregatorMethod) {
            config.aggregatorMethod = aggregatorMethod;
            return this;
        }

        public Builder buildMethod(String buildMethod) {
            config.buildMethod = buildMethod;
            return this;
        }

        public Builder builderFactoryMethod(String builderFactoryMethod) {
            config.builderFactoryMethod = builderFactoryMethod;
            return this;
        }

        public Builder builderName(String builderName) {
            config.builderName = builderName;
            return this;
        }

        public Builder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public ConfigBuilder build() {
            return ConfigBuilder.this;
        }
    }
```

Each filed named `xyz` has a corresponding method with the same name
that sets the value of the field. The method `build()` returns the
configured generator object.

Following this structure the generator can be instantitated using the

```java
ConfigBuilder.builder().filter("sdddd"). ... .build()
```

setting all the configuration parameters that can be used by the
generator. When the generator is attending to a source file reading it
and then generating code it also reads the configuration from the
`editor-fold` and from the annotations. A well designed generator will
read and interpret the configuration key `xyz` if that appears in the
`Config` class and is `String` type.

### Source configuration

The `String` configuration values that are defined in the `Config` class
can be overridden in the source code. The scope of these values will be
the code generation of the actual sorce file and they have no effect on
the code generation on the next source code processing.

> Note that generally a `Generator` can work on any source file, like on
> XML, JSON or even on binary files and generate usually Java code. If
> the generator works on anything else but Java source code then it is
> totally up to the generator implemenation if it reads configuration
> values if any from the source file it uses to work on. From now on we
> assume that the generator works on Java source code and the generator
> class is directly or through other abstract classes extends the
> `AbstractJavaGenerator` defined in the `tools` module.

When the generator starts processing a Java source file it tries to read
the Geci annotation of the class. An annotation is a Geci annotation if

* the name of the annotation is `Geci`
* the annotation interface is annotated using a Geci annotation. Note
    that this definition is recursive and should be interpreted
    non-circular in the meaning that somewhere in the chain there has to
    be an annotation that is named `Geci`.

Every Geci annotation has a mnemonic that defines which generator it is
configuring. This mnemonic should present in the string of the `value()`
parameter of the annotation. In this case this is the first word in the
string value separated by one or more spaces from the parameters. For
example the annotation

```java
@Geci("accessor filter='private | protected'")
```

has the mnemonic `accessor` and the parameters ..., it has only one, so
the parameter is `filter`.

When a Geci annotation is not named `Geci` then the name of the
annotation can also be used to identify the generator it is configuring.
In that case the name of the annotation can be the mnemonic of the
generator. The first character of the annotation is lower cased in this
case before it is used as the mnemonic of the generator.

There can be several `@Geci` annotations on a class and the generator
takes only the one into account, the one that specifies the mnemonic of
the generator.