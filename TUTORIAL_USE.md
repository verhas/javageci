# Using the existing code generators

Java::Geci provides several out-of-the-box code generators. In this
tutorial we'll use the generator `Accessor` as an example, but the
things explained in this tutorial apply to every other generator.

**Important:** when this tutorial talks about 'generators', it always
*means a core generator provided by Java::Geci, which are all subclasses
*of `AbstractFilteredFieldsGenerator`.

### Reminder - Dependencies

As a quick reminder from the [README](README.md):
to use Java::Geci, add the following dependency to your project:
 ```xml
 <dependency>
     <!-- This is optional, you can use your own annotations or comment config -->
     <groupId>com.javax0.geci</groupId>
     <artifactId>javageci-annotation</artifactId>
     <version>1.1.2-SNAPSHOT</version>
 </dependency>
 <dependency>
     <groupId>com.javax0.geci</groupId>
     <artifactId>javageci-engine</artifactId>
     <scope>test</scope>
     <version>1.1.2-SNAPSHOT</version>
 </dependency>
 ```
There are other modules, but you do not need to declare dependency on
them as the engine module has transitive dependency and thus maven
automatically will use them.

### Using Java::Geci core generators

You use Java::Geci by using the `javax0.geci.engine.Geci` class in tests.

<!-- snip TestAccessor -->
```java
    @Test
    public void testAccessor() throws Exception {
        Geci geci;
        Assertions.assertFalse(
                (geci = new Geci()).source(maven().module("javageci-examples").mainSource())
                        .register(Accessor.builder().build())
                        .generate(),
                geci.failed());
    }
```

This might seem like a lot at first, so let's break it down.

`Geci` has a fluent interface.

The method `source()` can be used to specify the directories where your
source files are. If you have source files in different places you have
to chain several `source()` invocations one after the other. Each single
call to `source()` specifies one source set with several alternative
locations. These are regarded as possible directories and the first that
is okay is used to discover the files.

This means:

* Call `source("foo").source("bar")` when you have sources in **both**
  foo **and** bar. 
* Call `source("foo", "bar")` if you have sources in **either** foo
  **or** bar.

The method `register()` can register one or more source code generators.
You can also chain `register()` calls and register your generator
objects one-by-one. Each registered generator will be invoked on the
sources.

Finally the method invocation `generate()` will do the work, read the
source files, invoke the generators and write to the files the code the
generators created.

Every core generator has a *mnemonic* which identifies the generator.
For example the Accessor generator (that generates getters and setters)
has the mnemonic "accessor".

After you assigned the source to the generator this mnemonic is used to
identify which classes need code generation. This can be mainly done in
two ways:

* Adding the mnemonic of the generator in a `@Geci` annotation.

Example (using the Accessor generator):

```java
@Geci("accessor")
public class Example {
    private int example;
}
```

* Adding an editor-fold segment to the class with the mnemonic as an id.

Example (using the Accessor generator):

```java
public class Example {
    private int example;
    //<editor-fold id="accessor">
    //</editor-fold>
}
```

Generally speaking, annotating your classes is preferred because it is
more up-front about using code generation.

You can do both! This way you specify **which classes** utilize code
generation and also **where the generated code should go** as Java::Geci
will always put the generated code in the editor-fold segment. If you
only use annotation, Java::Geci will automatically add the editor-fold
segment at the end of your class when it first generates code.

There are other ways for marking your files for code generation, covered
in a later tutorial, titled [How to write your own annotations for
Java::Geci](ANNOTATIONS.md)

That's it! To summarize:

1. In your tests, use the `Geci` class to: <br/>
    - Add your source directories with `source()`
    - Register the generators you want to use on those source files with
      `register()`
    - Start the code generation process by calling `generate()`
2. In your sources: <br/>
    - Use the `@Geci` annotation with the mnemonic of the generator you
      want to use.
    - Or add an editor-fold segment with the mnemonic as the id.

If we run our unit test now, it will say:

    Geci modified source code. Please compile and test again.

This is exactly what we expected. It means that the generator and
classes are properly configured/annotated/marked and Java::Geci
generated some code. It looks like this:

```java
@Geci("accessor")
public class Example {
    private int example;
    //<editor-fold id="accessor">
    public int getExample() {
        return example;
    }
    
    public void setExample(int example) {
        this.example = example;
    }
    
    //</editor-fold>
}
```

If you would like to know more about the generators provided by
Java::Geci, [read their dedicated tutorials](GENERATORS.md).

