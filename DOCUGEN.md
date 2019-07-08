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

1. Snippet collectors that read the source files that the Java::Geci
interface lists. The snippets are collected into a `SnippetStore`
object, which itself is stored in the context managed by the framework.
Since this context is shared between the different generators the
`SippetStore` object is available to all snippet handling generators
that run controlled by the same Geci object or by Geci objects that
themselves share the same context. The `SnippetStore` can store a
snippet and can also retrieve a snippet using its name.

1. Snippet modifiers that modify the already collected snippets. There
are snippet modifying generators that trim off the spaces on the left of
the lines, perform search and replace on the lines using regular
expression, delete certain lines from snippets, join different snippets
together, number the lines and so on. These snippet modifiers are
controlled by the parameters of the source segments that use the actual
snippet(s) and to let different segments to use the same snippet with
individual modifications these snippet modifying generators create
copies of the snippets they modify.
 
1. Snippet inserters that insert the optionally modified snippets into
the text where they are needed in different segments. These are real
generators in the sense that they really modify the code, even if the
code is documentation. They do it the standard way Java::Geci support
writing segments and possibly failing the unit test when the
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
sippet willm be copied. This approach needs a lot of discipline no to
mix up the original snippet text and the copies. Many times it is better
to store these snippets in one or many snippet files, which is not part
of the documentation and the documentation files all contain copies.

Another use of the snippets when part of the documentation has its
natural place in the code itself. A good example is the configuration
parameters of the generators. Generators usually have an inner class
named `Config` with many fields. When a new configuration parameter is
inserted or deleted or the use of it changes then this is more likely to
update the documenation if it is attached directly to the field as a
comment / snippet than if it is in a separate documenatation file.
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

In other cases the order may be different. We may need to delete certain
lines from a code sample and want to number the lines so that the text
can reference the individual code lines in the explaination. At a
different code sample, however we want to number the lines first and then
delete the lines that we do not want in the documentation. That way the
numbering clearly denotes that some of the lines were deleted.

The ordering of the execution of the generators is generally up to date
to the framework, but generators can decide in which phase they want to
run.

## Implemented Snippet Handling Generators

As there are three types of snippet handling generators we will discuss
the implemented generators in three subsections.

### Collector

### Modifiers

### Snippet Inserters

In case of markdown formatted code is
enclosed between three starting and three ending back-ticks on a line.

When handling markdown Java::Geci needs the `MarkdownSegmentSplitHelper`
registered with the appropriate file extension. 

<!-- snip TestGenerateJavageciDocumentation trim="to=0"-->
```java
final var geci = new Geci();
Assertions.assertFalse(
    geci
        .source("..", ".").ignore("\\.git", "\\.(png|zip|class|jar|asc|graffle)$", "target")
        .log(Geci.MODIFIED)
        .register(SnippetCollector.builder().phase(0).build())
        .register(SnippetAppender.builder().phase(1).build())
        .register(SnippetRegex.builder().phase(2).build())
        .register(SnippetTrim.builder().phase(3).build())
        .register(SnippetNumberer.builder().phase(4).build())
        .register(SnipetLineSkipper.builder().phase(5).build())
        .register(MarkdownCodeInserter.builder().phase(6).build())
        .splitHelper("md", new MarkdownSegmentSplitHelper())
        .generate(),
    geci.failed());
```
