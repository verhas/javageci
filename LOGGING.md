# Logging support

## Configuring logging

Java::Geci is using the Java 9+ `System.getLogger()` functionality to get access to the logging framework facade.
If you want to configure how the logging happens then you have to configure your loggers that are in effect during the test execution.
The configuration of the logging system is out of the scope of this document, and generally a standard matter.

The compilation that is targeting the Java 8 VM is used the Java standard JUL logger.

## Logging in generators

The generators are encouraged to use the logger that the `Source` object provides.
The `Source` object is accessible by the generators since this object is passed as an argument to the method `process()` implemented by every generator.
Note that the approach to provide a customer logger to plugins (which are generators in case of Java::Geci) is an industry standard practice and can be found in such well-established software like Maven.

The `Source#getLogger()` returns an object that implements the `javax0.geci.api.Logger` interface.
This object is supposed to be used for logging.

The advantage of this logger is that the log messages are collected during the execution, and the log messages belonging to one source code are sent to the logging output together at the end of the processing.

The framework can also be configured to send only those messages to the output which belong to a source that were

* `UNTOUCHED` and/or
* `TOUCHED` and/or
* `MODIFIED`

If you are not interested in the log messages that were generated during the processing of a source that was not touched (source was read and processed by the generator, but the generator did not try to write anything into the code) then these messages will not be printed if the log is set that way.

A `javax0.geci.engine.Geci` object has the chain-able method `log(int)` that can be used to configure what to log.
By default messages sent during handling a ` MODIFIED & TOUCHED` source are printed.
You can use `&` character to use the different constants `UNTOUCHED`, `TOUCHED`, `MODIFIED` defined in the class `javax0.geci.engine.Geci`.

Note that `log()` is not defined in the interface `javax0.geci.api.Geci`.

## Suggestions

Usually generators do not need to log things when they are simple.
If you need logging then your generator is probably complex.

There are 5 levels of logging in `javax0.geci.api.Logger` each with its method:

* `trace` use as usual
* `debug` use as usual
* `info` should contain information that the user, who does not care about the inner working of the generator is about to read.
  Do not log code that is generated.
  It is either already in the source code or it will be saved there.
* `warning` when the generator thinks that something is unusual and something is probably different than it was intended by the user.
* `error` when the generator sees something that it cannot cope with.
  In such a case it is advised to throw a `GeciException`.

Do not log the name of the source, it will be there in the output automatically.
Also do not log the name of the generator.
The name of the generator class, or the mnemonic of the generator (if it has mnemonic) will automatically be printed.