# Template Based Generator (DEPRECATED)

Instead of this generator use the [Jamal based generator](javageci-jamal/README.adoc).

## Introduction

The template based generator can be used to generate code when a specific generator is not available for the purpose but the task is not so difficult to require a specific generator.

Using `Templated` you can generate code into Java source files based on templates and string parameters.
What `Templated` really does is the following:

* Collects the methods of the class in a collection
* Collects the fields of the class in a collection
* Collects the inner/nested classes of the class in a collection
* Collects all the classes that are scanned by the code generator into a collection

After that it reads different templates, resolves the place holders in the templates using the information collected and then inserts the code into the source code.
All this is done is several steps, each having its template, parameters and parameter producers which are all configurable.
The steps are executed:

1.  `preprocess` before anything else
2.  `processField` for each filtered field in loop
3.  `processMethod` for each filtered method in loop
4.  `processMemberClass` for each filtered member class in loop
5.  `processFields` once for all filtered fields
6.  `processMethods` once for for all filtered methods
7.  `processMemberClasses` once for all filtered member classes
8.  `preprocessClass` before processing application classes 
9.  `processClass` for each application class in loop
10. `postprocessClass` once for all application classes
11. `postprocess` after all previous steps

`processField`, `processMethod`, `processMemberClass`, `processClass` are executed many times, once for each field, method, member class or scanned class.

In the next section we will discuss each step working and how to use and configure the step.

## Steps

Each steps are configurable, and in case a step is not configured it
essentially does nothing.
In other words you have to configure only the steps that are going to use.

Step configuration can define

* a template that the step will use
* parameter definition consumer (lambda expression).

The template configuration is a string and as such it can be configured when calling the generator builder as well as in the source code via annotation parameters or segment parameters.
The parameter definition "lambdas" are eventually not string, and thus they can only be configured in the builder.

`Templated` uses the configuration builder generator and thus can be configured in the recommended standard way.

When a step is named `stepName` then the configuration name for the template is also `stepName`.

The configuration name for the parameter defining "lambda" is `stepNameParams` (the same as the name of the step with the `Params` postfix).

The configuration name for the `BiFunction` resolver that can optionally convert the template before it is inserted as generated code is `stepNameResolv`.

The template itself is a string that contains `{{xxx}}` formatted placeholders that are replaced by the generator when it uses the segment.
The template can be in a resource.
In this case the name of the resource file has to be specified in the configuration.
The configuration can also specify the string itself.
If the configuration string starts and ends with three back-tick characters, like

```
    ```this is the content of the template and not the name of the resouce file```
```

then the configuration string itself is used as the template string (the back-ticks removed of course).
In other cases the configuration string is the name of the resource file.

In case the resource cannot be loaded then a default template is used that contains an error message between `/*` and `*/` strings.
This will get into the Java code as generated text and can be used for debugging of the resource naming. 
  
Resolvers and parameter defining methods/lambdas get a context as the first argument.
This context is an object that implements the interface `Templated.Context`.
For more information have a look at the JavaDoc of the class `Context`

The generator can be configured to hold a context object, and in case there is no specific context is provided then the generator will use a simple implementation that holds three objects that the parameter defining and the template resolver lambdas can use.

When a step is executed it starts defining segment parameters.
These are parameters that can be referenced using `{{xxx}}` notation in the template text.
For example the step `preprocess` defines the parameter `this.SimpleName`.
If the string `{{this.SimpleName}}` appears in the template defined in the configuration for this step then it will be replaced by the simple name of the class into which the code generation is running.
These parameters, unless deleted by the step, are available for subsequent steps.
Unless the documentation explicitly says the parameters are not deleted after a step is finished.

The next thing a step does is to invoke the parameter defining lambda that was configured for the generator in the builder.
This lambda should be a `Consumer` or `BiConsumer` interface implementation.
The different steps require different lambdas based on the numberof parameters they provide for the lambdas.
The first argument to these lambdas is the context object.
The second parameter, in case of a `BiConsumer` depends on the actual step.
It can be a method object, field object and so on.

When the parameters are defined by the generator and also by the configured lambdas the step loads the template and invokes the resolver `BiFunction` if configured that can alter the template text before it is passed to the segment writer so that the text gets into the source code.


### `preprocess`

This step is the first one executed by the generator.
This step defines the following template parameters:

* `this.SimpleName` the simple name of the class
* `this.Name` the name of the class
* `this.CanonicalName` the canonical name of the class
* `this.Package` the name of the package of the class
* `this.TypeName` the type name of the class
* `this.GenericString` the generic string representation of the class

The parameter defining consumer configured using the key `preprocessParams` gets four arguments:

1. Context object
2. Source object
3. Class
4. Segment

### `processField`
### `processMethod`
### `processMemberClass`
### `processFields`
### `processMethods`
### `processMemberClasses`
### `preprocessClass` 
### `processClass`
### `postprocess`
### `postprocessClass`

## General Configuration

# `ctx`