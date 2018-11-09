# Reference Documentation of Java::Geci


## Introduction

Java::Geci is a code generation framework that makes it very easy to
write Java code generating tools. It also

* comes with off the shelf code generators that can replace other
  widely used code generation tools like the one implemented int he
  IDEs or Lombok

* provides services and interfaces to write code genrating classes.

The code generation is executed along with the other unit tests. When
a Java::Geci code generator is executed it has access to the source
code but it also has access to the class itself that was compiled
from the source without the generated code or with a previous version
of the generated source. The code generator generates the code it
is designed for and passes the code to the framework.

The framework decides if the generated code is different or not from
the already existing code and writes it back to the source code in
case the code changed but lets the code intact in case the generated
code has not changed.

The framework is usually invoked from a three line unit tests that has
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

This structure has some constraints that we have to accept as a trade
deal for the advantages the tool provides:

* Generators should generate exactly the same code if they are executed
  on the same source and classes. This is usually not a strong
  requirement, code generators do not tend to generate random
  source. Some code generators may want to insert time stamps as
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
  fetch - compile - test - commit artifacts to repo. The code generation
  was already done on the developer machine and the code generator
  on the CI only ensures that it was really done (or else the test
  fails).

In the followings we will describe how to configure and invoke
Java::Geci via its API (no external configuration whatsoever, only
the API invoked from the tests) and after that how to write code
generators.

This documentation is reference documentation. Examples are given in
the tutorials listed on the documentaiton page

* [Tutorials](TUTORIAL.md)

## Geci invocation API

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
generators. Not that in this context the word "source" has multiple
meanings. The generators can read the source files to gather information
but the same time it can also write generated code to the sources.

The different `source()` methods usually accept a `String` array as
vararg in the last argument position. This is NOT to specify different
source sets. This is to specify different possible directories where the
source set could be.

The need for this is because there is no guaranteed current working
directory (CWD) when the unit tests are started. Many times it
is not a problem, but in same cases, for example when you have a multi
module maven project, you may face different CWDs depending on how you
start the test. If you start the unit test from the interactive
environment of the IDE then the CWD will be the root directory of the
module project. If you start `mvn clean install` in the parent
project then the CWD is the project root directory of the parent
project.

The directory or directories for the source set should be the directory
where Java code starts in case of Java source directory. In other
words it is the directory where you the `com` directory is that
corresponds to your `com. ...` package structure. In still another
words this is the `src/main/java` directory in Maven terms.

You should not try to specify a directory deeper to limit the source
scanning of the framework, because this will prevent finding the
class that was created from a certain source file. If there are many
packages and sources the generators will ignore them anyway if they
do not have anything to do there.


## Writing Generator

//DRAFT PART


This eases the use of these tests in different environments. You do not want to write here
absolute path names to the source directory, but the current working directory may be different depending on how you
start the tests. When you start the test in a command line maven build then the current working directory is
the project root. If you start the test from a multi-module maven build  executing the build of a single module
then the project root of the single module is the current working directory. In the example above Geci will
try find the sources first in the directory `src/main/java` and then if it cannot then it will look for them
in `tests/src/main/java`.

Also note that here you have to specify the root directory of the Java sources because
later the directory names are used to calculate the class name and the code generator will not find
`java.com.mydomain.MyClass` class file even if you have only a single `java` directory under `main`.


    