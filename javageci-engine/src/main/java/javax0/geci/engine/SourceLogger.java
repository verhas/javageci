package javax0.geci.engine;

import javax0.geci.api.Generator;
import javax0.geci.api.Logger;

public class SourceLogger implements Logger {
    final Source source;
    final static int TRACE = 1;
    final static int DEBUG = 2;
    final static int INFO = 3;
    final static int WARNING = 4;
    final static int ERROR = 5;
    public SourceLogger(Source source) {
        this.source = source;
    }

    @Override
    public void trace(String format, Object... params) {
        source.logEntries.add(new LogEntry(String.format(format, params), source.currentGenerator, TRACE));
    }

    @Override
    public void debug(String format, Object... params) {
        source.logEntries.add(new LogEntry(String.format(format, params), source.currentGenerator, DEBUG));
    }

    @Override
    public void info(String format, Object... params) {
        source.logEntries.add(new LogEntry(String.format(format, params), source.currentGenerator, INFO));
    }

    @Override
    public void warning(String format, Object... params) {
        source.logEntries.add(new LogEntry(String.format(format, params), source.currentGenerator, WARNING));
    }

    @Override
    public void error(String format, Object... params) {
        source.logEntries.add(new LogEntry(String.format(format, params), source.currentGenerator, ERROR));
    }

    static class LogEntry {
        final String message;
        final Generator generator;
        final int level;

        LogEntry(String message, Generator generator, int level) {
            this.message = message;
            this.generator = generator;
            this.level = level;
        }
    }
}
