package javax0.geci.api;

public interface Logger {
    void trace(String format, Object... params) ;
    void debug(String format, Object... params) ;
    void info(String format, Object... params);
    void warning(String format, Object... params);
    void error(String format, Object... params) ;
}
