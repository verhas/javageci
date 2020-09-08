# The simplest Code Generator using Java:Geci

The simplest example will contain a unit test class and a code generator that generates a method into the test code.
The generated method will just return a constant string.
The test code will check that this generated code works as expected.
The very first version of the unit test class will be

<!-- snip Test1HelloWorld regex="replace='/Test1/Test/'" -->
```java
package javax0.geci.tutorials.simplest;

import org.junit.jupiter.api.Test;

@SuppressWarnings("EmptyMethod")
public class TestHelloWorld {

    @Test
    public void generatedMethodReturnsGreetings(){

    }

    //<editor-fold id="HelloWorldTest">
    private static String greeting() {
        return "greetings";
    }
    //</editor-fold>
}
```

The code contains the skeleton of the test and also the placeholder for the method that will be generated.
This class as it is does not really do anything but this is how we start.
The next thing is that we write the code generator.

The simplest way to write a generator is to extend the `AbstractGenerator`, which is defined in the `geci.tools` module.

<!-- snip HelloWorldTestGenerator1 regex="replace='/1//'" -->
```java
package javax0.geci.tutorials.simplest;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;

public class HelloWorldTestGenerator extends AbstractJavaGenerator {
    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        try(var segment = source.open("HelloWorldTest")){
            segment.write_r("private static String greeting() {");
            segment.write("return \"greetings\";");
            segment.write_l("}");
        }
    }

    @Override
    public String mnemonic() {
        return "HelloWorldTest";
    }
}
```

The generator has to define two methods.
One should return the mnemonic of the generator.
This is the string that the code generating code can and should use in the `Geci` annotation.
This mnemonic is the unique string identifier of the generator that we use in the code when we refer to the generator as we will see in the next version of the text class.

The method `process()` gets the source object as argument, the klass that was generated from the source file during the compilation process and the parameters that are defined in the annotation for this generator.
Currently we do not use the class and we also do not need any parameters.
We use only the `source` object.

We use the `source` object to open the `simplest` segment.
The segment contains the lines that are between the `//<editor-fold ...>` and `//</editor-fold>` lines.
The name of the segment should be defined using the `id` parameter.
In out example the line contains `//<editor-fold id="HelloWorldTest">` thus the name is `HelloWorldTest` and this is the name that the code generator is looking for.

Code generators that write into a single segment usually use the mnemonic of the generator to name the segment.

The `segment` object can be used to write the code into that segment.
In this case we write three lines that define a simple static method that returns the string `"greeting"`.
The tree different version of `write()` automatically indent the lines.
`_r` at the end of the method means that the next lines are one tab to the right.
`_l` means that the line and any consequent lines are back in the previous tab position.

Now we can write the test that generates the code.   
 
<!-- snip Test2HelloWorld regex="replace='/2//'" -->
```java
package javax0.geci.tutorials.simplest;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"SameReturnValue", "EmptyMethod"})
@javax0.geci.annotations.Geci("HelloWorldTest")
public class TestHelloWorld {

    @Test
    public void generateOkay() throws Exception {
        if (new Geci().source("src/test/java").register(new HelloWorldTestGenerator1()).generate()) {
            Assertions.fail("Code was regenerated");
        }
    }

    @Test
    public void generatedMethodReturnsGreetings() {
    }

    //<editor-fold id="HelloWorldTest">
    private static String greeting() {
        return "greetings";
    }
    //</editor-fold>
}
```

The method `generateOkay()` checks that the code in the test is okay and fails if not.
To notify that this class needs the attention of the code generator `HelloWorldTest` we have to annotate the class with the annotation with `@Geci("HelloWorldTest")`.
The test method <!-- DOES THE FOLLOWING: -->
creates a new `Geci` object,
defines where the source files are,
registers an instance of the `HelloWorldTestGenerator` generator
and then executes the generation invoking the method `generate()`.

When you follow this tutorial, do not copy the lines

```
private static String greeting() {
    return "greetings";
}
```

to your code.
Just leave it empty between the

```java
    //<editor-fold id="HelloWorldTest">
    //</editor-fold>
```

lines.
When you execute the test `generateOkay()` it will automatically insert these lines for you.
The test, when you run it the first time, will fail and will output a stack trace and the message `Code was regenerated`.
The same time it will insert the lines into the test file.
Run the test again and it will run without error.

After this we can finally write the test itself:

<!-- snip Test3HelloWorld trim="do" -->
```java
@Test
public void generatedMethodReturnsGreetings() {
    Assertions.assertEquals("greetings", greeting());
}
```

If you run the code again both tests will run fine.
