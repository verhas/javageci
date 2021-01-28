<a href="https://travis-ci.com/verhas/javageci"><img src="https://api.travis-ci.org/verhas/javageci.svg?branch=master" border="0"/></a>

# Java::Geci

Java Generate Code Inline

<img src="images/logo.svg" width="100px"/>

Read "[how to contribute](CONTRIBUTE.md)".

Java::Geci is a library for generating Java code.
Code generation programs using Java::Geci can be executed to generate new source code or to modify existing Java source files.
This way the programmer can use metaprogramming to express code in a shorter and more expressive way than it would be possible in pure Java.

The framework:
 - discovers the files that need generated code
 - provides easy to use API to generate code
 - writes the generated code into the source code
 
The code generating program should focus on the actual code structure it wants to generate.

## When do you need Java::Geci?

There are several occasions when we need generated code.
IDEs (Eclipse, NetBeans, IntelliJ) support the simplest of such scenarios.
They can create setters, getters, constructors, `equals()`and `hashCode()` methods in different ways.
There are two major problems with that solution.

* One is that the code generation happens manually.
  If the developer forgets to re-generate the code after an influencing, relevant change, the generated code becomes outdated.

* The other problem is that the code generation possibilities are not extendable.
  There is only a very limited set of code that the tools can generate, and the developer cannot easily extend these. 

Java::Geci eliminates these two problems.
It has an execution mode to check if all code generation is up-to-date and this can be used as a unit test.
If the developer forgot to update the generated code after the program effecting the generated code changed, the test will fail.
As a matter of fact the test also updates the generated code, you only need to start the build phase again.

Java::Geci also has a straightforward API supporting code generators, so it is simple to create new code generators that are project specific.
You do not even need to package your code generator classes.
Just put them into a test package and execute Java::Geci during the test phase of the build process.

Java::Geci already includes several readily available code generators.
These are packaged with the core package and can generate:

* fluent API classes and interfaces
* internal builders into the class
* repetitive code iteratively
* record classes (that are Java 8 compatible and can easily be replaced with Java 14 records later)
* code that converts an object to a `Map` and back  
* delegation methods (under development)
* setter and getter (just as an example, there are so many setter/getter generators...)

## How to use Java::Geci

Include the Java::Geci modules that you want to use in your project.
To do that with maven use the following dependencies:

```xml
<dependency>
    <!-- This is optional, you can use your own annotations or comment config -->
    <groupId>com.javax0.geci</groupId>
    <artifactId>javageci-annotation</artifactId>
    <version>1.6.2-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.javax0.geci</groupId>
    <artifactId>javageci-engine</artifactId>
    <scope>test</scope>
    <version>1.6.2-SNAPSHOT</version>
</dependency>
```

This documentation contains the latest development version.
For release versions you can use see the latest non-SNAPSHOT version in the [release history documentation](RELEASE.md).

There are other modules, but you do not need to declare dependency on them as the engine module has transitive dependency and thus maven automatically will use them.

> ❗ Note that this does not include out-of-the box generators.
> Check the individual documentations to see what additional dependencies you might need for those.

Since Java::Geci works during test execution the dependencies have test scope.
The exception is the annotation module.
This module defines an annotation that can be used to configure the code generation during test time (using reflection).
Because of that, the retention of this module is run-time.
Although the annotation is not used in production, but it remains in the byte code and thus is has to be the default maven scope.

If for any reason production dependency must not include Java::Geci you can configure the generators using comments, or you can use your own annotation interfaces that you can define inside your project.
Simply copy the `Geci.java` and `Gecis.java` to your project into any of your packages and Java::Geci will recognize that they are to configure a generator.

If you have a look at the test code `TestAccessor.java` in the test module you can see the following sample code:

<!-- snip TestAccessor -->
```java
    @Test
    void testAccessor() throws Exception {
        Geci geci;
        Assertions.assertFalse(
                (geci = new Geci()).source(maven().module("javageci-examples").mainSource())
                        .register(Accessor.builder().build())
                        .generate(),
                geci.failed());
    }
```

The test runs during the build process, and it generates code, whenever that is needed.
The return value answers the question: _"Was code generated?"_.
`true` if new code was generated or `false` if everything was up-to-date.
The `Assertions.assertFalse` checks this return value and if Java::Geci generated new code, your test (and your build) fails.
In this case, you just have to restart your build, Java::Geci will recognize that the code it could generate is already there, and the test passes.

For further information see the following content:

* [Tutorials](TUTORIAL.md)
* [Reference documentation](REFERENCE.adoc)
* [Frequently Asked Questions](FAQ.md)
* [Explanation of the name and the logo](NAME.md)
* [How to guides](HOWTO.md)
* [Explanations](EXPLANATION.md)
* [Module Structure of Java::Geci](MODULES.md)
* [Configuring Generators](CONFIGURATION.md)
* [Logging support for generators](LOGGING.md)
* [Generators provided with Java::Geci out of the box](GENERATORS.md)
