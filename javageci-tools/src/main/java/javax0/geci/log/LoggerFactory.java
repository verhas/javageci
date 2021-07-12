package javax0.geci.log;

/**
 * <p>Create a logger that will use the caller code's class as the logger name.</p>
 *
 * <p>The current implementation used Java 8 compatible calls, which are less performant. When Java 8 compatibility will
 * be abandoned in later versions of the application the code has to be simplified to use the StackWalker to get the
 * callers class.</p>
 *
 * <p>Note that performance creating a logger should not be an issue with the usual way of creating loggers as they are usually
 * objects referenced static and final fields and that way they get invoked only once per class.</p>
 */
public class LoggerFactory {
    public static Logger getLogger() {
        return new Logger(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }
}
