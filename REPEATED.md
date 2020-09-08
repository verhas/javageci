# Repeated Generator

## Deprecated

 ⚠️ **This generator is deprecated and replaced by the generator Iterate. The
generator Iterate has essentially the same features but it approaches
the task from a different angle that makes it much easier to use.**

⚠️ **Use this generator ONLY if you already started to use it in your project
and it would be too much effort to migrate your code to use the new
Iterate generator.**

The repeated generator can be used when the code contains some kind of
repeated code that is cumbersome to maintain manually. The generator can
collect several string values, like the names of some fields, or names
of methods from the source file and then it can generate code based on
templates into the different editor fold segments. The list of values
can be defined using start and stop lines limiting the range where the
values may be found and regular expression that cuts out the values from
the individual lines. The values can also be specified as comma separate
values in the configuration.

##Introduction

For example the complex generator `Templated` uses the generator
`Repeated` (as well as the config builder) to generate the configuration
parameters for the different templates, parameter definition consumers
and text modifying resolvers. These configuration parameters come in
triplets and in addition to this the template fields have to be repeated
in an inner class.

To do this the class is annotated as

```java
@Geci("repeated values='preprocess,processField,processMethod,processClass," +
    "processMemberClass,processMethods,processClasses,processFields," +
    "postprocess,preprocessClass,postprocessClass'")
public class Templated extends AbstractJavaGenerator {
...
}
```

This annotation defines the names of the template configuration fields
in the configuration `values` as comma separated list. The code contains
different editor folds. The generator builder in this case defines
different `selector` values (explained a bit later). When the editor
fold has an `id="xxx"` then the templates and other parameters assigned
to the `xxx` selector are used.

The following editor fold uses the template, define and resolver that
are defined after the selector `configTemplates`:

```java
        //<editor-fold id="configTemplates">
        private String preprocess = null;
        private String processField = null;
        private String processMethod = null;
        private String processClass = null;
        private String processMemberClass = null;
        private String processMethods = null;
        private String processClasses = null;
        private String processFields = null;
        private String postprocess = null;
        private String preprocessClass = null;
        private String postprocessClass = null;
        //</editor-fold>
```

The generator object is created using the generator builder pattern in a
test:


```java
new Geci().source("./javageci-core/src/main/java/", "../javageci-core/src/main/java/")
                .register(Repeated.builder()
                    //
                    .selector("configSetters")
                    .define((ctx, s) -> ctx.segment().param("setter", "set" + CaseTools.ucase(s)))
                    .template("```private void {{setter}}(String template) {\n" +
                        "    templates().{{value}} = template;\n" +
                        "}\n\n```")
                    //
                    .selector("bifunctions")
                    .template("```private BiFunction<Context, String, String> {{value}}Resolv = BiFuNOOP;```")
                    //
                    .selector("templates")
                    .template("```private String {{value}} = null;```")
                    //
                    .selector("consumers")
                    .template("```private {{type}} {{value}}Params = {{const}};```")
                    .define((ctx, s) -> {
                            String subtype;
                            if (s.startsWith("process") && (s.endsWith(subtype = "Field") || s.endsWith(subtype = "Method") || s.endsWith(subtype = "Class"))) {
                                ctx.segment().param("type", "BiConsumer<Context, " + subtype + ">",
                                    "const", "BiNOOP");
                            } else {
                                ctx.segment().param("type", "Consumer<Context>",
                                    "const", "NOOP");
                            }
                        }
                    )
                    //
                    .selector("configTemplates")
                    .template("```private String {{value}} = null;```")
                    //
                    .build()).generate()
```
This structure defines five different selectors. There are five
different editor folds in the code. Each selector can define a
`template()`, a `define()` and a `resolver()`. In the example above only
the first two is used, resolvers are rarely needed.

Templates can be defined by the name of the Java resource or can be
defined as the content of the template itself. In the example above the
template string is defined. It is recognized by the template loader
because the template starts and ends with three back-tick characters.

In the example the last selector is the one that contains the template
for the `configTemplates` editor fold. This template is used for each
value in the code.

## Configuration

### `start`

Can define a regular expression pattern. The generator will look for
values only after a line that matches this pattern. (The whole line
should match, not only part of it. In other words `match()` is used and
not only `find()` on the regular expression pattern.) The lines after
the one that matches this pattern until before the line matching the
`end` regular expression pattern will be searched for values.

There can be many start/end pairs in the code at different places. If
there is no line matching the `end` pattern the search for values will
last till the last line of the source file.

### `end`

Can define a regular expression pattern. The generator will not look for
values after a line that matches this pattern. The `start` and `end`
regular expression patterns essentially switch on and off the search for
values as the lines of the source code is scanned by the generator.

### `matchLine`

Can define a regular expression pattern. If this pattern can be found in
any line between the `start` and `end` pattern then the line will be
used to extract the value. After the matching (using `find()` on the
matcher) the first group will be used as value.

### `values`

Values can also be defined as configuration parameter in addition to
being collected from the source code. This configuration parameter can
contains the strings of values comma separated. These values will be
used together to those that were collected from the source code. Usually
programs either collect the values from the source or use this
configuration.

### `valuesSupplier`

Values can also be defined in the builder call chain via a
`Function<Class,List<String>>` that may return the list of values that
it calculates from the class object. This function will probably use
reflection to get fields, methods and whatnot from the class.

### `selector`

This configuration parameter can only be used in the builder of the
generator to specify the key to the template and defines that follow.
The templates as well as define and resolver lambdas are stored in maps
indexed by the selector and they are used to generate code into editor
fold segments that have the specific selector as id.

### `template`

This configuration can only be used in the builder following a selector
to specify the name of a Java resource text file that contains the
template text or the template text itself when the text starts and ends
with three back-tick characters (like code sections in markdown)

### `define`

This configuration can only be used in the builder following a selector
to specify a `BiConsumer<Context, String>` that can add segment
parameters for the processing of the template. The consumer gets the
context as a first argument and it can call `ctx.segment()` to get
access to the segment and through that it can add parameters to the
segment. These parameters are (`xxx`,`yyy`) pairs that are used when the
code generation is performed. Every occurence of `{{xxx}}` is replaced
with `yyy`. The second argument to the consumer is the actual value that
the template is used for. The argument passed to the `define()` method
in the builder is usually a lambda expression. This lambda is invoked
for every value in the `values` list.

### `resolver`

This configuration can only be used in the builder following a selector
to specify a `BiFunction<Context, String, String>` that can convert the
string as a last step after the parameters were resolved just before the
text is inserted into the generated segment.

### `ctx`

This configuration can only be used in the builder to pass an object
that implements the `javax0.geci.templated.Context` interface. In case
there is no such object configured then a default implementation will be
used.

A context passed as a first argument to the lambda expressions when
invoked that are used to define segment parameters referenced in the
template using the `{{xxx}}` format, as well as to resolvers that can
convert the text of the inserted code.

### `mnemonic`

This configuration can only be used in the builder to specify the
mnemonic of the generator. The default value is `repeated`. This can be
redefined in case one class needs multiple value lists into different
segments. 
