package javax0.geci.tools;

import javax0.geci.log.Logger;
import javax0.geci.log.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Tracer implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger();
    private static Tracer root;
    private static Tracer current;
    private static final Tracer FAKE = new Tracer(null, null);

    static {
        on();
    }

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
    }

    public static void off() {
        root = null;
        current = root;
    }

    public static void log(final String msg) {
        if (root == null) return;
        current.children.add(new Tracer(current, msg));
    }

    public static Tracer push(String msg) {
        if (root == null) return FAKE;
        final var down = new Tracer(current, msg);
        current.children.add(down);
        current = down;
        return current;
    }

    public static void pop() {
        if (current == null) return;
        current = current.parent;
    }

    public static void dumpXML(String fileName) throws Exception {
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
