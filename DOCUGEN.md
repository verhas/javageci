# Documentum Generation using SNIPPETS

A software is as good as it is used. Good and up-to-date documentation
is needed for the use of a software and be honest: good documentation is
rare. There are many aspects to create good documentation like good
grammar, easy to understand sentences, up-to-date facts, examples in the
documentation and many others. Docugen code generator working inside the
Java::Geci framework can help to keep the documentation up-to-date
reducing redundancy.

Documentation is redundant many times. This is not a problem.
Documenatation is not code. When we read the documentation the
information has to be consistent and has to be there where we actually
are in the reading and understanding process. This many times mean that
the same text appears at different places. It gets there using copy and
paste. Similarly program documentation many times contains code
examples. Sometimes these examples are written by the document
writer/editor (bad practice), other times, they are written by the
programmers as example programs and th editors only copy the code to the
documentation.

Documentation has to follow the change of the code. If the code chnages
the relevant, but only the relevant parts of the documentation has to be
updates.

If the copy and paste or update operations are preformed each time the
original text or the code changes then the documentation is up-to-date.
Usually these operations, when done by a human are not performed,
performed late or with mistakes. The reason for this is that these are
boring and mechanical tasks which humans are not good at. The same time
computers can be asked to perform these tasks and this is what
Java::Geci snippet handling does.

## Using snippets

To have Java::Geci do these copy paste operations the parts of the text,
or program code that needs to appear elsewhere as well has to be
signalled. This is done putting a special line containing

```
snippet snippet_name parameters
```

where the text starts and putting a special line containing

```
end snippet
```

after the last line of the text. These two lines define a snippet. The
words `snippet` and `end snippet` are configurable in the code generator
that collects the snippets. By default these are the strings and the
word `snippet` following the `end` is optional and you can also write
`snipet` if you like to mispell the word `snippet`.

Snippets are essentially text lines. Each snippet has a name and
optionally has parameters. The snippet collectors usually collect the
lines from the sources recognizing the start and the end lines. For
example the class `SnippetCollector` collects the snippets with the
start and end lines as described above. If only requires that the line
contains* the work `snippet` there is a name after it and optionally
parameters. Since it requires that it only *contains* these parts the
Java comment sequence `//`, `/*` or `*/` can precede or follow the
snippet starting or ending characters.


## Snippet handling generators

Snippet handling is implemented by many different generators. Thes are
not conventional generators in the sense that many of them  do not
generate code. They, however, implement the `Generator` interface and
thus work inside the Java::geci framework and they can be configured the
same way as many other generators.

There are three type of snippet handling generators:

1. **Snippet collectors** that read the source files that the Java::Geci
interface lists. The snippets are collected into a `SnippetStore`
object, which itself is stored in the context managed by the framework.
Since this context is shared between the different generators the
`SippetStore` object is available to all snippet handling generators
that run controlled by the same Geci object or by Geci objects that
themselves share the same context. The `SnippetStore` can store a
snippet and can also retrieve a snippet using its name.

1. **Snippet modifiers** that modify the already collected snippets.
There are snippet modifying generators that trim off the spaces on the
left of the lines, perform search and replace on the lines using regular
expression, delete certain lines from snippets, join different snippets
together, number the lines and so on. These snippet modifiers are
controlled by the parameters of the source segments that use the actual
snippet(s) and to let different segments to use the same snippet with
individual modifications these snippet modifying generators create
copies of the snippets they modify.
 
1. **Snippet inserters** that insert the optionally modified snippets
into the text where they are needed in different segments. These are
real generators in the sense that they really modify the code, even if
the code is documentation. They do it the standard way Java::Geci
support writing segments and possibly failing the unit test when the
documentation *was* not up-to-date.

Later in this documentum we will write about the available snippet
handling generators and how to use them.

## Snippet Strategy

Before getting into the details of the different snippet handling
generators let's talk about the strategy how to use snippets.

When the documentation contains sample code then it is fairly
straightforward what a snippet is. It is the code of the unit test that
plays the role of the documentation. The code is inserted into the
documentation as formatted code. 

There parts of the document that are repeated. It is also possible to
have hyperlinks and references to the text, but many times it is better
to repeat the text. In this case one part of the document can play the
role of the snippet and the other parts may be the segments where the
snippet will be copied. This approach needs a lot of discipline no to
mix up the original snippet text and the copies. Many times it is better
to store these snippets in one or many snippet files, which is not part
of the documentation and the documentation files all contain copies.

Another use of the snippets when part of the documentation has its
natural place in the code itself. A good example is the configuration
parameters of the generators. Generators usually have an inner class
named `Config` with many fields. When a new configuration parameter is
inserted or deleted or the use of it changes then this is more likely to
update the documenation if it is attached directly to the field as a
comment / snippet than if it is in a separate documentation file.
Although there is a high chace that the comment, which is part of the
documentation will also be out dated as comments usually are outdated.
Even though, the chances are a slim better than in case of a separate
documentation.

## Generator Phases

Code generators usually work alone without the aid of other generators.
Snippet handling generators are different. They cooperate heavily. In
this case it has the consequence that there has to be a specific order
how these generators are executed. It is obvious that we cannot trim the
lines of the snippets eliminating the tabbing on the left side of the
lines if they were not collected yet. Also trimming is not possible when
the lines were already numbered. In this case the order is fairly
obvious.

