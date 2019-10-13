package javax0.geci.log;

public class LoggerFactory {
    public static Logger getLogger() {
        // return new Logger( StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
        try {
            return new Logger(Class.forName(Thread.currentThread().getStackTrace()[1].getClassName()));
        } catch (ClassNotFoundException e) {
            return new Logger(LoggerFactory.class);
        }
    }
}
