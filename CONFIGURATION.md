# Configuring Generators

Generators are classes that implement the `Generator` interface, thus
their configuration is very much depends on how they are implemented.
Their implementation is out of the control of the Java::Geci library.
After all this is the major goal of the framework: anyone can develop a
generator and it is up to them how their code can be configured.

Having said that, however, there are guidelines for code generator
development that also include configuration management for code
generators. This document describes how to develop a code generator that
follows these guidelines and also how a generator following those
guidelines can be configured.

The advantage following these guidelines are twofold:

1. There are tools readily available for code generators to handle
    configuration data. Using these tools the code generator code can
    focus on what the core functionality of the code generator is and
    does not need to have excessive configuration handling code.

2. Developers who use the code generator do not need to learn the
    specific configuration handling of the actual code generator. They
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
  the code generator running different times on many different machines
  at different geo locations. (Or in space.)

* for the generator object whole lifecycle, which means that the
  generator will use the set value for all the source files it
  processes, unless there is some value that the source code
  annotations and other configurations override. This level is managed
  by using the builder pattern when the generator is instantiated,
  usually in an expression that is the argument to the Java::Geci
  `register()` method.

* for the source level that will control how the execution of code
  generation for one specific source file. Such a value is usually
  configured using the annotation the class, and/or in the arguments of
  the `editor-fold` segment or in a comment that looks like the `@Geci`
  annotation.
    
* for the field, method or other managed member level. Such a value is
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
 
Note that the name has to be `Config` in order to use the code
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

Following this structure the generator can be instantiated using the

```java
ConfigBuilder.builder().filter("(private|protected) & !static & !final"). ... .build()
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
XML, JSON or even on binary files and generate usually Java code. If
the generator works on anything else but Java source code then it is
totally up to the generator implemenation if it reads configuration
values if any from the source file it uses to work on. From now on we
assume that the generator works on Java source code and the generator
class is directly or through other abstract classes extends the
`AbstractJavaGenerator` defined in the `tools` module.

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

has the mnemonic `accessor` and the parameter, it has only one, is
`filter`.

When a Geci annotation is not named `Geci` then the name of the
annotation can also be used to identify the generator it is configuring.
In that case the name of the annotation can be the mnemonic of the
generator. The first character of the annotation is lower cased in this
case before it is used as the mnemonic of the generator.

The example above can be converted to use an annotation named `Accessor`
if one exists and is annotated using a Geci annotation:

```java
@Accessor("filter='private | protected'")
```

There can be several `@Geci` annotations on a class and the generator
takes only the one into account, the one that specifies the mnemonic of
the generator. The other annotations are ignored.

If there is no Geci annotation on a class then the generator reads the
source code and tries to find a line that is a comment line (starting
with `//` characters) and contains something that is syntactically is a
Geci annotation. In this case you can ONLY use the name `Geci` as an
annotation look like name because the code is scanned as a series of
lines and not via reflection. The code will treat any line that is a
commented `@Geci` annotation if it is followed by a line that looks like
the start of the class. If there are more than one such comments in
front of a class then only the one will be taken into account that has
the mnemonic at the start of the string.

Also note that you cannot use `@Geci(value="...")` format. The line
scanning expects only `@Geci("...")` format in the comment. In general
the use of the annotation in a comment is a last resort feature and it
is recommended to use normal annotations instead.

When the generator has collected the parameters from the annotation on
the class or from the the comment that looks like an annotation (and
never from both) then it finds a line that looks like

```java
//<editor-fold id="mnemonic" ...>
```

and merges the parameters from this line into the already collected
parameters. It is possible not to annotate a class at all and have the
generator triggered to generate code for the class if the source code
contains the line as above.

> Note that the parameters in the annotation (commented or real
annotation) use single apostrophe (`'`) to enclose the values of the
parameters when they are specified inside the `value` string. Normal
annotation arguments and `editor-fold` parameters use double quotes
(`"`). Escaping single apostrophe in annotation value and escaping
double quotes in `editor-fold` parameters is not possible. This way a
parameter defined in the annotation `value` cannot contain the
apostrophe character and a parameter defined in the `editor-fold` cannot
contain the double quote character. Regular annotation parameters are
interpreted by the Java compiler and thus they can contain any
character.

### Parameter checking

Configuration parameter names can be mistyped. When using the builder
for the generator object life-time scope the compiler will not allow the
programmer to enter any wrong configuration name. When using annotation
parameters (and not encoding the parameter into the `value` string) then
again, the compiler will warn the programmer when there is a typo in the
name of a parameter.

When the parameter is defined in a comment or in the `value` string or
in the `editor-fold` segment the compiler has no means to detect the
spelling error. To warn the programmer in this case the generators check
that all the parameters defined in the source code are expected by the
generator and in case there is a configuration value that the generator
does not understand then they will throw a `GeciException`.

The actual check is done by the abstract class `AbstractJavaGenerator`.
The concrete implementation has to provide a method `implementedKeys()`
overriding the one implemented in the abstract class. This method should
return a set of strings containing all the configuration parameters the
generator can handle. (Note that the set should also include the
parameter name `id`). The default implementation returns `null` and in
this case there is no check. This is not a good practice.

The checking ignores the parameter `desc` because this parameter can be
used in the `editor-fold` line to specify a string that the IDE displays
when the fold is closed. You can use `desc` freely, the generators will
ignore it. (Unless one generator explicitly uses that as parameter. Not
recommended.)

## Using config builder

Writing the generator code to properly handle the configuration
parameters will result a lot of boiler plate code even when using the
library support. You need the builder class, the method
`implementedKeys()` and some other utility methods. All these can be
generated automatically knowing the names of the configuration
parameters.

The generator `ConfigBuilder` does this code generation. When developing
a code generator the developer has to create the `private static class
Config` class with all the fields that can be configured and insert an

```java
//<editor-fold id="configBuilder">
//</editor-fold>
```

segment into the code and run the `configBuilder` from some tests:

```java
@Test
void buildGenerators() throws Exception {
    Assertions.assertFalse(
        new Geci().source("...").register(ConfigBuilder.builder().build()).generate(),
        Geci.FAILED
    );
}
```

The generator will create

* the `config` field, 
* the `builder()` method, which is the fatory method for the builder
  class
* the builder class itself
* the method `implementedKeys()` returning all coonfigurable keys
* and a `private Config localConfig(CompoundParams params)` method.

We have already discussed the first four items in the above list, but
we have not discussed the method `localConfig()`. This method is to help
the development of the generator in a way that is coherent with the
guidelines laid out here.

### `localConfig`

The method `localConfig(CompoundParams params)` creates a new instance
of the `Config` class and fills it with the parameters from the instance
referenced by the field `config` and the parameters specified in the
argument variable `params`. If a configuration parameter is defined in
the `params` parameter set then that is used, otherwise the builder
configured or default value from `config` is used.

When a generator needs some paramater it is a good practice to pass the
parameters read by the framework to this method and use the returned
value to get the actual value of the parameter.

When the generator uses configuration from the class level as well as
field or method (or other member) level then the combined parameters has
to be passed as `params`. The hierarchy and configuration inheritance is
handled automatically by this structure and there is no need to manually
program the decision what parameter to use at a certain point in the
code generator.

Note that this method will copy the non string values from the `config`
object but will not touch the `final` fields (because that is not
possible, and they are there in case the generator class developer wants
to store some contant values there).
