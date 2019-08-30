package javax0.geci.tools;

import javax0.geci.log.Logger;
import javax0.geci.log.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * <p>Tracing the execution of code generation.</p>
 *
 * <p>This utility class provides methods to collect hierarchical trace messages about the execution of the code
 * generation. These messages can be dumped into an XML formatted file after the code generation has finished which is a
 * neat and highly supported format by different code editors to navigate along the hierarchical structure.</p>
 *
 * <p>Note that the public methods of the class are {@code static}. This has the advantage that the callers do not
 * need access to any instance. The drawback is that this approach is not thread safe at all. However, this should
 * be an acceptable limitation because the functionality is to be used only when debugging some configuration that
 * runs during unit test execution. Even if the unit tests are executed parallel, multiple tests the same time, only
 * one of them should use tracing. Why would anyone want to debug multiple unit tests running parallel?</p>
 *
 * <p>The typical use us </p>
 *
 */
public class Tracer implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger();
    private static Tracer root;
    private static Tracer current;
    private static Tracer last;
    private static final Tracer FAKE = new Tracer(null, null);

    private final Tracer parent;
    private final ArrayList<Tracer> children = new ArrayList<>();
    private String message;

    private Tracer(Tracer parent, String message) {
        this.parent = parent;
        this.message = message;
    }

    public static void on() {
        root = new Tracer(null, "tracer root");
        current = root;
        last = root;
    }

    public static void off() {
        root = null;
        current = root;
        last = root;
    }

    public static void append(final String msg) {
        if (last == null) return;
        last .message = last.message + msg;
    }

    public static void log(final String msg) {
        if (root == null) return;
        current.children.add(last = new Tracer(current, msg));
    }

    public static Tracer push(String msg) {
        if (root == null) return FAKE;
        last = new Tracer(current, msg);
        current.children.add(last);
        current = last;
        return current;
    }

    public static void pop() {
        if (current == null) return;
        current = current.parent;
    }

    public static void dumpXML(String fileName) throws IOException {
        if (root == null) return;
        final var sb = new StringBuilder();
        dumpXML(root, sb, 0);
        final var file = new File(fileName);
        try (final var fos = new FileOutputStream(file)) {
            fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void dumpXML(Tracer node, StringBuilder sb, int tab) {
        if (node.children.isEmpty()) {
            sb.append(" ".repeat(tab)).append("<log msg=\"").append(escape(node.message)).append("\"/>").append("\n");
        } else {
            sb.append(" ".repeat(tab)).append("<log msg=\"").append(escape(node.message)).append("\">").append("\n");
            node.children.forEach(c -> dumpXML(c, sb, tab + 2));
            sb.append(" ".repeat(tab)).append("</log>\n");
        }
    }

    private static String escape(String s) {
        return s.replace("\"", "&quot;");
    }

    @Override
    public void close() {
        pop();

    }

}
