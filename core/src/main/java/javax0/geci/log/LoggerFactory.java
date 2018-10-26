package javax0.geci.log;

public class LoggerFactory {
    public static Logger getLogger(Class<?> forClass) {
        return new Logger(forClass);
    }
    public static Logger getLogger() {

        return new Logger( StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }
}
