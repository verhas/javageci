# Reference Documentation of Java::Geci


## Introduction

Java::Geci is a code generation framework that makes it very easy to
write Java code generating tools. It also

* comes with off the shelf code generators that can replace other
  widely used code generation tools like the one implemented int he
  IDEs or Lombok

* provides services and interfaces to write code generating classes.

The code generation is executed along with the other unit tests. When
a Java::Geci code generator is executed it has access to the source
code but it also has access to the class itself that was compiled
from the source without the generated code or with a previous version
of the generated source. The code generator generates the code it
is designed for and passes the code to the framework.

The framework decides if the generated code is different or not from
the already existing code and writes it back to the source code in
the case the code changed but lets the code intact in case the generated
the code has not changed.

The framework is usually invoked from a three-line unit test that has
the structure:

```java
if( code_was_generated ){
    Assertions.fail("code has changed");
}
```

The method `generate()` which is the final call in the chain to
the code generation returns `true` if any code was changed and
written back to the source code. This will fail the test, but if we
run the test again with the already modified sources then the test
should run fine.

This structure has some constraints that we have to accept 
for the advantages the tool provides:

* Generators should generate exactly the same code if they are executed
  on the same source and classes. This is usually not a strong
  requirement, code generators do not tend to generate random
  source. Some code generators may want to insert timestamps as
  comment in the code: it should not.

* The generated code becomes part of the source and are not compile
  time artifacts. This is usually the case for all code generators that
  generate code into already existing classes. Java::Geci can generate
  separate files but it was designed for inline code generation
  (hence the name).

* The generated code has to be sent to the repository and the manual
  source along with the generated code has to be in a state that
  does not need further code generation. This ensures that the CI
  server in the development can work with the original workflow:
  fetch - compile - test - commit artifacts to the repo. The code generation
  was already done on the developer machine and the code generator
  on the CI only ensures that it was really done (or else the test
  fails).

In the followings, we will describe how to configure and invoke
Java::Geci via its API (no external configuration whatsoever, only
the API invoked from the tests) and after that how to write code
generators.

This documentation is reference documentation. Examples are given in
the tutorials listed on the documentation page

* [Tutorials](TUTORIAL.md)

## Geci invocation API

To use Java::Geci you have to have the libraries on the classpath. If you use Maven then the easiest way is
to define the dependencies in the POM file.

```xml
<dependency>
    <groupId>com.javax0.geci</groupId>
    <artifactId>javageci-annotation</artifactId>
</dependency>
<dependency>
    <groupId>com.javax0.geci</groupId>
    <artifactId>javageci-api</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.javax0.geci</groupId>
    <artifactId>javageci-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.javax0.geci</groupId>
    <artifactId>javageci-engine</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.javax0.geci</groupId>
    <artifactId>javageci-tools</artifactId>
    <scope>test</scope>
</dependency>
```

The structure of the invocation is usually three lines in a unit test:

```java
if( configuration and invocation of the code generators ){
    Assertions.fail("code has changed");
}
```

The `configuration and invocation of the code generators` part of the
code is a chained method call that starts with creating a new `Geci`
object and ends with the call to the method `generate()`. For example
the call:

* Example:

<!-- USE SNIPPET */TestAccessor -->
```java
package javax0.geci.tests.accessor;

import javax0.geci.accessor.Accessor;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestAccessor {

    @Test
    public void testAccessor() throws Exception {
        if (new Geci().source(maven().module("javageci-examples").mainSource()).register(new Accessor()).generate()) {
            Assertions.fail(Geci.FAILED);
        }
    }
}
```

creates the new `javax0.geci.engine.Geci` object and calls the
`source()` and `register()` methods to configure the framework. Finally
invoking `generate()` starts the code generation.

### Geci configuration calls

#### `source()` defines the alternative directories where the source is

There are several overloaded methods named `source()` in the `Geci`
interface. They provide means to define different source sets for the
generators. Note, that in this context the word "source" has multiple
meanings. The generators can read the source files to gather information
but at the same time, it can also write generated code to the sources.

The different `source()` methods usually accept a `String` array as
vararg in the last argument position. This is NOT to specify different
source sets. This is to specify alternative directories where the
source set can be.

> The need for this is because there is no guaranteed current working
> directory (CWD) when the unit tests are started. Many times it
> is not a problem, but in some cases, for example, when you have a multi
> module maven project, you may face different CWDs depending on how you
> start the test. If you start the unit test from the interactive
> environment of the IDE then the CWD will be the root directory of the
> module project. If you start `mvn clean install` in the parent
> project then the CWD is the project root directory of the parent
> project.

