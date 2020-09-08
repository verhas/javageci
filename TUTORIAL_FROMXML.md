# A Code Generator creating new Class code from XML using Java:Geci

In this tutorial we will create a code generator that will read the XML file `/src/test/resources/javax0/geci/tutorials/beangenerator/SampleBean.xml` and will generate the Java source file `/src/test/java/javax0/geci/tutorials/beangenerator/SampleBean.java`.
Note that the XML file and the generated Java file are in different directory structures (a.k.a. source sets). 

## Test Code

To invoke the generator we will use the following test:
<!-- snip testBeanGenerator trim="do"-->
```java
@Test
public void testBeanGenerator() throws Exception {
    if (new Geci()
        .source(hasTheFile("javax0/geci/tutorials/beangenerator/SampleBean.xml"),"./src/test/resources", "./javageci-examples/src/test/resources")
        .source(set("java"),"./src/test/java", "./javageci-examples/src/test/java")
        .register(new BeanGenerator()).generate()) {
        Assertions.fail("Code was changed during test phase.");
    }
}
```

This test uses two different methods to define source sets.
One is `source(Predicate, String ...)`, the other one is `source(Source.Set, String ...)`.
The first one defines a directory without naming the set.
(The `hasTheFile()` call we discuss a few lines below.)
The second one defines the sources where the Java files are and should be generated and assigns the set name `"java"`˙.
(Note that the several directory names in the argument list are alternatives to make the test executable from different current working directories.)
Later we will see that when the generator calls the method `source.newSource(set("java"), file)` it will refer to the set to tell the framework that it needs a new source that is in a different source set named `"java"`.

The call `hasTheFile("javax0/geci/tutorials/beangenerator/SampleBean.xml")` instructs Geci to consider only directories that contain a `SampleBean.xml` file in the subdirectory `javax0/geci/tutorials/beangenerator`.
This extra check helps locate the right directory when one of the wrong directories in the list may actually exist.
You can also check for the existence of multiple files or you can provide alternative files so that one of them should exist in the directory.

## Generator

To create a new Java class that does not exist yet needs the use of special part of the API.
In this case there is no source code that we
could just extend writing some of the `editor-fold` segments. Because of
that the generator needs to extend the class `AbstractGeneratorEx`
abstract class.

<!-- snip BeanGenerator_head snippet="epsilon" 
                             append="snippets='BeanGenerator_head.*'"
                             regex="kill='import' kill='^$'"-->
```java
package javax0.geci.tutorials.beangenerator;
public class BeanGenerator extends AbstractGeneratorEx {
}
```

This abstract class that we extend simply implements the `Generator` interface and the implementation of the `process()` method calls an abstract method `processEx()` and in case there is any exception thrown by that it is encapsulated to a special `GeciException`.
That was out `processEx()` method does not need to care about exceptions that it can not handle.

The method `processEx()` is invoked for each source file that we configure in the test invocation.
We only care about the XML files that have `.xml` extension.
A real life generator would probably read into the XML file to check that it is really a description of some code that the generator has to create, but for this tutorial we assume that all XML files that are in the configured directories are bean definitions.

<!-- snip BeanGenerator_main1 snippet="epsilon" append="snippets='BeanGenerator_main1.*'"-->
```java
    @Override
    public void processEx(Source source) throws Exception {
        if (source.getAbsoluteFile().endsWith(".xml")) {
//          ...
        }
    }
```
To know that the file is an XML file we check the absolute file name of the source and look if it ends with `.xml`.

The next thing we have to do is that we prepare the parameters for the new class we want to create.
The name of the class will be the same as the name of the XML file.
This is calculated for us by the `source` object.
The method `getKlassSimpleName()` calculates the simple name of the class based on the name of the source file.
Simply this is the name of the file without the file extension.
The method `getPackageName()` returns the name of the package calculated from the directory structure.
The `target` is a source we will write.
In this case we create a new source that we will write from the start to the end, and this source will be in a different directory from the one where the XML is.

To get a source object to a file that will be in a different directory the other source directory has to be configured in the test that invokes the generator.
To identify the different directories the class `Source.Set` is used.

The new file will be created in the same "relative" directory as the actual source (the XML file) except in a different top directory.
 
<!-- snip BeanGenerator_main2 trim="do"-->
```java
final var newKlass = source.getKlassSimpleName();
final var pckage = source.getPackageName();
final var target = source.newSource(set("java"), newKlass + ".java");
final var doc = getDocument(source);
```

In this code example when we The `source` represents the `/src/test/resources/javax0/geci/tutorials/beangenerator/SampleBean.xml` file.
This file is in the source set that starts at `/src/test/resources/`, has a relative path `javax0/geci/tutorials/beangenerator/` and has the name `SampleBean.xml`.
When we ask the source to create a new `Source` object in the set `set("java")` then it will create it in the source set that starts at `src/test/java/` as this source set was named `"java"`, in the directory `javax0/geci/tutorials/beangenerator/` in the source set and the name `SampleBean.java`.
When this source is saved at the end of the code generations the directory will be created if it does not exist.

The `getDocument()` method is a simple `static private` method that parses the XML from the source.
The method `cap()` capitalizes the first character of a string:

<!-- snip BeanGenerator_aux-->
```java
    private Document getDocument(Source source) throws ParserConfigurationException, SAXException, IOException {
        final var dbFactory = DocumentBuilderFactory.newInstance();
        final var dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(new InputSource(new StringReader(source.toString())));
    }
```

When we have the `target` source we need access the global segment of the source.
A `Source` object cannot be written directly, but a source can have several segments that can be written.
We have seen example in the [The simplest Code Generator...](TUTORIAL_SIMPLE.md) where we inserted code into `editor-fold` segments.
This case we want to write the whole source and thus we need a segment that starts from the very start of the file to the very end and writing into it will overwrite the whole file.
This is the global segment of the source and can be accessed calling the `open()` method without argument.
This is what we do in the `try-with-resources` command.
After this we simply analyze the data in the XML and based on this we write the class we wanted to generate.

<!-- snip BeanGenerator_main3 trim="do"-->
```java
try (final var segment = target.open()) {
    segment.write("package " + pckage + ";");
    segment.write_r("public class " + newKlass + " {");
    var fields = doc.getElementsByTagName("field");
    for (var index = 0; index < fields.getLength(); index++) {
        var field = fields.item(index);
        var attributes = field.getAttributes();
        String name = attributes.getNamedItem("name").getNodeValue();
        String type = attributes.getNamedItem("type").getNodeValue();
        segment.write("private " + type + " " + name + ";");

        segment.write_r("public " + type + " get" + ucase(name) + "() {");
        segment.write("return " + name + ";");
        segment.write_l("}");

        segment.write_r("public void set" + ucase(name) + "(" + type + " " + name + ") {");
        segment.write("this." + name + " = " + name + ";");
        segment.write_l("}");

    }
    segment.write_l("}");
}
```
