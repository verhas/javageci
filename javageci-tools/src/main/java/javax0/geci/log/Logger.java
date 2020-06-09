package javax0.geci.log;

import java.lang.reflect.Method;
import java.util.function.Function;

public class Logger implements javax0.geci.api.Logger {

    final static Function<Class<?>, LoggerJDK> factory;

    static {
        Function<Class<?>, LoggerJDK> _factory = null;
        try {
            final Method m = Class.forName("javax0.geci.log.LoggerJDK9").getDeclaredMethod("factory", Class.class);
            _factory = convert(m);
        } catch (ClassNotFoundException | NoSuchMethodException ignore) {
        }
        if (_factory == null) {
            try {
                final Method m = Class.forName("javax0.geci.log.LoggerJVM8").getDeclaredMethod("factory", Class.class);
                _factory = convert(m);
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            }
        }
        factory = _factory;
    }

    private static Function<Class<?>, LoggerJDK> convert(Method m) {
        return (c) -> {
            try {
                return (LoggerJDK) m.invoke(null,c);
            } catch (Exception ignore) {
                return null;
            }
        };
    }

    private final LoggerJDK LOGGER;
    public Logger(Class<?> forClass) {
        this.LOGGER = factory.apply(forClass);
    }

    @Override
    public void trace(String format, Object... params) {
        LOGGER.log(LoggerJDK.TRACE, format, params);
    }

    @Override
    public void debug(String format, Object... params) {
        LOGGER.log(LoggerJDK.DEBUG, format, params);
    }

    @Override
    public void info(String format, Object... params) {
        LOGGER.log(LoggerJDK.INFO, format, params);
    }

    @Override
    public void warning(String format, Object... params) {
        LOGGER.log(LoggerJDK.WARNING, format, params);
    }

    @Override
    public void error(String format, Object... params) {
        LOGGER.log(LoggerJDK.ERROR, format, params);
    }
}