The directory or directories for the source set should be the directory
where Java code starts (in case of Java source directory). In other
words, it is the directory where the `com` directory is
corresponding to your `com. ...` package structure. In still another
words this is the `src/main/java` directory in Maven terms.

You should not specify a directory deeper only to limit the source
scanning of the framework, because this will prevent finding the
class that was created from a certain source file. If there are many
packages and sources the generators will ignore them if they
do not have anything to do with.

##### Named Source Sets 

You can also specify a named source set using the `source()` method.
This is needed when the code generator wants to create a new file
and there are multiple source sets defined. For example, there can be a
source set in the directory `src/main/resources` containing resource files
and another `src/main/java` containing Java files. If a generator
processes a resource file and then wants to generate a Java file then
the framework has to create the generated file in the `src/main/java`
directory in the appropriate subdirectory as defined by the package.

Generators that want to create whole new files will specify the source
set where they want to create the new file. If there is no named source set
in the configuration with the name the generator is seeking then
they can not work.

The method `source()` has an overloaded version that accepts the first argument
of the type `Source.Set`. This type is a simple `String` wrapper to ease
readability and help overload. There is a `static` method in the
class `Source.Set` named `set()` that can be imported statically and
used to specify a source set name. Thus you can specify a named source
set in the form `source(set("java"),"src/main/java")`.

##### Maven directories

Since Maven is the number one build tool and also other build tools use
the directory structure standard that was minted by Maven there is some
support to specify the source sets in a simple way for Maven projects.
There is an overloaded version of the method `source()` that accept a
`Source.Maven` object as an argument. Using that you can specify the
directories in a simple way. You can simply write

```java
source(maven().module("myModule").mainSource())
```

to specify the main source directory of the module `myModule`. You can omit the `module(...)`
part of the call in case you have a simple maven project. You can use

* `mainSource()` to have the main source directory as a source set
* `testSource()` to have the test source directory as a source set
* `mainResources()` to have the main resource directory as a source set
* `testResources()` to have the test resource directory as a source set

If you do define none of them, just use `source(maven())`
or `source(maven().module("myModule"))`
then you define all the four source sets in a single call. This call
also defines the set names:

* `"mainSource"` for the main source
* `"mainResources"` for the main resources
* `"testSource"` for the test source
* `"testResources"` for the test resources

It is recommended to use these source set names when you write a generator.

#### Registering generators

After the source sets are defined code generator objects have to be registered so that the framework can
call them, one after the other for all the source codes. To do that

* `register(Generator ...)`

has to be called on the call chain. `Generator` is the interface that all generators implement.
The instantiation of the actual generators is up to the configuration. It is usually just creating
the instance using the `new MyGenerator()` constructor, but in some cases, it may be different
if the generator can be instantiated in different ways.

One call can be used to register multiple generator objects, but it is also possible
to use subsequent calls to `register()`.

#### Generate

The last call after the chain of configuration calls has to be `generate()`. This call will initiate the
code generation and return `true` if there was some new code generated.

## Writing Generator

A generator is a class that implements the interface `javax0.geci.api.Generator`.
This interface defines a single method

```java
 void process(Source source)
```

The `source` object represents a single source file and the object should be used by the generator

* to read the lines from the actual textual source code,
* get access to the compiled class that was  generated from the given source (such class 
  may not exist in case the source code is not a Java source file)
* to get access to writable segments of the source file and to write text into the source
* to get access to totally new source files and to write into those generated source code.

### Accessing the source in the generator

There are many things that you can reach in your generator code through the `source` object passed
as argument to the method `process()` of the generator you write.

The first that comes in front of us is `getAbsoluteFile()` that will return the absolute file name
as a string of the source file that the generator is actually working on. This is rarely needed because
the content of the file is available in other ways. It may, however, be needed if the generator
limits itself to work only on some specific file (e.g.: it ready only files with the extension `.xml`),
or when the file is binary and cannot be accessed line by line.

If the generator needs to read the text in the file then the method `getLines()` should be invoked.
The return value of the method is a list of `String` objects that contain the text of the lines.
If the generator accesses the content of the file then it does not need to deal with the operating system
line ending. The different generators that run in the same process one after the other can also
share the content. There is no need to read the content of the file again and again.

### Getting segments to write code into

The source file needing modification may contain named segments. These are the lines that are between

```
<editor-fold id="segment name">
```

and 

```
</editor-fold>
```

lines. These lines are edited by the programmer signaling the part of the source code where it expects
generated code to be inserted. The generator can access these segments opening a segment in the
source calling the method `open(id)` on the `source` object. The parameter is the string that is
the specified `id="segment name"` on the starting line of the segment. Segment object should be used
to write lines of generated code. The framework will write back the changes at the end of the execution
automatically and also check if the generated code is/was the same as the one that was already in the file.

