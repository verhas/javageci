package javax0.geci.log;

public class LoggerFactory {
    public static Logger getLogger() {

        return new Logger( StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }
}
