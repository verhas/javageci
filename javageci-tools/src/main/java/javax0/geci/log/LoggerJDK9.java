package javax0.geci.log;

class LoggerJDK9 implements LoggerJDK {

    static LoggerJDK factory(Class<?> forClass){
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