There is a version of the method `open()` that has no argument. This opens a segment that means the
whole source file. A generator should invoke this only when it generates a new source file and the source
object was acquired via calling `newSource()` on the `source` object. Never invoke the argument-less
`open()` method on the `source` object that was passed to the generator `process()` as an argument. It will
delete the content of the source file that was edited by the programmer.

Opening a segment is a fairly cheap operation and the generator code can open the same segment many times.
The `open()` method will just return the same segment and the code generation can continue from the place
where it was left off after the previous call to `open()`. 

### Segment initialization

In some cases, code generators happen to generate empty code. In this case code logic may just never call
`open(id)` on a segment, as there is nothing to write there. However, the framework will interpret this
that the generator does not want to touch the segment and the old value remains. For example, there is a code
that does something with every field in the class that is `final` and `volatile` (contrived example as it
can never happen). In the segment, there is already some code either edited by the programmer or some
previous miraculous code generation run that found such fields. When the code generator runs again it
does not find any `final volatile` field and thus it wants the segment to be empty.

To tell the framework that the segment is to be modified even if no `open(id)` is invoked for the specific
segment the generator has to call `init(id)` on the source object. This will essentially delete the already
optionally existing content of the segment. 

### Creating, opening new source object

The generator can call `newSource(fileName)` on a `source` object. This will create a new source object that
can be used to read the content of the named source file if it exists or
to write generated code to it and the code will be written into the file named in the argument 
at the end of the execution unless the code was already there and did not change.

Since the new source code, most of the time is generated in the same directory where the other source code
is the `fileName` is relative to the file name of the source the `newSource()` was invoked on.

There is also a version of the method that accepts two arguments: `newSource(Source.Set set, String fileName)`.
This can be used when the generated code has to be in a different source set than the one containing the information
the generator reads. Even in this case, the directories will be relative to the `source` just in the
different source set. For example, there is the file `com/javax0/javageci/Bean.xml` in the source set starting in
directory `src/main/resources/`. It contains some description of the bean the generator has to generate.
There is another source set defined with the name `"mainSource"` in the directory `src/main/java`. Calling
`source.newSource(set("mainSource"),"Bean.java")` will create the file
`src/main/java/com/javax0/javageci/Bean.java`.

The code will only be generated only if the global or a named segment was initialized, opened during code generation.
If the source was only used to read information and no segment was opened then the file will not be touched
by the framework.

When the new content is written back to the file the directories along with all needed parent directories are
automatically created.

### Accessing the class of the source

Most of the time the `source` object refers to a Java source file. Since the code runs during unit test execution
the compiled version of the class is available and can be examined by the generator using reflection. The name
of the class and the package can be deducted from the file name. The suggested way to do this to invoke the methods
provided by the `source` object for the purpose.

* `getKlass()` returns the class that was created by the source during the compilation phase.
  If there is no such class then the return value is `null`.
* `getKlassName()` returns name name of the class. This includes the full package names dot separated.
* `getKlassSimpleName()` returns the simple name of the class file.
* `getPackageName()` returns the name of the package.

There are support methods in the tool module that help with reflection. Before starting to write your code from scratch
consult those methods. They contain significant experience.

For example when a generator wants to
generate code for each field or each method then this is vital that the order of the fields or methods is the
same on different Java versions. There may be different Java build on the developer machine and on the CI server
and the reflection method `getDeclaredFields()` may return the fields in a different order. This causes code
generated different on the CI server from the one generated by the developer and thus the CI build fails with
unit test error. (It really happened.) To avoid that there are methods that collect fields, methods etc in a sorted
definite order in the tool module. 

### Writing into a segment

After you get access to a `Segment` object you can use that object to write into the source code into the
segment. Whatever you write into a segment will replace the old content. Opening a segment many times, however, does
not overwrite the content that the generator was already writing into the segment. For example, a generator creates
a setter and a getter for each field in the class. As the generator iterates through the ordered list of the declared
fields it opens the segment named "setters" for each field. The generated code will be appended each time and
finally replacing the content that was in the file before the code generation.

To write into a segment there are four methods:

* `write(...)` write a line into the code.
* `write_l(...)` write a line into the code and then set the tabstop indented.
* `write_r(...)` unindent the tab stop and then write a line into the code.
* `newline(...)` insert an empty line.

The `write...()` methods accept a `String` format and a variable number of objects as parameters. The format
string will be used in the `String.format()` method. Please read the Java documentation on how to use the formatting.

