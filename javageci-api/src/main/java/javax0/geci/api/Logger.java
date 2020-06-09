package javax0.geci.api;

/**
 * <p>Interface that defines logging methods This is the type of the logger returned by the {@link Source#getLogger()}
 * method that the generators are required to use if and when they want to log messages. This special logging supports
 * log messages filtered not only by the conventional way as most of the loggers but also based on the source object
 * parameters. For example logs that are created for sources that were not changed, because all generated source was
 * already there may be suppressed.</p>
 */
public interface Logger {
    void trace(String format, Object... params) ;
    void debug(String format, Object... params) ;
    void info(String format, Object... params);
    void warning(String format, Object... params);
    void error(String format, Object... params) ;
}
