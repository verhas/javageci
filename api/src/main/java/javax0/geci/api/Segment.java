package javax0.geci.api;

public interface Segment extends AutoCloseable {
    void write(String s);

    void newline();

    void write_r(String s);

    void write_l(String s);

    default void close() throws Exception {
    }
}
