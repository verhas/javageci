# Fluent API Code Generator

Fluent API code generator is a fairly complex generator that can generate fluent API facade in front of an already existing class.

## What is fluent api

You can read the following articles to get to know what fluent api is:

[Fluent Api & Wikipedia](http://en.wikipedia.org/wiki/Fluent_interface)

[FluentInterface from Martin Fowler](http://martinfowler.com/bliki/FluentInterface.html)

[The Java Fluent API Designer Crash Course](http://dzone.com/articles/java-fluent-api-designer-crash)

## Impatient? Do not want to read those articles?

Fluent API in Java is a technique that results readable method call chaining.
Example:

```
    CreateSql.select("column1","column2").from("tableName")
                                       .where("column1 = 'value'")
```

This can be reached implementing the methods `select`, `from` and `where` returning an instance of the the class `CreateSql`.
(Note: `select` is static.)
This is simple and straightforward.
However this is far from real fluent API.
This implementation will not prevent someone write

```
    // WRONG!!!
    CreateSql.select("column1","column2")
                              .from("tableName").from("anotherTable")
```

which is simply wrong.
To prevent this you have to define extra interfaces as depicted in the articles [The Java Fluent API Designer Crash Course](http://dzone.com/articles/java-fluent-api-designer-crash)

This fluent API code generator will generate those extra interfaces automatically based on the definition of the grammar of the fluent API.

## Fluent API Generator

To use the fluent API generator you have to create three code parts. 

1. One is the builder class that has the methods with the appropriate names and arguments.
The methods may or may not be chained in this "builder", it is up to you.
You have to annotate the class using the `Geci()` annotation to ensure that the generator will process this class.
The annotation string should contain the parameter `definedBy` to specify the fluent API grammar.
We will describe this a few lines below.
The class should also have an `editor-fold` segment to hold the generated code.

2. You should write the test code that creates the fluent API code.
This is the same as for any other generator.
Note that you only need one test method even if you have many classes to fluentize.

3. Finally you need a method that defines the grammar of the fluent API.
You need a separate one for each fluentized class as it is not likely that they share the same grammar. 
The name of this method is defined by the `definedBy` parameter of the class annotation.

A good example is the `JavaSource` class that is in the Java::Geci tools module.
It starts with the following lines:

```java
@Geci("fluent definedBy='javax0.geci.buildfluent.TestBuildFluentForSourceBuilder::sourceBuilderGrammar'")
public class JavaSource implements AutoCloseable {
```

As you can see the mnemonic of the generator is `fluent` and the parameter `definedBy` specifies the method in the syntax of a method reference.
This is not really a method reference as it is inside a string, but the syntax follows that of the method references.
(You can also use `#` or a simple dot `.` to separate the name of the class and the name of the method.)

Also note that in this specification you have to specify the class with a fully qualified name.

An alternative possibility to use the `syntax` parameter and provide the syntax of the fluent API directly and not through a builder method.
Note that certain features cannot be described using the syntax parameter.
An example is to make the API `AutoClosable`.

```java
@Geci("fluent syntax='a|b|c d'")
public class JavaSource {
```


The editor fold 

```java
//<editor-fold id="fluent" desc="fluent API interfaces and classes">
//</editor-fold>
```

will hold the generated code after the generator runs.
You can define an `id` for the editor fold in the annotation.
The default is the mnemonic of the generator.
It is usually okay, you are not likely to generate more than one fluent API into one single class.
Most probably they would also collide with each other.

The test code that generates the fluent API is the following:

```java
@Test
public void testSourceBuilderGeneratedApiIsGood() throws Exception {
    if (new Geci().source("../javageci-tools/src/main/java", "./javageci-tools/src/main/java").register(new Fluent()).generate()) {
        Assertions.fail(Geci.FAILED);
    }
```

The framework will try to open the `../tools/src/main/java` directory first and in case it can not be found then it goes on to open the `./tools/src/main/java` directory to discover the source files.
If you use standard maven directory structure you can use the `Source.maven()` static method to specify the directories and to ease the readability of the test.

The code creates a new instance of the generator and starts the generation invoking `generate()`.
In case the generated code differs from the one that was already in the file the return value of `generate()` is `true` and then the test fails: the code was modified, it has to be committed into the repository and compiled and tested again.
No manual code modification is needed.
This is also standard for all the generators.

The method that defines the fluent API is in the method `sourceBuilderGrammar()`:

```java
public static FluentBuilder sourceBuilderGrammar() {
    var source = FluentBuilder.from(JavaSource.class).start("builder").fluentType("Builder").implement("AutoCloseable").exclude("close");
    var statement = source.oneOf("comment", "statement", "write", "write_r", "write_l", "newline", "open");
    var methodStatement = source.oneOf(statement, source.oneOf("returnStatement()", "returnStatement(String,Object[])"));
    var ifStatement = source.one("ifStatement").zeroOrMore(statement).optional(source.one("elseStatement").zeroOrMore(statement));
    var whileStatement = source.one("whileStatement").zeroOrMore(statement);
    var forStatement = source.one("forStatement").zeroOrMore(statement);
    var methodDeclaration = source.one("method").optional("modifiers").optional("returnType").optional("exceptions").oneOf("noArgs", "args");
    var method = source.name("MethodBody").one(methodDeclaration).zeroOrMore(methodStatement);
    var grammar = source.zeroOrMore(source.oneOf(statement, ifStatement, whileStatement, forStatement, method)).one("toString");
    return grammar;
}
```

It is recommended that you place this method in the test class.
There are different reasons for it:

1. It is close to the generating code, it eases maintenance and readability.
1. The Java::Geci core module is used by this code and this module is probably not used in anywhere else in the code.
   Placing this code in the test class the dependency scope for the core module can remain `test` (note that the code generating test already needs this dependency).
1. This method is not needed during production run time, this is a test support code.

The method has to build a `FluentBuilder` object and to do that it uses the fluent API of the `FluentBuilder`.
Thus we have a fluent API to defined our own fluent API.

To create the fluent API grammar you can use the following (fluent) methods.
Whenever an argument is a `String` that identifies a method the name of the method can be used.
If there are more than one methods in the class with the same name then the signature of the method should be used to identify the actual methods.
Other methods can still be referred only by name.
The signature of the method is the name of the method and the argument types between `(` and `)` comma separated.
If the type is a Java JDK class (package starts with `java.`) then the package can be omitted.
For example you can write `String` instead of `java.lang.String`.
In other cases the fully qualified domain name has to be used (dot separated and not `$` even if the type is an inner class).

### `start(String method)`

Define the name of the start method.
The start method is a `public static` method that can be used to instantiate the builder.
When you fluentize a class `MyClass` and call `start("builder")` then you will start a fluent API use with `MyClass.builder()`.
The start method does not have any parameter in the current implementation.

`method` is the name of the start method.

### `implement(String interfaces)`

Define interfaces that all other interfaces in the fluent interface should implement.
This can be typically `AutoCloseable` when some API uses the structure of the try-with-resources command to follow the built structures in the generating Java code.

The parameter `interfaces` is the names of the interfaces to be implemented comma separated.
This string will be inserted into the list of the interfaces that stands after the `extends` or `implements` keyword.

### `autoCloseable()`

This is a complimentary method that is equivalent to call `implement("AutoCloseable")`.

### `fluentType(String type)`

Define the top-level interface name that will start the fluent API.
Other names are generated automatically unless defined by the method `name(String)`.

The parameter `type` is the name of the interface top-level interface.

### `exclude(String method)`

Exclude a method from the fluent interface.
If a method is excluded it can not be used in the definition of the fluent api and it will not be part of the interfaces and the wrapper class.
The caller may exclude more than one method from the fluent API with subsequent calls to `exclude(String)`.
The parameter `method` is the name or signature of the method to be excluded.

Starting with version 1.0.1 there is no need to exclude certain methods.
After this version only those methods get into the interface and into the Wrapper class that are explicitly referenced in the fluent API definition.

### `cloner(String method)`

Define the method that clones the current instance of the class that is fluentized.
Such a method usually creates a new instance and copies all the fields to the new instance so that fluent building can go on from that instance and all previous instances can be used in case they are needed to build something different.

The parameter `method` is the name of the cloner method.
The method should return a new instance of the class and should have no parameters.

### `optional(String method)`

The method may be called zero or one time in the fluent API at the defined point.

The parameter `method` is the name of the method.
For more information see the note in the documentation of the class `FluentBuilder`.

### `optional(FluentBuilder sub)`

The sub expression may be called zero or one times in the fluent API at the defined point.
The parameter `sub` is the fluent api structure used in the expression.

### `oneOrMore(String method)`

The method may be called one or more time in the fluent API at the defined point.
The parameter `method` is the name of the method.
For more information see the note in the documentation of the class `FluentBuilder`.

### `oneOrMore(FluentBuilder sub)`

The sub expression may be called one or more times in the fluent API at the defined point.
The parameter `sub` is the fluent api structure used in the expression.

### `zeroOrMore(String method)`

The method may be called zero or more time in the fluent API at the defined point.
The parameter `method` is the name of the method.
For more information see the note in the documentation of the class `FluentBuilder`.

### `zeroOrMore(FluentBuilder sub)`

The sub expression may be called zero or more times in the fluent API at the defined point.
The parameter `sub` is the fluent api structure used in the expression.


### `oneOf(String... methods)`

The fluent API using code may call one of the methods at this point.
The parameter `methods` is the names of the methods.
For more information see the note in the documentation of the class `FluentBuilder`.

### `oneOf(FluentBuilder... subs)`

The fluent API using code may call one of the sub structures at this point.
The parameter `subs` is the sub structures from which one may be selected by the caller.

### `one(String method)`

The method can be called exactly once at the point.
The parameter `method` is the name of the method.
For more information see the note in the documentation of the class `FluentBuilder`.

### `one(FluentBuilder sub)`

The sub structure can be called exactly once at the point.
The parameter `sub` is substructure.

### `name(String interfaceName)`

The structure at the very point has to use the name as the interface name.
The parameter `interfaceName` is the name of the interface to use at this point of the structure.
Where the name is not defined the fluent api builder generates interface names automatically.

### `syntax(String syntaxDef)`

The syntax can be defined using a complex string in addition to fluent API calls.
The fluent API calls and the call of the method `syntax()` can also be mixed together with some limits.

In the the unit tests there is a syntax test string sample that looks like the following:

```java
"kw(String) ( noParameters | parameters | parameter+ )? regex* usage help executor build"
```

This syntax definition says that the fluent API generatedwhen used has to call the method `kw` first, the one of the methods `noParameters`, `parameters` or `parameter`.
When the last one is used it can be called one or more times, however all these alternative calls can also be just skipped.
After that the method `regex` can be called zero or more times.
After that the `usage`, `help`, `executor` and `build` methods have to be invoked and they are mandatory.

The rules are intuitive and simple.

* A word means a method call.
* Methods that should be called one after the other are written one after the other separated by space.
* Methods are defined the same way as in other calls, (e.g.: as the argument to method `one()`) with the name and with optional signature.
* Something enclosed between '(' and ')' characters is a substructure.
* Alternatives are enclosed between '(' and ')' and the elements are separated using '|'.
* Anything followed by a '?' is optional.
* Anything followed by a '+' is one or more times.
* Anything followed by a '*' is zero or more times.

Call to `syntax()` can be mixed with other calls.
For example the syntax does not provide any means to define interface name like the call to the method `name()`.
If you need that and still want to use the syntax instead of method chain you can use the following expression:

```java
klass.syntax("kw(String) ( noParameters | parameters | parameter+ )? regex* usage help executor")
     .name("SpecialName")
     .syntax("build");
```

In the last call you could just call `one("build")` and it finally would result the same structure.
