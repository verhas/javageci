package javax0.geci.log;

interface LoggerJDK {

    int TRACE = 1;
    int DEBUG = 2;
    int INFO = 3;
    int WARNING = 4;
    int ERROR = 5;

    void log(int level, String format, Object... params);
}
