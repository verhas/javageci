package javax0.geci.engine;

import javax0.geci.api.Generator;
import javax0.geci.api.Logger;

public class SourceLogger implements Logger {
    final Source source;

    public SourceLogger(Source source) {
        this.source = source;
    }

    @Override
    public void trace(String format, Object... params) {
        source.logEntries.add(new LogEntry(String.format(format, params), source.currentGenerator, System.Logger.Level.TRACE));
    }

    @Override
    public void debug(String format, Object... params) {
        source.logEntries.add(new LogEntry(String.format(format, params), source.currentGenerator, System.Logger.Level.DEBUG));
    }

    @Override
    public void info(String format, Object... params) {
        source.logEntries.add(new LogEntry(String.format(format, params), source.currentGenerator, System.Logger.Level.INFO));
    }

    @Override
    public void warning(String format, Object... params) {
        source.logEntries.add(new LogEntry(String.format(format, params), source.currentGenerator, System.Logger.Level.WARNING));
    }

    @Override
    public void error(String format, Object... params) {
        source.logEntries.add(new LogEntry(String.format(format, params), source.currentGenerator, System.Logger.Level.ERROR));
    }

    static class LogEntry {
        final String message;
        final Generator generator;
        final System.Logger.Level level;

        LogEntry(String message, Generator generator, System.Logger.Level level) {
            this.message = message;
            this.generator = generator;
            this.level = level;
        }
    }
}
