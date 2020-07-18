package javax0.geci.log;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * <p>A logger proxy that is delegating all calls to {@link LoggerJDK9} or to {@link LoggerJVM8} depending on which one
 * is available when the code runs. Note that the compilation is controlled by maven profiles and when the compilation
 * targets Java 8 then the {@link LoggerJDK9} implementation is excluded from the compilation. Because of that the
 * decision to use Java 9 or Java 8 compatible logging must use reflection so that the code can handle the {@code
 * ClassNotFoundException}. To avoid performance issues reflection is used only during class loading time in a {@code
 * static} block.</p>
 */
public class Logger implements javax0.geci.api.Logger {

    /**
     * <p>This variable is initialized in a static block and at the end of the static block it will contains a function
     * that is a factory returning a {@link LoggerJDK} implementation for each class. The implementation it returns is
     * either {@link LoggerJDK9} when it is available or {@link LoggerJVM8} when the Java 9+ logging is not
     * available.</p>
     *
     * <p>The static initializer uses reflection and tries to load the {@link LoggerJDK9} class and in case it fails it
     * does the same with the {@link LoggerJVM8}. This static code uses reflection to find the implementation. The
     * created factory function calls the reflective method {@link Method#invoke(Object, Object...) invoke} to create a
     * new instance of a logger. Therefore it is important that the calling code (it is only internal code in
     * Java::Geci) uses "static loggers". (Loggers that are referenced by {@code static final} fields only and thus are
     * not recreated many times.</p>
     */
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

    /**
     * Convert a {@link Method} object to a {@link Function} that is a logger factory creating a logger for each class.
     *
     * @param m the factory method
     * @return the factory function
     */
    private static Function<Class<?>, LoggerJDK> convert(final Method m) {
        return (aClass) -> {
            try {
                return (LoggerJDK) m.invoke(null,aClass);
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
