package javax0.geci.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>The JVM8 compatible implementation of the {@link LoggerJDK} interface</p>
 *
 * <p>The name of the logger could be {@code LoggerJDK8} and it would be consistent with the name {@link LoggerJDK9} and
 * with the name of the interface {@link LoggerJDK}. The logging does not depend on the actual VM level. It depends on
 * the classes provided by the JDK. That is the reason why the interface and {@link LoggerJDK9} has JDK in the names. At
 * the same time when we compile using the profile {@code JVM8} it generates code for the Java 8 VM. It has nothing to
 * do with the JDK. As a matter of fact the compilation, when executed by Java 9+ compiler will no realize when Java 9
 * or later JDK classes are used. This is the reason why the project has to be compiled using a genuine Java 8 compiler.
 * The name {@link LoggerJVM8} refers to the fact that this is the implementation, which is used when the Maven
 * compilation profile {@code JVM8} is active.</p>
 */
class LoggerJVM8 implements LoggerJDK {
    /**
     * <p>A factory that creates a new instance of this class initializing the logger for the parameter class. This
     * method is invoked from the class {@link Logger} via reflection.</p>
     *
     * @param forClass the class for which the logger is needed
     * @return the new instance
     */
    static LoggerJDK factory(Class<?> forClass) {
        return new LoggerJVM8(forClass);
    }

    LoggerJVM8(Class<?> forClass) {
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
