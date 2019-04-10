# Frequently Asked Questions about Java::Geci

Note that this is a living document and contains answers to questions that were asked multiple times and questions that
the developer thinks is worth answering here.

## What is the difference between Java::Geci and Roaster, which one is the better?

Roaster is available at https://github.com/forge/roaster

Roaster and Java:Geci are two different things and have different purposes, although there is some functionality
that they share. Roaster is a library, Java::Geci is more a framework that you can insert into your test cycle.
Let's have a look at what one does and the other does not and also the functions that they share.

* Both Roaster and Java::Geci support code generation. They both have an API that helps you generate Java code using a
  fluent API interface. This is one of the main features of Roaster and it provides a more extensive and more robust
  API for the purpose than Java::Geci.
  
* Roaster has a Java source code syntax analyzer. Java::Geci does not have anything like that. Java::Geci runs during
  the test cycle when the code was already compiled by the Java compiler and thus classes and other structures are
  available for introspection via the Java reflection API. This way the syntax analyzer of Java::Geci is the Java
  compiler itself.
  
* Java::Geci scans the source code and invokes the generators only for the classes/source code files that need code
  generation. Also, Java::Geci provides and API for the generators that they can use to provide the code and the
  framework takes care of the file modifications in case the generated code gets into some already existing source file
  or new file creation.
  
If Roaster has some features that you need and is not provided by Java::Geci you should use Roaster. If there is some
feature that you need from Java::Geci then you should use Java::Geci. If both has features then you can use both.
Java::Geci was designed to be open in the sense that althogh it provides code generation tools to build proper Java
source code structures you can use other tools skipping the function of this part of the Java::Geci API and provide
the generated source as plain text.

In short: Roaster and Java::Geci are two products for similar but not the same purposes and are not competitive tools. 
