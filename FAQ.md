# Frequently Asked Questions about Java::Geci

Note that this is a living document and contains answers to questions
that were asked multiple times and questions that the developer thinks
is worth answering here.

## What is the difference between Java::Geci and Roaster, which one is the better?

Roaster is available at https://github.com/forge/roaster

Roaster and Java:Geci are two different things and have different
purposes, although there is some functionality that they share. Roaster
is a library, Java::Geci is more a framework that you can insert into
your test cycle. Let's have a look at what one does and the other does
not and also the functions that they share.

* Both Roaster and Java::Geci support code generation. They both have an
 API that helps you generate Java code using a fluent API interface.
 This is one of the main features of Roaster and it provides a more
 extensive and more robust API for the purpose than Java::Geci.
  
* Roaster has a Java source code syntax analyzer. Java::Geci does not
 have anything like that. Java::Geci runs during the test cycle when the
 code was already compiled by the Java compiler and thus classes and
 other structures are available for introspection via the Java
 reflection API. This way the syntax analyzer of Java::Geci is the Java
 compiler itself.
  
* Java::Geci scans the source code and invokes the generators only for
 the classes/source code files that need code generation. Also,
 Java::Geci provides and API for the generators that they can use to
 provide the code and the framework takes care of the file modifications
 in case the generated code gets into some already existing source file
 or new file creation.
  
If Roaster has some features that you need and is not provided by
Java::Geci you should use Roaster. If there is some feature that you
need from Java::Geci then you should use Java::Geci. If both has
features then you can use both. Java::Geci was designed to be open in
the sense that althogh it provides code generation tools to build proper
Java source code structures you can use other tools skipping the
function of this part of the Java::Geci API and provide the generated
source as plain text.

In short: Roaster and Java::Geci are two products for similar but not
the same purposes and are not competitive tools.

## What is the difference between Java::Geci and xtext

Xtext is a full blown DSL language implementation tool that builds on
top of the Eclipse IDE infrastructure. It is complex and can be used
when there is a need to solve complex problems that need a new
programming language.

On the other hand Java::Geci is a simple tool that you can use to solve
simpler code generation tasks that does not need a full domain specific
language support. Getting acquainted with Java::Geci is a task of a few
hours. If you have a not too complex code generation task you can have
it up and running within a day.

## What happens if I reformat the generated code?

When you reformat the generated code then the code generation framework
will feel that the code it generated during the code generation phase is
different from the one, which is in the source file and will insert the
original code as it was generated before the IDE performed formatting.

This definitely is an issue and there are remedies to handle the
situation.

* Do not format your code in a way that reorganizes the order of the
  fields, methods, inner classes and other members of the class. Such a
  formatting may move the generated code out of the editor-fold parts
  and that way the generated code gets mixed up with the manual code.
  There is no remedy for that.

* You can configure your IDE to format only the code that was changed.
  After the ode generation phase you should `commit` the modified file
  into Git and do further editing only after. If you set the editor to
  format only the changes or the manually selected code you will avoid
  the reformatting of the generated code.
  
* If you cannot avoid or do not want to avoid reformatting the generated
  code then you can define a `BiPredicate<List<String>, List<String>>`
  for the code generator `Geci` object calling the
  `comparator(BiPredicate<List<String>, List<String>>)` method
  before registering the generators. Currently there is pre-cooked
  comparator that would recognize when the code differs only in
  formatting, but it is on the roadmap.   
