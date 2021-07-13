package javax0.geci.log;

import static java.lang.String.format;
import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.TRACE;
import static java.lang.System.Logger.Level.WARNING;

/**
 * <p>A logger proxy that is delegating all calls to the JDK9+ system logging.
 */
public class Logger implements javax0.geci.api.Logger {

    private final System.Logger LOGGER;

    public Logger(Class<?> forClass) {
        this.LOGGER = System.getLogger(forClass.getName());
    }

    @Override
    public void trace(String format, Object... params) {
        LOGGER.log(TRACE, format(format, params));
    }

    @Override
    public void debug(String format, Object... params) {
        LOGGER.log(DEBUG, format(format, params));
    }

    @Override
    public void info(String format, Object... params) {
        LOGGER.log(INFO, format(format, params));
    }

    @Override
    public void warning(String format, Object... params) {
        LOGGER.log(WARNING, format(format, params));
    }

    @Override
    public void error(String format, Object... params) {
        LOGGER.log(ERROR, format(format, params));
    }
}
