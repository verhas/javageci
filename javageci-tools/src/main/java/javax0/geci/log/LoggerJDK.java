package javax0.geci.log;

/**
 * <p>This interface declares the basic functionality that the logger facades implement. The two implementations provide
 * the functionality in JAVA8 and in JAVA9 environment.</p>
 */
interface LoggerJDK {

    int TRACE = 1;
    int DEBUG = 2;
    int INFO = 3;
    int WARNING = 4;
    int ERROR = 5;

    void log(int level, String format, Object... params);
}
