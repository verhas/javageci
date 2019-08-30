package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.log.Logger;
import javax0.geci.log.LoggerFactory;

import java.io.*;
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
 */
public class Tracer implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger();
    private static final String DEFAULT_TAG = "log";
    private static Tracer root;
    private static Tracer current;
    private static Tracer last;
    private static final Tracer FAKE = new Tracer(null, null, null, null);

    private final Tracer parent;
    private final ArrayList<Tracer> children = new ArrayList<>();
    private String message;
    private final String tag;
    private final String cData;

    private Tracer(Tracer parent, String message, String tag, String cData) {
        this.parent = parent;
        this.message = message;
        this.tag = tag;
        this.cData = cData;
    }

    public static void on() {
        root = new Tracer(null, "tracer root", "trace", null);
        current = root;
        last = root;
    }

    public static void off() {
        root = null;
        current = root;
        last = root;
    }

    private static Tracer walkUpTo(final String tag) {
        var walk = last;
        while (walk != null && !walk.tag.equals(tag)) {
            walk = walk.parent;
        }
        if( walk == null ){
            final var e = new GeciException("Walking upward in trace there is no tag '"+tag+"'");
            log(e);
        }
        return walk;
    }

    public static void prepend(final String tag, final String msg) {
        if (last == null) return;
        final var my = walkUpTo(tag);
        if (my != null) {
            my.message = msg + my.message;
        }
    }

    public static void append(final String tag, final String msg) {
        if (last == null) return;
        final var my = walkUpTo(tag);
        if (my != null) {
            my.message = my.message + msg;
        }
    }

    public static void prepend(final String msg) {
        if (last == null) return;
        last.message = msg + last.message;
    }

    public static void append(final String msg) {
        if (last == null) return;
        last.message = last.message + msg;
    }

    public static void log(final String msg) {
        log(DEFAULT_TAG, msg);
    }

    public static void log(final String tag, final String msg) {
        log(tag, msg, null);
    }

    public static void log(final String tag, final String msg, String cData) {
        if (root == null) return;
        current.children.add(last = new Tracer(current, msg, tag, cData));
    }

    public static Tracer push(String msg) {
        return push(DEFAULT_TAG, msg);
    }

    public static Tracer push(String tag, String msg) {
        if (root == null) return FAKE;
        final var actual = current;
        last = new Tracer(current, msg, tag, null);
        current.children.add(last);
        current = last;
        return actual;
    }

    public static void pop() {
        if (root == null) return;
        if (current.parent != null) {
            current = current.parent;
        } else {
            final var e = new GeciException("Too many Tracer.pop() calls");
            log(e);
        }
    }

    public static void pop(Tracer actual) {
        if (root == null) return;
        current = actual;
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
        final String messageTag = node.message != null ? " msg=\"" + escape(node.message) + "\"" : "";
        if (node.children.isEmpty()) {
            if (node.cData == null) {
                sb.append(" ".repeat(tab)).append("<" + node.tag).append(messageTag).append("/>").append("\n");
            } else {
                sb.append(" ".repeat(tab)).append("<" + node.tag).append(messageTag).append(">").append("\n");
                sb.append("<![CDATA[").append(node.cData).append("]]>\n");
                sb.append(" ".repeat(tab)).append("</" + node.tag + ">\n");
            }
        } else {
            sb.append(" ".repeat(tab)).append("<" + node.tag).append(messageTag).append(">").append("\n");
            node.children.forEach(c -> dumpXML(c, sb, tab + 2));
            sb.append(" ".repeat(tab)).append("</" + node.tag + ">\n");
        }
    }

    private static String escape(String s) {
        return s.replace("\"", "&quot;");
    }

    @Override
    public void close() {
        pop(this);
    }

    public static void log(Throwable e){
        try (final var sw = new StringWriter();
             final var pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            log("ERROR", null, sw.toString());
        } catch (IOException ioegnored) {
        }
    }

}
