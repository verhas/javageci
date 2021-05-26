# Using the existing code generators

Java::Geci provides several out-of-the-box code generators. In this
tutorial we'll use the generator `Accessor` as an example, but the
things explained in this tutorial apply to every other generator.

**Important:** when this tutorial talks about 'generators', it always means a core generator provided by Java::Geci, which are all subclasses of `AbstractFilteredFieldsGenerator`.

### Reminder - Dependencies

As a quick reminder from the [README](README.adoc.jam):
to use Java::Geci, add the following dependency to your project:
 ```xml
 <dependency>
     <!-- This is optional, you can use your own annotations or comment config -->
     <groupId>com.javax0.geci</groupId>
     <artifactId>javageci-annotation</artifactId>
     <version>1.6.3-SNAPSHOT</version>
 </dependency>
 <dependency>
     <groupId>com.javax0.geci</groupId>
     <artifactId>javageci-engine</artifactId>
     <scope>test</scope>
     <version>1.6.3-SNAPSHOT</version>
 </dependency>
 ```
These are the base modules that are necessary to get started.
This tutorial also uses the additional `javageci-core` module, which contains the core generators.

### Using Java::Geci core generators

You use Java::Geci by using the `javax0.geci.engine.Geci` class in tests.

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

This might seem like a lot at first, so let's break it down.

`Geci` has a fluent interface.

The method `source()` can be used to specify the directories where your source files are.
If you have source files in different places you have to chain several `source()` invocations one after the other.
Each single call to `source()` specifies one source set with several alternative locations.
These are regarded as possible directories and the first that is okay is used to discover the files.

This means:

* Call `source("foo").source("bar")` when you have sources in **both** foo **and** bar. 
* Call `source("foo", "bar")` if you have sources in **either** foo **or** bar.

If all your source code is in the `src/main/java` directory then you do not need to specify any source location.
`src/main/java` is the default setting for Java::Geci.

The method `register()` can register one or more source code generators.
You can also chain `register()` calls and register your generator objects one-by-one.
Each registered generator will be invoked on the sources.

The method invocation `generate()` will <!-- do the following -->
read the source files,
invoke the generators and
write the generated code into the files.

### Marking your classes for code generation

Every core generator has a *mnemonic* which identifies the generator.
It is a plain `String` that is unique to each generator.
For example the Accessor generator (that generates getters and setters) has the mnemonic "accessor".

After you assigned the source to the generator this mnemonic marks classes that need code generation.
This can be mainly done in two ways.

#### Marking your classes with annotations

You can add the mnemonic of the generator in a `@Geci` annotation.

For example:

```java
@Geci("accessor")
@Geci("builder")
public class ExampleWithAnnotations {
    private int example;
}
```

The `@Geci` annotation is repeatable, so you can mark your classes for multiple generators.
When you first generate code Java::Geci will automatically put dedicated editor-fold segments at the end of your class.
These segments are where the generated code will go.

Example output:

<!-- snip ExampleWithAnnotations -->
```java
@Geci("accessor")
@Geci("builder")
public class ExampleWithAnnotations {
    private int example;
    //<editor-fold id="builder">
    @javax0.geci.annotations.Generated("builder")
    public static ExampleWithAnnotations.Builder builder() {
        return new ExampleWithAnnotations().new Builder();
    }

    @javax0.geci.annotations.Generated("builder")
    public class Builder {
        @javax0.geci.annotations.Generated("builder")
        public Builder example(final int x) {
            ExampleWithAnnotations.this.example = x;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public ExampleWithAnnotations build() {
            return ExampleWithAnnotations.this;
        }
    }
    //</editor-fold>
    //<editor-fold id="accessor">
    public void setExample(int example) {
        this.example = example;
    }

    public int getExample() {
        return example;
    }

    //</editor-fold>
}
```

#### Marking your classes with dedicated editor-fold segments

The other approach is to add a dedicated editor-fold segment to the class with the mnemonic as an id.

For example:

```java
public class ExampleWithEditorFolds {
    private int example;
    //<editor-fold id="accessor">
    //</editor-fold>
    //<editor-fold id="builder">
    //</editor-fold>
}
```

The editor-fold segment is like an XML tag (but in a comment).
You have to add a new editor-fold segment for each generator you want to use.

Example output:

<!-- snip ExampleWithEditorFolds -->
```java
public class ExampleWithEditorFolds {
    private int example;
    //<editor-fold id="accessor">
    public void setExample(int example) {
        this.example = example;
    }

    public int getExample() {
        return example;
    }

    //</editor-fold>
    //<editor-fold id="builder">
    @javax0.geci.annotations.Generated("builder")
    public static ExampleWithEditorFolds.Builder builder() {
        return new ExampleWithEditorFolds().new Builder();
    }

    @javax0.geci.annotations.Generated("builder")
    public class Builder {
        @javax0.geci.annotations.Generated("builder")
        public Builder example(final int x) {
            ExampleWithEditorFolds.this.example = x;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public ExampleWithEditorFolds build() {
            return ExampleWithEditorFolds.this;
        }
    }
    //</editor-fold>
}
```

Annotating your classes is the preferred approach, because it is more up-front about using code generation.
In both cases Java::Geci will put the generated code in the dedicated editor-fold segment.

You _can_ do both!
This way you specify **which classes** utilize code generation and **where the generated code should go**.

There are other ways for marking your files for code generation.
This is covered in a later tutorial, titled [How to write your own annotations for Java::Geci](ANNOTATIONS.md).

### Summary

* In your tests, use the `Geci` class to:
    - Add your source directories with `source()`
    - Register the generators you want to use on those source files with `register()`
    - Start the code generation process by calling `generate()`
* In your sources:
    - Use the `@Geci` annotation with the mnemonic of the generator you want to use.
    - Or add an editor-fold segment with the mnemonic as the id.

If we run our unit test now, it will say:

    Geci modified source code. Please compile and test again.

This is exactly what we expected.
It means that the generator and classes are properly configured/annotated/marked and Java::Geci generated some code.

If you would like to know more about the generators provided by Java::Geci, [read their dedicated tutorials](GENERATORS.adoc.jam).

