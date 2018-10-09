# Reference Documentation of Java::Geci


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


    