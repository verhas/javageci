package javax0.geci.log;

public class Logger {
    public Logger(Class<?> forClass) {
        this.LOGGER = System.getLogger(forClass.getName());
    }

    private final System.Logger LOGGER;

    private void log(System.Logger.Level level, String format, Object[] params) {
        if (LOGGER.isLoggable(level)) {
            var s = String.format(format, params);
            LOGGER.log(level, s);
        }
    }

    public void trace(String format, Object... params) {
        log(System.Logger.Level.TRACE, format, params);
    }

    public void debug(String format, Object... params) {
        log(System.Logger.Level.DEBUG, format, params);
    }

    public void info(String format, Object... params) {
        log(System.Logger.Level.INFO, format, params);
    }

    public void warning(String format, Object... params) {
        log(System.Logger.Level.WARNING, format, params);
    }

    public void error(String format, Object... params) {
        log(System.Logger.Level.ERROR, format, params);
    }
}
