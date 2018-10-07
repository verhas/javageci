# Java::Geci

Java Generate Code Inline

Javageci is a framework to generate Java code. Code generation programs implemented using Java::Geci can be executed
to generate new source code, modify existing Java source files. This way the programmer can use meta programming to
express code in a shorter and more expressive way than it would be possible in pure Java.

The framework discovers the files that need generated code, provides easy to use API to generate code and
takes care to write the generated code into the source code. The code generating program should focus on the
actual code structure it wants to generate.

## When do you need Java::Geci?

There are several occasions when we need generated code. The simplest such sscenarios are also supported by the
IDEs (Eclipse, NetBeans, IntelliJ). They can create setters, getters, constructors, `equals()` and `hashCode()`
methods in different ways. There are two major problems with that solution. One is that the code generation
is manual, and in case the developer forgets to regenerate the code after an influencing change the code becomes
outdated. The other problem is that the code generation possibilities are not extendable. There is a limited set
of code that the tools can generate and the developer cannot easily extend these possibilities.

Java::Geci eliminates these two problems. It has an execution mode to check if all code generation is up-to-date and
this can be used as a unit test. If the developer forgot to update some of the generated code after the program
effecting the generated code, the test will fail. (As a matter of fact the test also updates the generated code,
you only need to start the build phase again.)

Java::Geci also has an extremely simple API supporting code generators so it is extremely simple to create new
code generators that are project specific. You do not even need to package your code generator classes. Just put
them into some of the test packages and execute Java::Geci during the test phase of the build process.

Java::Geci already includes several readily available code generators. These are packaged with the core package
and can generate

* setter and getter
* delegation methods
* others will be under development   
    