When the line itself contains newline characters then the indenting will automatically be kept for each line. There
is no need to spit up the generated multi-line string into lines and invoke the `write()` method several times.
You can write multi-line code safely well tabulated using these methods.

The `Segment` also has a `close()` method, that actually does nothing, but `Segment` also implements `AutoCloseable`
thus it can be used in try-with-resource blocks. It may improve code readability.

Note that the methods provided by the implementation of `Segment` are simple and does not mean to be a full blown
code generation tool. There is an experimental class `javax0.geci.tools.JavaSource` that provides more possibilities
to generate code. It was mainly used to create the fluent API code generation and also the class API itself is generated
by itself demonstrating recursive iterative code generation development. If even the functions provided there are
not enough you can use any external library together with Java::Geci.

### Generator Parameters

Generators are free to use any configuration they like, however, there are supported configuration ways.
Generators can be configured on the application, instance and source level. There is support for the source
level configuration. For higher level configuration the tools provided by the Java language and infrastructure
is sufficient.

* Application level configuration can be hard coded into the class as parameter or can be read from
  `properties` files or from other sources. There is no special support for this in Java::Geci. You
  should follow the usual Java conventions in your code. These parameters affect the behavior of the
  application for all the runs in the JVM.
* Instance level configuration can be done via constructor parameters or via setter or other configuration
  methods. This is, again, standard Java practice, nothing specific to Java::Geci. These parameters affect
  the behavior of the application for the instance they were provided.
* Generators read source level configuration from the source. These parameters play for the specific source.
  Although generators read the source and could get parameters from many structures, there is support to get the
  configuration from annotations or from comments. The rest of the section is about the supporting tools that
  help the generators to read these configuration parameters.



--- TODO ---

### Special Generators

When you write a generator you do not need to manually implement the interface `javax0.geci.api.Generator`. The
library contains abstract classes that implement the interface and do some specific task that may be the same for
a variety of generators.

Currently there are three abstract classes that each extend the next on in the line. Their inheritance structure
is the following:

```java
Generator <- AbstractGeneratorEx <- AbstractGenerator <- AbstractDeclaredFieldsGenerator
```

* `AbstractGeneratorEx` can be extended by generators that may throw exception. Note that the signature of
  the method `process()` in the interface `Generator` does not throw any exceptions.
* `AbstractGenerator` is to be extended by generators that work only on Java source files and need the compiled
  class of the source.
* `AbstractDeclaredFieldsGenerator` is for generators that want to generate code for each declared field in a
  class.

#### `AbstractGeneratorEx`

The interface `Generator` defines the method `process()` in a way that it should not throw exception. If there
is an exception during code generation then it has to wrapped into some run-time exception. This will be
propagared to the unit test level and thus the test will fail, as it should.

For the wrapping Java::Geci provides the exception class `javax0.geci.api.GeciException`.

`AbstractGeneratorEx` implements the method `process()` invoking the abstract method `processEx()` it defines
wrapping the call into a try-catch block. If there is any exception thron from `processEx()` then it is wrapped
into a `GeciException` and thrown.

The abstract method generators must implement in this case is

```java
public abstract void processEx(Source source) throws Exception;
```

Note that the method may throw `Exception` and the implemented `process()` catches only `Exception` and not
any `Throwable`.

#### `AbstractGenerator`

This abstract generator implements the method `processEx()` and calculates the class of the source and
also collects the parameters defined in a `@Geci` annotation. Extending classes should implement the
abstract method

```java
public abstract void process(Source source, Class<?> klass, CompoundParams global)throws Exception;
``` 

This case the method is named `process()` as it has different arguments than the one in the interface
and is an overloading of the interface method. The arguments are the

* `source` is the source object
* `klass` is the class of the source
* `global` contains the parameters that are defined in the `@Geci` annotation on the class level. 

#### `AbstractDeclaredFieldsGenerator`

This abstract generator does everything as `AbstractGenerator` essentially extending that class and
iterates through the fields of the class. It defines one abstract method that generators extending
this class have to implement:

```java
public abstract void processField(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception;
```

This method is invoked for every field. The parameter `params` is the composition of the parameters
defined on the class level and on the field in `@Geci` annotations. If a parameter is defined on the field
then it prevails, otherwise the one on the class is used.

The class also defines two do-nothing methods that can optionally be overridden by the extending class. These
are:

```java
public void preprocess(Source source, Class<?> klass, CompoundParams global) throws Exception {
    }

public void postprocess(Source source, Class<?> klass, CompoundParams global) throws Exception {
    }
```

As the name suggest `preprocess()` is invoked before the fields iteration starts and `postprocess()`
is invoked after that. 