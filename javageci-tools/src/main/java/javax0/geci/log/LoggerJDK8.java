package javax0.geci.log;

import java.util.logging.Level;
import java.util.logging.Logger;

class LoggerJDK8 implements LoggerJDK {

    static LoggerJDK factory(Class<?> forClass){
        return new LoggerJDK8(forClass);
    }

    LoggerJDK8(Class<?> forClass) {
        this.LOGGER = Logger.getLogger(forClass.getName());
    }
    private final Logger LOGGER;

    private void log9(Level level, String format, Object... params) {
        if (LOGGER.isLoggable(level)) {
            var s = String.format(format, params);
            LOGGER.log(level, s);
        }
    }

    @Override
    public void log(int level, String format, Object... params) {
        switch (level) {
            case LoggerJDK.TRACE:
                log9(Level.FINEST, format, params);
                break;
            case LoggerJDK.DEBUG:
                log9(Level.FINER, format, params);
                break;
            case LoggerJDK.INFO:
                log9(Level.INFO, format, params);
                break;
            case LoggerJDK.WARNING:
                log9(Level.WARNING, format, params);
                break;
            case LoggerJDK.ERROR:
                log9(Level.SEVERE, format, params);
                break;
        }
    }
}