In other cases the ordering may not be so simple and obvious. We may
need to delete certain lines from a code sample and want to number the
lines so that the text can reference the individual code lines in the
explanation. In this case numbering comes after the deletetion of
certain lines. A different code sample, however, needs to number the
lines first and then delete the lines that we do not want in the
documentation. That way the numbering follows the numbering of the
original lines in the code sample and it clearly shows in the
documentation that some of the lines were deleted.

The ordering of the execution of the generators is generally up to the
framework, but generators can decide in which phase they want to run.
The framework executes the generators in multiple phases many times and
it consults each generator in each phase whether it needs execution that
phase or not.

Snippet handling generators need execution only in a single phase and
they can be configured during their creation (using the builder) which
phase they should run. If the line trimming should run before numbering
then the generator `SnippetTrim` should have a smaller phase serial
number than `SnippetNumberer`. The snippet handling generator build up usually looks like
the one that is used to create this documentation:

<!-- snip TestGenerateJavageciDocumentation trim="to=0"-->
```java
final var geci = new Geci();
int i = 0;
Assertions.assertFalse(
    geci
        .source("..", ".").ignore("\\.git", "\\.(png|zip|class|jar|asc|graffle)$", "target")
        .log(Geci.MODIFIED)
        .register(SnippetCollector.builder().phase(i++).build())
        .register(SnippetAppender.builder().phase(i++).build())
        .register(SnippetRegex.builder().phase(i++).build())
        .register(SnippetTrim.builder().phase(i++).build())
        .register(SnippetNumberer.builder().phase(i++).build())
        .register(SnipetLineSkipper.builder().phase(i++).build())
        .register(MarkdownCodeInserter.builder().phase(i++).build())
        .splitHelper("md", new MarkdownSegmentSplitHelper())
        .generate(),
    geci.failed());
```

The generators are registered in the order they are to be executed and
the call to `phase(i++)` registers the phases 0, 1, 2 ... and so on.

## Implemented Snippet Handling Generators

As there are three types of snippet handling generators we will discuss
the implemented generators in three subsections.

### Collector

Currently there is only one snippet collecting generator (unless this
document is out of date). The generator `SnippetCollector`. It is
executed bz the framework for all sources that are configured and
collected and the collector scans through the lines of the sources and
collects the snippets.

It starts to collect lines after a line that matches the regular
expression named `snippetStart` and before `snippetEnd`. These regular
expressions have default values and as they are listed in the `Config`
inner class of the generator `SnippetCollector`:

<!-- snip SnippetCollector_config trim="to=0" regex="replace='/snippetEnd/snippetEnd  /' replace='/private Pattern//' replace='/~);//' replace='/Pattern.compile~(//' escape='~'"-->
```java
snippetStart = "(?://|/\\*)\\s*snipp?et\\s+(.*)$"
snippetEnd   = "(?://\\s*end(?:\\s+snipp?et)?|end(?:\\s+snipp?et)?\\s*\\*/)"
```

The characters that follow the `snippet` to the end of the line are
parameters. Syntactically they are the same as the parameters in the
`@Geci` annotations or in the editor-fold segments. The first word is
the name/id of the snippet the rest are parameters. The snippet is
stored using the name and this name is used when later the snippet is
referenced in the text that needs the snippet to be inserted. The
parameters are stored along with the snippet and are available for
snippet modifying and snippet inserting generators.

### Modifiers

Snippet modifiers run when the snippets are collected and modify the
lines of the snippets. Because the same snippet may be used with
different modifications for different segments modifiers create a copy
of the snippet before any modification is performed. The copy is
assigned to the segment where the snippet will be inserted.

Keeping track of the copies is automatically done by the snippet store.
The snippet store stores the original snippes as they are collected and
also copies that are identified by the name of the snippet and by the
id of the segment.

For example the snippet named `SnippetCollector_config` is used a few
lines above and the use is configured to perform certain modifications.
Namely these are trimming, and replacing declaration clutter that is
important in the Java code, but distracts in the documentation. We can
insert the same snippet without any modifications. 

<!-- snip SnippetCollector_config_other snippet="SnippetCollector_config"-->
```java
        private Pattern snippetStart = Pattern.compile("(?://|/\\*)\\s*snipp?et\\s+(.*)$");
        private Pattern snippetEnd = Pattern.compile("(?://\\s*end(?:\\s+snipp?et)?|end(?:\\s+snipp?et)?\\s*\\*/)");
```



### Snippet Inserters

In case of markdown formatted code is
enclosed between three starting and three ending back-ticks on a line.

When handling markdown Java::Geci needs the `MarkdownSegmentSplitHelper`
registered with the appropriate file extension. 

<!-- snip TestGenerateJavageciDocumentation trim="to=0"-->
```java
final var geci = new Geci();
int i = 0;
Assertions.assertFalse(
    geci
        .source("..", ".").ignore("\\.git", "\\.(png|zip|class|jar|asc|graffle)$", "target")
        .log(Geci.MODIFIED)
        .register(SnippetCollector.builder().phase(i++).build())
        .register(SnippetAppender.builder().phase(i++).build())
        .register(SnippetRegex.builder().phase(i++).build())
        .register(SnippetTrim.builder().phase(i++).build())
        .register(SnippetNumberer.builder().phase(i++).build())
        .register(SnipetLineSkipper.builder().phase(i++).build())
        .register(MarkdownCodeInserter.builder().phase(i++).build())
        .splitHelper("md", new MarkdownSegmentSplitHelper())
        .generate(),
    geci.failed());
```
