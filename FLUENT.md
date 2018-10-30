# Fluent API Code Generator

Fluent API code generator is  a fairly complex generator that can generate fluent API facade in front of an already
existing class.

## What is fluent api

You can read the following articles to get to know what fluent api is:

[Fluent Api & Wikipedia](http://en.wikipedia.org/wiki/Fluent_interface)

[FluentInterface from Martin Fowler](http://martinfowler.com/bliki/FluentInterface.html)

[The Java Fluent API Designer Crash Course](http://java.dzone.com/articles/java-fluent-api-designer-crash)

## Impatient? Do not want to read those articles?

Fluent API in Java is a technique that results readable method call chaining. Example:


    CreateSql.select("column1","column2").from("tableName").where("column1 = 'value'")

This can be reached implementing the methods `select`, `from` and `where` returning an instance of the the 
class `CreateSql`. (Note: `select` is static.) This is simple and straightforward. However this is far from 
real fluent API. This implementation will not prevent someone write

    // WRONG!!!
    CreateSql.select("column1","column2").from("tableName").from("anotherTable")    

which is simply wrong. To prevent this you have to define extra interfaces as depicted in the articles 
[The Java Fluent API Designer Crash Course](http://java.dzone.com/articles/java-fluent-api-designer-crash) 

This fluent API code generator will generate those extra interfaces automatically based on the definition of the
grammar of the fluent API.

## Fluent API Generator

To use the fluent API generator you have to create three code parts. 

1. One is the builder class that has the methods
with the appropriate names and arguments. The methods may or may not be chained in this "builder", it is up to you.
You have to annotate the class using the `Geci()` annotation to ensure that the generator will process this class.
The annotation string should contain the parameter `definedBy` to specify the fluent API grammar. We will describe
this a few lines below. The class should also have an `editor-fold` segment to hold the generated code.

2. You should write the test code that creates the fluent API code. This is the same as for any other generator. Note
that you only need one test method even if you have many classes to fluentize.

3. Finally you need a method that defines the grammar of the fluent API. You need a separate one for each fluentized
class as it is not likely that they share the same grammar. The name of this method is defined by the `definedBy`
parameter of the class annotation.

A good example is the `JavaSource` class that is in the Java::Geci tools module. It starts with the following lines:
```java
@Geci("fluent definedBy='javax0.geci.buildfluent.BuildFluentForSourceBuilder::sourceBuilderGrammar'")
public class JavaSource implements AutoCloseable {
```

As you can see the mnemonic of the generator is `fluent` and the parameter `definedBy` specifies the method in the
syntax of a method reference. This is not really a method reference as it is inside a string, but the syntax follows
that of the method references. Also note that in this specification you have to specify the class with a fully
qualified name.

The editor fold 

```java
//<editor-fold id="fluent" desc="fluent API interfaces and classes">
//</editor-fold>
```

will hold the generated code after the generator runs. You can define an `id` for the editor fold in the annotation.
The default is the mnemonic of the generator. It is usually okay, you are not likely to generate more than one
fluent API into one single class. Most probably they would also collide with each other.

The test code that generates the fluent API is the following:

```java
@Test
public void testSourceBuilderGeneratedApiIsGood() throws Exception {
    if (new Geci().source("../tools/src/main/java", "./tools/src/main/java").register(new Fluent()).generate()) {
        Assertions.fail(Geci.FAILED);
    }
```

The framework will try to open the `../tools/src/main/java` directory first and in case it can not be found then
it goes on to open the `./tools/src/main/java` directory to discover the source files. If you use standard maven
directory structure you can use the `Source.maven()` static method to specify the directories and to ease the
readability of the test.

The code creates a new instance of the generator and starts the generation invoking `generate()`. In case the generated
code differs from the one that was already in the file the return value of `generate()` is `true` and then the test
fails: the code was modified, it has to be committed into the repository and compiled and tested again. No manual 
code modification is needed. This is also standard for all the generators.

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

It is recommended that you place this method in the test class. There are different reasons for it:

1. It is close to the generating code, it eases maintenance and readability.
1. The Java::Geci core module is used by this code and this module is probably not used in anywhere else in the code.
   Placing this code in the test class the dependency scope for the core module can remain `test` (note that the code
   generating test already needs this dependency).
1. This method is not needed during production run time, this is a test support code.

The method has to build a `FluentBuilder` object and to do that it uses the fluent API of the `FluentBuilder`. Thus
we have a fluent API to defined our own fluent API.   