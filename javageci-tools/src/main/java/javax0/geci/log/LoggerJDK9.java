package javax0.geci.log;
/**
 * <p>The JDK9+ compatible implementation of the {@link LoggerJDK} interface</p>
 *
 * <p>Note that this source file is explicitly excluded from the compilation when the {@code JVM8} profile is used in
 * Maven.</p>
 */

class LoggerJDK9 implements LoggerJDK {

    /**
     * <p>A factory that creates a new instance of this class initializing the logger for the parameter class. This
     * method is invoked from the class {@link Logger} via reflection.</p>
     *
     * @param forClass the class for which the logger is needed
     * @return the new instance
     */
    static LoggerJDK factory(Class<?> forClass) {
        return new LoggerJDK9(forClass);
    }

    LoggerJDK9(Class<?> forClass) {
        this.LOGGER = System.getLogger(forClass.getName());
    }

    private final System.Logger LOGGER;

    private void log9(System.Logger.Level level, String format, Object... params) {
        if (LOGGER.isLoggable(level)) {
            var s = String.format(format, params);
            LOGGER.log(level, s);
        }
    }

    @Override
    public void log(int level, String format, Object... params) {
        switch (level) {
            case LoggerJDK.TRACE:
                log9(System.Logger.Level.TRACE, format, params);
                break;
            case LoggerJDK.DEBUG:
                log9(System.Logger.Level.DEBUG, format, params);
                break;
            case LoggerJDK.INFO:
                log9(System.Logger.Level.INFO, format, params);
                break;
            case LoggerJDK.WARNING:
                log9(System.Logger.Level.WARNING, format, params);
                break;
            case LoggerJDK.ERROR:
                log9(System.Logger.Level.ERROR, format, params);
                break;
        }
    }
}
