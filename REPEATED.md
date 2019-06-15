# Repeated Generator

The repeated generator can be used when the code contains some kind of
repeated code that is cumbersome to maintain manually. The generator can
collect several string values, like the names of some fields, or names
of methods from the source file and then it can generate code based on
templates into the differente editor fold segments. The list of values
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
### `selector`
### `template;`
### `ctx`
### `resolver;`
### `define;`
